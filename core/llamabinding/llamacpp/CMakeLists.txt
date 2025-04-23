cmake_minimum_required(VERSION 3.10)
project(llamacpp_jni CXX)

# --- Find Dependencies ---

# Find JNI headers (Required)
if(ANDROID)
    # For Android NDK builds, JNI headers are typically included automatically
    # by the toolchain. find_package(JNI) might fail looking for desktop JVM components.
    # We just need to ensure the include path is available later.
    # We can try finding it, but make it NOT required, and rely on NDK providing headers.
    find_package(JNI)
    if (JNI_FOUND)
        message(STATUS "Found JNI via find_package (Android NDK)")
        set(JNI_INCLUDE_DIRS ${JNI_INCLUDE_DIRS})
    else()
        message(WARNING "find_package(JNI) failed for Android. Assuming NDK provides jni.h")
        # NDK typically places jni.h in a path like <ndk>/sysroot/usr/include
        # This path is often added by the toolchain automatically.
        # If linking fails later, we might need to manually find/add include_directories here.
        set(JNI_INCLUDE_DIRS "") # Set to empty, rely on toolchain includes
    endif()
else()
    # For non-Android builds (like host Desktop), require the standard JNI package
    find_package(JNI REQUIRED)
    message(STATUS "Found JNI via find_package (Host)")
endif()

# Find Threads (Required by ggml/llama)
find_package(Threads REQUIRED)

# Add the llama.cpp source directory. This will find its CMakeLists.txt
# and define targets like 'llama', 'ggml', etc.
add_subdirectory(src/llama.cpp)

# Find Accelerate Framework (macOS specific)
# This might be handled internally by llama.cpp's CMake now, but doesn't hurt to check.
if(APPLE)
    find_library(ACCELERATE_FRAMEWORK Accelerate)
    if(NOT ACCELERATE_FRAMEWORK)
        message(WARNING "Accelerate framework not found explicitly, hoping llama.cpp found it.")
    endif()
endif()

# --- Define JNI Shared Library ---

add_library(llama_jni SHARED
    src/main/cpp/llama_jni.cpp
    src/main/cpp/llama_jni_common.cpp
)

# --- Include Directories ---

# Include JNI headers
# Only add explicitly if JNI_INCLUDE_DIRS was found, otherwise assume NDK toolchain handles it
if(JNI_INCLUDE_DIRS)
    target_include_directories(llama_jni PRIVATE ${JNI_INCLUDE_DIRS})
endif()

# Include directories from the llama target (which includes llama.h and ggml.h)
# Using INTERFACE includes from the 'llama' target ensures we get the right paths.
# If 'llama' isn't the correct target name exported by src/llama.cpp/CMakeLists.txt,
# this needs to be adjusted.
target_include_directories(llama_jni PRIVATE $<TARGET_PROPERTY:llama,INTERFACE_INCLUDE_DIRECTORIES>)

# --- Link Libraries ---

# Link the JNI library against the 'llama' target (which should include ggml).
# If 'llama' doesn't correctly link/include ggml targets, we might need to add
# ggml targets explicitly here too (e.g., ggml, ggml_static, ggml-metal).
target_link_libraries(llama_jni PRIVATE
    llama # Link against the main target built from src/llama.cpp
    Threads::Threads
)

# Add platform-specific system libraries (Check if needed for Android)
if(APPLE)
    # Link Foundation framework for macOS (usually needed for Objective-C runtime bits)
    target_link_libraries(llama_jni PRIVATE "-framework Foundation")
    # Metal/Accelerate should be handled by llama target
elseif(UNIX AND NOT APPLE AND NOT ANDROID)
    # Link dl and m for non-Android Unix (Linux)
    target_link_libraries(llama_jni PRIVATE "-ldl" "-lm")
elseif(ANDROID)
    # Android specific linking:
    # - log: for using __android_log_print etc. (if JNI code uses it)
    # - dl: for dynamic loading if needed
    # - m: for math functions
    # The NDK toolchain might link these automatically depending on API level/config
    target_link_libraries(llama_jni PRIVATE
        log # Link against liblog.so
        dl  # Link against libdl.so
        m   # Link against libm.so
    )
endif()

# --- Output Configuration ---

# Set output directory (AGP might override this, but good practice)
set_target_properties(llama_jni PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
)

# Ensure correct suffix (AGP usually handles this for Android too)
if(NOT ANDROID) # Let AGP handle suffix for Android
    if(APPLE)
        set_target_properties(llama_jni PROPERTIES SUFFIX ".dylib")
    elseif(UNIX AND NOT APPLE)
        set_target_properties(llama_jni PROPERTIES SUFFIX ".so")
    elseif(WIN32)
        set_target_properties(llama_jni PROPERTIES SUFFIX ".dll")
    endif()
endif() 