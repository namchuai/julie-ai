#include <jni.h>
#include <string>

#ifdef __ANDROID__

#include <android/log.h>

#define LOG_TAG "WhisperJNI"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)
#else
#include <iostream>
#define LOG_TAG "WhisperJNI"
#define LOGI(...) do { fprintf(stdout, "[INFO] " LOG_TAG ": " __VA_ARGS__); fprintf(stdout, "\n"); } while(0)
#define LOGE(...) do { fprintf(stderr, "[ERROR] " LOG_TAG ": " __VA_ARGS__); fprintf(stderr, "\n"); } while(0)
#endif

// JNI OnLoad function - called when the library is loaded
JNIEXPORT jint

JNICALL JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        LOGE("Failed to get JNI environment");
        return JNI_ERR;
    }

    LOGI("WhisperJNI loaded successfully");
    return JNI_VERSION_1_6;
}

// JNI OnUnload function - called when the library is unloaded
JNIEXPORT void JNICALL
JNI_OnUnload(JavaVM
*vm,
void *reserved
) {
LOGI("WhisperJNI unloaded");
}

// Utility function to convert jstring to std::string
std::string jstring_to_string(JNIEnv *env, jstring jstr) {
    if (!jstr) return "";

    const char *cstr = env->GetStringUTFChars(jstr, nullptr);
    if (!cstr) return "";

    std::string result(cstr);
    env->ReleaseStringUTFChars(jstr, cstr);
    return result;
}

// Utility function to check and clear JNI exceptions
bool check_jni_exception(JNIEnv *env) {
    if (env->ExceptionCheck()) {
        env->ExceptionDescribe();
        env->ExceptionClear();
        return true;
    }
    return false;
}