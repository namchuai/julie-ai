#include <jni.h>
#include <string>
#include <vector>
#include <cstring>
#include "whisper.h"

#ifdef __ANDROID__

#include <android/log.h>

#define LOG_TAG "WhisperJNI"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#include <iostream>
#define LOG_TAG "WhisperJNI"
#define LOGD(...) do { fprintf(stdout, "[DEBUG] " LOG_TAG ": " __VA_ARGS__); fprintf(stdout, "\n"); } while(0)
#define LOGE(...) do { fprintf(stderr, "[ERROR] " LOG_TAG ": " __VA_ARGS__); fprintf(stderr, "\n"); } while(0)
#endif

extern "C" {

// Initialize a whisper context from model file
JNIEXPORT jlong
JNICALL
Java_ai_julie_whisperbinding_NativeMethods_initContext(
        JNIEnv *env,
        jobject /* this */,
        jstring model_path) {

    const char *path = env->GetStringUTFChars(model_path, nullptr);
    if (!path) {
        return 0;
    }

    // Initialize whisper context with default parameters
    struct whisper_context_params cparams = whisper_context_default_params();
    struct whisper_context *ctx = whisper_init_from_file_with_params(path, cparams);

    env->ReleaseStringUTFChars(model_path, path);

    if (!ctx) {
        LOGE("Failed to initialize whisper context");
        return 0;
    }

    return reinterpret_cast<jlong>(ctx);
}

// Free whisper context
JNIEXPORT void JNICALL
Java_ai_julie_whisperbinding_NativeMethods_freeContext(
        JNIEnv * env ,
jobject /* this */,
jlong context ) {

if ( context == 0 ) return ;

struct whisper_context *ctx = reinterpret_cast<struct whisper_context *>(context);
whisper_free(ctx);
}

// Simple transcribe function - we'll expand this later
JNIEXPORT jstring
JNICALL
        Java_ai_julie_whisperbinding_NativeMethods_transcribe(
        JNIEnv * env,
        jobject /* this */,
        jlong
context,
jfloatArray samples,
        jint
n_samples) {

if (context == 0) {
return env->NewStringUTF("Error: Invalid context");
}

struct whisper_context *ctx = reinterpret_cast<struct whisper_context *>(context);

// Get audio samples
jfloat *audio_data = env->GetFloatArrayElements(samples, nullptr);
if (!audio_data) {
return env->NewStringUTF("Error: Failed to get audio data");
}

// Set up whisper parameters with defaults
struct whisper_full_params params = whisper_full_default_params(WHISPER_SAMPLING_GREEDY);
params.
print_progress = false;
params.
print_special = false;
params.
print_realtime = false;
params.
print_timestamps = true;
params.
translate = false;
params.
language = "en";
params.
n_threads = 4;

// Run whisper
int result = whisper_full(ctx, params, audio_data, n_samples);

env->
ReleaseFloatArrayElements(samples, audio_data, JNI_ABORT
);

if (result != 0) {
return env->NewStringUTF("Error: Transcription failed");
}

// Get the transcribed text
std::string text;
const int n_segments = whisper_full_n_segments(ctx);
for (
int i = 0;
i<n_segments;
++i) {
const char *segment_text = whisper_full_get_segment_text(ctx, i);
text +=
segment_text;
}

return env->
NewStringUTF(text
.

c_str()

);
}

// Get whisper.cpp version
JNIEXPORT jstring

JNICALL
Java_ai_julie_whisperbinding_NativeMethods_getWhisperVersion(
        JNIEnv * env,
        jobject /* this */) {

    // Note: whisper.cpp doesn't have a version function, so we'll return a placeholder
    return env->NewStringUTF("1.7.6");
}

} // extern "C"