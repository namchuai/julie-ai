#include <jni.h>
#include <string>
#include <memory>
#include <map>
#include <mutex>
#include "audio_capture_bridge.h"

// Global storage for audio capture instances
static std::map<jlong, MAAudioCaptureHandle> captureInstances;
static std::mutex instanceMutex;
static jlong nextInstanceId = 1;

// Global references for callbacks
static JavaVM* g_jvm = nullptr;
static jclass g_callbackClass = nullptr;
static jmethodID g_onAudioDataMethod = nullptr;

extern "C" {

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    g_jvm = vm;
    JNIEnv *env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }
    
    // Cache the callback class and method
    jclass localCallbackClass = env->FindClass("ai/julie/feature/audiocapture/platform/AudioCaptureCallback");
    if (localCallbackClass == nullptr) {
        return JNI_ERR;
    }
    
    g_callbackClass = (jclass)env->NewGlobalRef(localCallbackClass);
    g_onAudioDataMethod = env->GetMethodID(g_callbackClass, "onAudioData", "([BIJJ)V");
    
    if (g_onAudioDataMethod == nullptr) {
        return JNI_ERR;
    }
    
    return JNI_VERSION_1_6;
}

JNIEXPORT void JNICALL JNI_OnUnload(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void**)&env, JNI_VERSION_1_6) == JNI_OK) {
        if (g_callbackClass) {
            env->DeleteGlobalRef(g_callbackClass);
            g_callbackClass = nullptr;
        }
    }
    g_jvm = nullptr;
}

// Callback data structure
struct CallbackData {
    jobject globalCallback;
    jlong instanceId;
};

// Audio data callback function for the bridge
void audioDataCallback(AudioBufferData buffer, void* userData) {
    CallbackData* data = (CallbackData*)userData;
    JNIEnv *callbackEnv;
    bool detachNeeded = false;
    
    // Get JNI env for this thread
    int envResult = g_jvm->GetEnv((void**)&callbackEnv, JNI_VERSION_1_6);
    if (envResult == JNI_EDETACHED) {
        if (g_jvm->AttachCurrentThread((void**)&callbackEnv, nullptr) != 0) {
            return;
        }
        detachNeeded = true;
    }
    
    // Create Java byte array for audio data
    jbyteArray audioData = callbackEnv->NewByteArray(buffer.dataSize);
    callbackEnv->SetByteArrayRegion(audioData, 0, buffer.dataSize, (const jbyte*)buffer.data);
    
    // Call Java callback
    callbackEnv->CallVoidMethod(data->globalCallback, g_onAudioDataMethod,
                               audioData,
                               (jint)buffer.frameCount,
                               (jlong)buffer.timestampNs,
                               data->instanceId);
    
    // Clean up
    callbackEnv->DeleteLocalRef(audioData);
    
    if (detachNeeded) {
        g_jvm->DetachCurrentThread();
    }
}

// Create a new audio capture instance
JNIEXPORT jlong JNICALL
Java_ai_julie_feature_audiocapture_platform_DesktopAudioCapture_createInstance(JNIEnv *env, jobject thiz, jobject callback) {
    std::lock_guard<std::mutex> lock(instanceMutex);
    
    MAAudioCaptureHandle capture = audio_capture_create();
    jlong instanceId = nextInstanceId++;
    
    // Store global reference to callback
    jobject globalCallback = env->NewGlobalRef(callback);
    
    // Create callback data
    CallbackData* callbackData = new CallbackData();
    callbackData->globalCallback = globalCallback;
    callbackData->instanceId = instanceId;
    
    // Set up the audio callback
    audio_capture_set_callback(capture, audioDataCallback, callbackData);
    
    captureInstances[instanceId] = capture;
    return instanceId;
}

// Start audio capture
JNIEXPORT jboolean JNICALL
Java_ai_julie_feature_audiocapture_platform_DesktopAudioCapture_startCapture(JNIEnv *env, jobject thiz, jlong instanceId) {
    std::lock_guard<std::mutex> lock(instanceMutex);
    
    auto it = captureInstances.find(instanceId);
    if (it == captureInstances.end()) {
        return JNI_FALSE;
    }
    
    MAAudioCaptureHandle capture = it->second;
    int success = audio_capture_start(capture);
    return success ? JNI_TRUE : JNI_FALSE;
}

