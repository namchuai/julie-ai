cmake_minimum_required(VERSION 3.10)
project(whispercpp_jni CXX)

# --- Find Dependencies ---

# Find JNI headers (Required)
if (ANDROID)
    # For Android NDK builds, JNI headers are typically included automatically
    find_package(JNI)
    if (JNI_FOUND)
        message(STATUS "Found JNI via find_package (Android NDK)")
        set(JNI_INCLUDE_DIRS ${JNI_INCLUDE_DIRS})
    else ()
        message(WARNING "find_package(JNI) failed for Android. Assuming NDK provides jni.h")
        set(JNI_INCLUDE_DIRS "") # Set to empty, rely on toolchain includes
    endif ()
else ()
    # For non-Android builds (like host Desktop), require the standard JNI package
    find_package(JNI REQUIRED)
    message(STATUS "Found JNI via find_package (Host)")
endif ()

# Find Threads (Required by ggml/whisper)
find_package(Threads REQUIRED)

# Add the whisper.cpp source directory
add_subdirectory(src/whisper.cpp)

# Find Accelerate Framework (macOS specific)
if (APPLE)
    find_library(ACCELERATE_FRAMEWORK Accelerate)
    if (NOT ACCELERATE_FRAMEWORK)
        message(WARNING "Accelerate framework not found explicitly, hoping whisper.cpp found it.")
    endif ()
endif ()

# --- Define JNI Shared Library ---

add_library(whisper_jni SHARED
        src/main/cpp/whisper_jni.cpp
        src/main/cpp/whisper_jni_common.cpp
)

# Set include directories for JNI
if (JNI_INCLUDE_DIRS)
    target_include_directories(whisper_jni PRIVATE ${JNI_INCLUDE_DIRS})
endif ()

# Include whisper headers
target_include_directories(whisper_jni PRIVATE
        src/whisper.cpp
        src/whisper.cpp/include
        src/whisper.cpp/ggml/include
)

# Link with whisper and dependencies
target_link_libraries(whisper_jni
        whisper
        ${CMAKE_THREAD_LIBS_INIT}
)

# macOS specific linking
if (APPLE AND ACCELERATE_FRAMEWORK)
    target_link_libraries(whisper_jni ${ACCELERATE_FRAMEWORK})
endif ()

# Android specific settings
if (ANDROID)
    find_library(log-lib log)
    target_link_libraries(whisper_jni ${log-lib})
endif ()

# Set output directory for the library
set_target_properties(whisper_jni PROPERTIES
        LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin"
)

# Also set properties for whisper library to ensure it goes to the same directory
set_target_properties(whisper PROPERTIES
        LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
        RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin"
)

# And for ggml
if (TARGET ggml)
    set_target_properties(ggml PROPERTIES
            LIBRARY_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/lib"
            RUNTIME_OUTPUT_DIRECTORY "${CMAKE_BINARY_DIR}/bin"
    )
endif ()