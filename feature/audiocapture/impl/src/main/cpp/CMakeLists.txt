cmake_minimum_required(VERSION 3.14)

project(AudioCaptureJNI C CXX OBJC)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_C_STANDARD 11)

# Enable Objective-C ARC (Automatic Reference Counting)
set(CMAKE_OBJC_FLAGS "-fobjc-arc")

# Set macOS deployment target (ScreenCaptureKit audio capture requires 13.0+)
set(CMAKE_OSX_DEPLOYMENT_TARGET "13.0")

# Find required frameworks
find_library(FOUNDATION_FRAMEWORK Foundation)
find_library(COREMEDIA_FRAMEWORK CoreMedia)
find_library(SCREENCAPTUREKIT_FRAMEWORK ScreenCaptureKit)
find_library(AVFAUDIO_FRAMEWORK AVFAudio)

# Find JNI
find_package(JNI REQUIRED)

# Source files
set(AUDIO_CAPTURE_SOURCES
    MAAudioCapture.m
    MAAudioCapture.h
    audio_capture_bridge.mm
    audio_capture_bridge.h
    audio_capture_jni.cpp
)

# Create the shared library (dylib)
add_library(audiocapture_jni SHARED ${AUDIO_CAPTURE_SOURCES})

# Include directories
target_include_directories(audiocapture_jni PRIVATE
    ${JNI_INCLUDE_DIRS}
    ${CMAKE_CURRENT_SOURCE_DIR}
)

# Link frameworks and libraries
target_link_libraries(audiocapture_jni
    ${FOUNDATION_FRAMEWORK}
    ${COREMEDIA_FRAMEWORK}
    ${SCREENCAPTUREKIT_FRAMEWORK}
    ${AVFAUDIO_FRAMEWORK}
    ${JNI_LIBRARIES}
)

# Compiler definitions
target_compile_definitions(audiocapture_jni PRIVATE
    MACOS_BUILD
)

# Compiler flags for release builds
if(CMAKE_BUILD_TYPE STREQUAL "Release")
    target_compile_options(audiocapture_jni PRIVATE
        -O3
        -DNDEBUG
        -fvisibility=hidden
        -fvisibility-inlines-hidden
    )
endif()

# Set output name and location
set_target_properties(audiocapture_jni PROPERTIES
    OUTPUT_NAME "audiocapture_jni"
    PREFIX "lib"
    SUFFIX ".dylib"
)

# Set the output directory to match the expected location for JNI libraries
set_target_properties(audiocapture_jni PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/../../../desktopMain/resources"
)

# Create resources directory if it doesn't exist
file(MAKE_DIRECTORY "${CMAKE_CURRENT_SOURCE_DIR}/../../../desktopMain/resources")

# Custom target to copy the built library to the expected location
add_custom_command(TARGET audiocapture_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy 
        $<TARGET_FILE:audiocapture_jni>
        "${CMAKE_CURRENT_SOURCE_DIR}/../../../desktopMain/resources/libaudiocapture_jni.dylib"
    COMMENT "Copying audiocapture_jni library to resources directory"
)

# Print build information
message(STATUS "Audio Capture JNI Configuration:")
message(STATUS "  Build type: ${CMAKE_BUILD_TYPE}")
message(STATUS "  Output library: libaudiocapture_jni.dylib")
message(STATUS "  Target platform: macOS ${CMAKE_OSX_DEPLOYMENT_TARGET}")
message(STATUS "  JNI include dirs: ${JNI_INCLUDE_DIRS}")