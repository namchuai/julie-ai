#include "audio_capture_bridge.h"
#import "MAAudioCapture.h"
#import <Foundation/Foundation.h>
#include <string.h>

// Wrapper structure to hold callback info
typedef struct {
    AudioDataCallbackFunc callback;
    void* userData;
} CallbackWrapper;

MAAudioCaptureHandle audio_capture_create(void) {
    @autoreleasepool {
        MAAudioCapture* capture = [[MAAudioCapture alloc] init];
        return (__bridge_retained void*)capture;
    }
}

void audio_capture_destroy(MAAudioCaptureHandle handle) {
    @autoreleasepool {
        if (handle) {
            MAAudioCapture* capture = (__bridge_transfer MAAudioCapture*)handle;
            [capture stopCapture];
            capture = nil;
        }
    }
}

int audio_capture_start(MAAudioCaptureHandle handle) {
    @autoreleasepool {
        if (handle) {
            MAAudioCapture* capture = (__bridge MAAudioCapture*)handle;
            BOOL result = [capture startCapture];
            return result ? 1 : 0;
        }
        return 0;
    }
}

int audio_capture_stop(MAAudioCaptureHandle handle) {
    @autoreleasepool {
        if (handle) {
            MAAudioCapture* capture = (__bridge MAAudioCapture*)handle;
            BOOL result = [capture stopCapture];
            return result ? 1 : 0;
        }
        return 0;
    }
}

int audio_capture_is_running(MAAudioCaptureHandle handle) {
    @autoreleasepool {
        if (handle) {
            MAAudioCapture* capture = (__bridge MAAudioCapture*)handle;
            return capture.isRunning ? 1 : 0;
        }
        return 0;
    }
}

AudioFormatData audio_capture_get_format(MAAudioCaptureHandle handle) {
    @autoreleasepool {
        AudioFormatData format = {0, 0, 0};
        if (handle) {
            MAAudioCapture* capture = (__bridge MAAudioCapture*)handle;
            MAAudioFormat objcFormat = capture.format;
            format.sampleRate = (unsigned int)objcFormat.sampleRate;
            format.channelCount = (unsigned int)objcFormat.channelCount;
            format.bitsPerChannel = (unsigned int)objcFormat.bitsPerChannel;
        }
        return format;
    }
}

void audio_capture_set_callback(MAAudioCaptureHandle handle, AudioDataCallbackFunc callback, void* userData) {
    @autoreleasepool {
        if (handle && callback) {
            MAAudioCapture* capture = (__bridge MAAudioCapture*)handle;
            
            // Create wrapper to hold callback info
            CallbackWrapper* wrapper = (CallbackWrapper*)malloc(sizeof(CallbackWrapper));
            wrapper->callback = callback;
            wrapper->userData = userData;
            
            // Set the Objective-C callback that calls our C function
            capture.audioCallback = ^(MAAudioBuffer buffer) {
                AudioBufferData cBuffer;
                cBuffer.data = buffer.data;
                cBuffer.dataSize = buffer.dataSize;
                cBuffer.frameCount = buffer.frameCount;
                cBuffer.timestampNs = buffer.timestampNs;
                
                wrapper->callback(cBuffer, wrapper->userData);
            };
        }
    }
}

int audio_capture_get_available_applications(char*** appNames, char*** bundleIds, int** pids) {
    @autoreleasepool {
        NSArray<NSDictionary *> *apps = [MAAudioCapture availableAudioApplications];
        NSUInteger count = apps.count;
        
        if (count == 0) {
            *appNames = NULL;
            *bundleIds = NULL;
            *pids = NULL;
            return 0;
        }
        
        // Allocate arrays
        *appNames = (char**)malloc(count * sizeof(char*));
        *bundleIds = (char**)malloc(count * sizeof(char*));
        *pids = (int*)malloc(count * sizeof(int));
        
        for (NSUInteger i = 0; i < count; i++) {
            NSDictionary *app = apps[i];
            
            NSString *name = app[@"name"];
            NSString *bundleId = app[@"bundleId"];
            NSNumber *pid = app[@"pid"];
            
            // Copy strings
            const char *nameStr = [name UTF8String];
            const char *bundleIdStr = [bundleId UTF8String];
            
            (*appNames)[i] = (char*)malloc(strlen(nameStr) + 1);
            strcpy((*appNames)[i], nameStr);
            
            (*bundleIds)[i] = (char*)malloc(strlen(bundleIdStr) + 1);
            strcpy((*bundleIds)[i], bundleIdStr);
            
            (*pids)[i] = [pid intValue];
        }
        
        return (int)count;
    }
}

void audio_capture_free_application_list(char** appNames, char** bundleIds, int* pids, int count) {
    if (appNames) {
        for (int i = 0; i < count; i++) {
            free(appNames[i]);
        }
        free(appNames);
    }
    
    if (bundleIds) {
        for (int i = 0; i < count; i++) {
            free(bundleIds[i]);
        }
        free(bundleIds);
    }
    
    if (pids) {
        free(pids);
    }
}