// Stop audio capture
JNIEXPORT jboolean JNICALL
Java_ai_julie_feature_audiocapture_platform_DesktopAudioCapture_stopCapture(JNIEnv *env, jobject thiz, jlong instanceId) {
    std::lock_guard<std::mutex> lock(instanceMutex);
    
    auto it = captureInstances.find(instanceId);
    if (it == captureInstances.end()) {
        return JNI_FALSE;
    }
    
    MAAudioCaptureHandle capture = it->second;
    int success = audio_capture_stop(capture);
    return success ? JNI_TRUE : JNI_FALSE;
}

// Check if capturing
JNIEXPORT jboolean JNICALL
Java_ai_julie_feature_audiocapture_platform_DesktopAudioCapture_isCapturing(JNIEnv *env, jobject thiz, jlong instanceId) {
    std::lock_guard<std::mutex> lock(instanceMutex);
    
    auto it = captureInstances.find(instanceId);
    if (it == captureInstances.end()) {
        return JNI_FALSE;
    }
    
    MAAudioCaptureHandle capture = it->second;
    int running = audio_capture_is_running(capture);
    return running ? JNI_TRUE : JNI_FALSE;
}

// Get audio format
JNIEXPORT jobject JNICALL
Java_ai_julie_feature_audiocapture_platform_DesktopAudioCapture_getAudioFormat(JNIEnv *env, jobject thiz, jlong instanceId) {
    std::lock_guard<std::mutex> lock(instanceMutex);
    
    auto it = captureInstances.find(instanceId);
    if (it == captureInstances.end()) {
        return nullptr;
    }
    
    MAAudioCaptureHandle capture = it->second;
    AudioFormatData format = audio_capture_get_format(capture);
    
    // Create AudioFormat object
    jclass audioFormatClass = env->FindClass("ai/julie/feature/audiocapture/model/AudioFormat");
    jmethodID constructor = env->GetMethodID(audioFormatClass, "<init>", "(III)V");
    
    return env->NewObject(audioFormatClass, constructor,
                         (jint)format.sampleRate,
                         (jint)format.channelCount,
                         (jint)format.bitsPerChannel);
}

// Get available applications
JNIEXPORT jobjectArray JNICALL
Java_ai_julie_feature_audiocapture_platform_DesktopAudioCapture_getAvailableApplications(JNIEnv *env, jobject thiz) {
    char** appNames;
    char** bundleIds;
    int* pids;
    int count = audio_capture_get_available_applications(&appNames, &bundleIds, &pids);
    
    jclass audioAppClass = env->FindClass("ai/julie/feature/audiocapture/model/AudioApplication");
    jmethodID constructor = env->GetMethodID(audioAppClass, "<init>", "(Ljava/lang/String;Ljava/lang/String;I)V");
    
    jobjectArray result = env->NewObjectArray(count, audioAppClass, nullptr);
    
    for (int i = 0; i < count; i++) {
        jstring name = env->NewStringUTF(appNames[i]);
        jstring bundleId = env->NewStringUTF(bundleIds[i]);
        jint pid = pids[i];
        
        jobject audioApp = env->NewObject(audioAppClass, constructor, name, bundleId, pid);
        env->SetObjectArrayElement(result, i, audioApp);
        
        env->DeleteLocalRef(name);
        env->DeleteLocalRef(bundleId);
        env->DeleteLocalRef(audioApp);
    }
    
    // Free the allocated memory
    audio_capture_free_application_list(appNames, bundleIds, pids, count);
    
    return result;
}

// Destroy instance
JNIEXPORT void JNICALL
Java_ai_julie_feature_audiocapture_platform_DesktopAudioCapture_destroyInstance(JNIEnv *env, jobject thiz, jlong instanceId, jobject callback) {
    std::lock_guard<std::mutex> lock(instanceMutex);
    
    auto it = captureInstances.find(instanceId);
    if (it != captureInstances.end()) {
        MAAudioCaptureHandle capture = it->second;
        audio_capture_destroy(capture);
        captureInstances.erase(it);
        
        // Clean up global callback reference
        if (callback) {
            env->DeleteGlobalRef(callback);
        }
    }
}

} // extern "C"