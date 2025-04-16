#include <jni.h>
#include <string>
#include "nativelib_core.h" // Include the core logic header

// Remove core logic definitions from here
// const char* global_string_cpp = "A global String from C++";
// std::string process_string(const char* input_str) { ... }

extern "C" {
    /*
     * Class:     ai_julie_feature_chat_NativeLibWrapper
     * Method:    strings
     * Signature: (Ljava/lang/String;)Ljava/lang/String;
     */
    JNIEXPORT jstring JNICALL Java_ai_julie_feature_chat_NativeLibWrapper_strings
      (JNIEnv *env, jobject thiz, jstring input) {

        const char *input_chars = env->GetStringUTFChars(input, nullptr);
        if (input_chars == nullptr) {
            return nullptr; // Handle error
        }
        std::string input_std_string(input_chars);
        env->ReleaseStringUTFChars(input, input_chars);

        // Call the core C++ function
        std::string result_str = core_process_string(input_std_string);

        // Convert the result back to a Java String
        return env->NewStringUTF(result_str.c_str());
    }

    /*
     * Class:     ai_julie_feature_chat_NativeLibWrapper
     * Method:    getGlobalString
     * Signature: ()Ljava/lang/String;
     */
    JNIEXPORT jstring JNICALL Java_ai_julie_feature_chat_NativeLibWrapper_getGlobalString
      (JNIEnv *env, jobject thiz) {

        // Call the core C++ function
        std::string result_str = core_get_global_string();

        // Convert the result to a Java String
        return env->NewStringUTF(result_str.c_str());
    }
} 