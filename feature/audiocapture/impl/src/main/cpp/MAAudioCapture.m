//
//  MAAudioCapture.m
//  AudioCapture
//
//  macOS Audio Capture implementation using ScreenCaptureKit
//

#import "MAAudioCapture.h"
#import <ScreenCaptureKit/ScreenCaptureKit.h>
#import <AVFAudio/AVFAudio.h>

@interface MAAudioCapture () <SCStreamOutput>

@property (nonatomic, strong) SCStream *stream;
@property (nonatomic, strong) SCStreamConfiguration *streamConfig;
@property (nonatomic, strong) dispatch_queue_t audioQueue;
@property (nonatomic, assign) BOOL isRunning;
@property (nonatomic, assign) MAAudioFormat format;

@end

@implementation MAAudioCapture

- (instancetype)init {
    self = [super init];
    if (self) {
        // Create a serial queue for audio processing
        _audioQueue = dispatch_queue_create("com.macoaudio.capture", DISPATCH_QUEUE_SERIAL);
        _isRunning = NO;
        
        // Set fixed audio format
        _format.sampleRate = 48000;     // 48 kHz sample rate
        _format.channelCount = 2;        // Stereo
        _format.bitsPerChannel = 16;     // 16-bit audio
        
        // Initialize stream configuration with fixed format
        [self setupStreamConfiguration];
    }
    return self;
}

- (void)setupStreamConfiguration {
    // Create ScreenCaptureKit stream configuration
    _streamConfig = [[SCStreamConfiguration alloc] init];
    
    // Configure for audio capture
    _streamConfig.capturesAudio = YES;                          // Enable audio capture
    _streamConfig.excludesCurrentProcessAudio = YES;            // Don't capture our own app's audio
    _streamConfig.sampleRate = _format.sampleRate;              // 48 kHz
    _streamConfig.channelCount = _format.channelCount;          // Stereo
    
    // ScreenCaptureKit requires video config even for audio-only
    // Set minimal video configuration (won't actually capture video)
    _streamConfig.width = 16;
    _streamConfig.height = 16;
    _streamConfig.minimumFrameInterval = CMTimeMake(1, 1);      // 1 fps
    _streamConfig.pixelFormat = kCVPixelFormatType_420YpCbCr8BiPlanarVideoRange;
}

- (void)dealloc {
    // Clean up when object is destroyed
    [self stopCapture];
}

- (BOOL)startCapture {
    // Check if already capturing
    if (_isRunning) {
        NSLog(@"Already capturing audio");
        return NO;
    }
    
    __block BOOL success = NO;
    // Create semaphore to wait for async completion
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    // Step 1: Get shareable content (displays, windows, apps)
    [SCShareableContent getShareableContentWithCompletionHandler:^(SCShareableContent *content, NSError *error) {
        if (error) {
            NSLog(@"Error getting shareable content: %@", error.localizedDescription);
            dispatch_semaphore_signal(semaphore);
            return;
        }
        
        // Step 2: Create a content filter for system-wide audio
        SCContentFilter *filter = nil;
        
        if (content.displays.count > 0) {
            // Get the first display (main display)
            SCDisplay *display = content.displays.firstObject;
            
            // Create filter for entire display (captures all system audio)
            NSArray *emptyApps = @[];     // Don't exclude any apps
            NSArray *emptyWindows = @[];  // Don't exclude any windows
            
            filter = [[SCContentFilter alloc] initWithDisplay:display
                                              excludingApplications:emptyApps
                                                  exceptingWindows:emptyWindows];
        } else {
            NSLog(@"No displays found");
            dispatch_semaphore_signal(semaphore);
            return;
        }
        
        // Step 3: Create the stream with our filter and configuration
        self.stream = [[SCStream alloc] initWithFilter:filter
                                         configuration:self.streamConfig
                                              delegate:nil];
        
        // Step 4: Add ourselves as the audio output handler
        NSError *outputError = nil;
        BOOL outputAdded = [self.stream addStreamOutput:self                    // We'll receive callbacks
                                                    type:SCStreamOutputTypeAudio  // Audio data only
                                      sampleHandlerQueue:self.audioQueue         // Use our audio queue
                                                   error:&outputError];
        
        if (!outputAdded) {
            NSLog(@"Failed to add audio output handler: %@", outputError.localizedDescription);
            dispatch_semaphore_signal(semaphore);
            return;
        }
        
        // Step 5: Start the capture
        [self.stream startCaptureWithCompletionHandler:^(NSError *error) {
            if (error) {
                NSLog(@"Failed to start capture: %@", error.localizedDescription);
            } else {
                self.isRunning = YES;
                success = YES;
                NSLog(@"Audio capture started successfully");
            }
            dispatch_semaphore_signal(semaphore);
        }];
    }];
    
    // Wait for the async operation to complete
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return success;
}

