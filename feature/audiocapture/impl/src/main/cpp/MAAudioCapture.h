//
//  MAAudioCapture.h
//  AudioCapture
//
//  macOS Audio Capture using ScreenCaptureKit
//

#import <Foundation/Foundation.h>
#import <CoreMedia/CoreMedia.h>

// Audio format configuration
typedef struct {
    NSUInteger sampleRate;
    NSUInteger channelCount;
    NSUInteger bitsPerChannel;
} MAAudioFormat;

// Audio buffer data
typedef struct {
    void *data;
    size_t dataSize;
    uint32_t frameCount;
    uint64_t timestampNs;
} MAAudioBuffer;

// Callback for audio data
typedef void (^MAAudioDataCallback)(MAAudioBuffer buffer);

// Audio capture interface
@interface MAAudioCapture : NSObject

// Properties
@property (nonatomic, readonly) BOOL isRunning;
@property (nonatomic, readonly) MAAudioFormat format;
@property (nonatomic, copy, nullable) MAAudioDataCallback audioCallback;

// Control methods
- (BOOL)startCapture;
- (BOOL)stopCapture;

// Get available audio applications
+ (nonnull NSArray<NSDictionary *> *)availableAudioApplications;

@end