#ifndef AUDIO_CAPTURE_BRIDGE_H
#define AUDIO_CAPTURE_BRIDGE_H

#include <stddef.h>

#ifdef __cplusplus
extern "C" {
#endif

// Opaque pointer to MAAudioCapture instance
typedef void* MAAudioCaptureHandle;

// Audio buffer data structure (C-compatible)
typedef struct {
    void *data;
    size_t dataSize;
    unsigned int frameCount;
    unsigned long long timestampNs;
} AudioBufferData;

// Audio format structure (C-compatible)
typedef struct {
    unsigned int sampleRate;
    unsigned int channelCount;
    unsigned int bitsPerChannel;
} AudioFormatData;

// Audio callback function pointer
typedef void (*AudioDataCallbackFunc)(AudioBufferData buffer, void* userData);

// C interface functions
MAAudioCaptureHandle audio_capture_create(void);
void audio_capture_destroy(MAAudioCaptureHandle handle);
int audio_capture_start(MAAudioCaptureHandle handle);
int audio_capture_stop(MAAudioCaptureHandle handle);
int audio_capture_is_running(MAAudioCaptureHandle handle);
AudioFormatData audio_capture_get_format(MAAudioCaptureHandle handle);
void audio_capture_set_callback(MAAudioCaptureHandle handle, AudioDataCallbackFunc callback, void* userData);
int audio_capture_get_available_applications(char*** appNames, char*** bundleIds, int** pids);
void audio_capture_free_application_list(char** appNames, char** bundleIds, int* pids, int count);

#ifdef __cplusplus
}
#endif

#endif // AUDIO_CAPTURE_BRIDGE_H