- (BOOL)stopCapture {
    // Check if we're actually capturing
    if (!_isRunning || !_stream) {
        return NO;
    }
    
    __block BOOL success = NO;
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    // Stop the capture stream
    [_stream stopCaptureWithCompletionHandler:^(NSError *error) {
        if (error) {
            NSLog(@"Error stopping capture: %@", error.localizedDescription);
        } else {
            success = YES;
            NSLog(@"Audio capture stopped");
        }
        self.isRunning = NO;
        dispatch_semaphore_signal(semaphore);
    }];
    
    // Wait for stop to complete
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    
    // Clean up the stream
    _stream = nil;
    
    return success;
}

+ (NSArray<NSDictionary *> *)availableAudioApplications {
    NSMutableArray *apps = [NSMutableArray array];
    __block NSArray *resultApps = nil;
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    // Get all running applications that can be captured
    [SCShareableContent getShareableContentWithCompletionHandler:^(SCShareableContent *content, NSError *error) {
        if (!error) {
            // Iterate through all running applications
            for (SCRunningApplication *app in content.applications) {
                // Only add apps with valid name and bundle ID
                if (app.applicationName && app.bundleIdentifier) {
                    [apps addObject:@{
                        @"name": app.applicationName,
                        @"bundleId": app.bundleIdentifier,
                        @"pid": @(app.processID)
                    }];
                }
            }
            resultApps = [apps copy];
        } else {
            NSLog(@"Error getting applications: %@", error.localizedDescription);
        }
        dispatch_semaphore_signal(semaphore);
    }];
    
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    return resultApps ?: @[];
}

#pragma mark - SCStreamOutput Protocol

// This method is called whenever new audio data is available
- (void)stream:(SCStream *)stream didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer ofType:(SCStreamOutputType)type {
    // Only process audio samples
    if (type != SCStreamOutputTypeAudio) {
        return;
    }
    
    // Check if the sample buffer is ready
    if (!CMSampleBufferDataIsReady(sampleBuffer)) {
        return;
    }
    
    // Debug: Check the actual audio format
    static BOOL formatLogged = NO;
    if (!formatLogged) {
        CMFormatDescriptionRef formatDesc = CMSampleBufferGetFormatDescription(sampleBuffer);
        if (formatDesc) {
            const AudioStreamBasicDescription *asbd = CMAudioFormatDescriptionGetStreamBasicDescription(formatDesc);
            if (asbd) {
                NSLog(@"Actual audio format from ScreenCaptureKit:");
                NSLog(@"  Sample Rate: %.0f Hz", asbd->mSampleRate);
                NSLog(@"  Channels: %u", asbd->mChannelsPerFrame);
                NSLog(@"  Bits per channel: %u", asbd->mBitsPerChannel);
                NSLog(@"  Bytes per frame: %u", asbd->mBytesPerFrame);
                NSLog(@"  Bytes per packet: %u", asbd->mBytesPerPacket);
                NSLog(@"  Frames per packet: %u", asbd->mFramesPerPacket);
                NSLog(@"  Format ID: %u", asbd->mFormatID);
                NSLog(@"  Format Flags: 0x%X", asbd->mFormatFlags);
                
                // Check if it's float or integer
                if (asbd->mFormatFlags & kAudioFormatFlagIsFloat) {
                    NSLog(@"  Data type: Float");
                } else {
                    NSLog(@"  Data type: Integer");
                }
                
                if (asbd->mFormatFlags & kAudioFormatFlagIsBigEndian) {
                    NSLog(@"  Byte order: Big Endian");
                } else {
                    NSLog(@"  Byte order: Little Endian");
                }
                
                if (asbd->mFormatFlags & kAudioFormatFlagIsNonInterleaved) {
                    NSLog(@"  Layout: Non-interleaved (planar)");
                } else {
                    NSLog(@"  Layout: Interleaved");
                }
            }
        }
        formatLogged = YES;
    }
    
    // Get the audio data block
    CMBlockBufferRef blockBuffer = CMSampleBufferGetDataBuffer(sampleBuffer);
    if (!blockBuffer) {
        return;
    }
    
    // Get pointer to the actual audio data
    size_t lengthAtOffset;
    size_t totalLength;
    char *dataPointer;
    
    OSStatus status = CMBlockBufferGetDataPointer(blockBuffer,
                                                   0,                    // Start offset
                                                   &lengthAtOffset,      // Length at offset (unused)
                                                   &totalLength,         // Total data length
                                                   &dataPointer);        // Pointer to data
    
    if (status != noErr || !dataPointer || totalLength == 0) {
        return;
    }
    
    // Debug: Log buffer info occasionally
    static int bufferCount = 0;
    if (++bufferCount % 100 == 1) {
        NSLog(@"Buffer %d: totalLength=%zu bytes, expected samples=%zu", 
              bufferCount, totalLength, totalLength / sizeof(float));
    }
    
    // Convert non-interleaved float32 to interleaved int16
    // Input: Non-interleaved (planar) 32-bit float from ScreenCaptureKit
    // Output: Interleaved 16-bit PCM for WAV file
    
    float *floatData = (float *)dataPointer;
    size_t totalSamples = totalLength / sizeof(float);  // Total float samples
    size_t framesPerChannel = totalSamples / _format.channelCount;  // Frames per channel
    
    // Allocate buffer for interleaved int16 data
    size_t int16DataSize = totalSamples * sizeof(int16_t);
    int16_t *int16Data = (int16_t *)malloc(int16DataSize);
    if (!int16Data) {
        return;
    }
    
    // Convert non-interleaved float to interleaved int16
    // Input layout: [L0, L1, L2, ..., Ln, R0, R1, R2, ..., Rn]
    // Output layout: [L0, R0, L1, R1, L2, R2, ...]
    
    float *leftChannel = floatData;
    float *rightChannel = floatData + framesPerChannel;
    
    for (size_t i = 0; i < framesPerChannel; i++) {
        // Left channel
        float leftSample = leftChannel[i];
        if (leftSample > 1.0f) leftSample = 1.0f;
        if (leftSample < -1.0f) leftSample = -1.0f;
        int16Data[i * 2] = (int16_t)(leftSample * 32767.0f);
        
        // Right channel
        float rightSample = rightChannel[i];
        if (rightSample > 1.0f) rightSample = 1.0f;
        if (rightSample < -1.0f) rightSample = -1.0f;
        int16Data[i * 2 + 1] = (int16_t)(rightSample * 32767.0f);
    }
    
    // Create audio buffer with converted data
    MAAudioBuffer audioBuffer;
    audioBuffer.data = int16Data;
    audioBuffer.dataSize = int16DataSize;
    
    // Frame count is frames per channel (since we now have interleaved stereo)
    audioBuffer.frameCount = (uint32_t)framesPerChannel;
    
    // Get timestamp
    CMTime presentationTime = CMSampleBufferGetPresentationTimeStamp(sampleBuffer);
    audioBuffer.timestampNs = (uint64_t)(CMTimeGetSeconds(presentationTime) * 1e9);
    
    // Call the callback if one is set
    if (self.audioCallback) {
        MAAudioDataCallback callback = self.audioCallback;
        callback(audioBuffer);
    }
    
    // Clean up the converted data
    free(int16Data);
}

@end