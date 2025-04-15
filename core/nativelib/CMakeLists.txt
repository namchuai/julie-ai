cmake_minimum_required(VERSION 3.10)
project(nativelib CXX)

# Find JNI headers
find_package(JNI REQUIRED)

# Define the shared library
add_library(native SHARED
    src/nativeMain/cpp/lib.cpp          # JNI Wrapper
    src/nativeMain/cpp/nativelib_core.cpp # Core Logic
)

# Include JNI directories
target_include_directories(native PRIVATE ${JNI_INCLUDE_DIRS})

# Set output directory for the library to a standard location
set_target_properties(native PROPERTIES
    LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib # Simpler output path
)

# Ensure the library name has the standard "lib" prefix on macOS/Linux
# set_target_properties(native PROPERTIES PREFIX "")

# Ensure the suffix is .dylib on macOS
if(APPLE)
    set_target_properties(native PROPERTIES SUFFIX ".dylib")
elseif(UNIX AND NOT APPLE)
    set_target_properties(native PROPERTIES SUFFIX ".so")
elseif(WIN32)
    set_target_properties(native PROPERTIES SUFFIX ".dll")
endif() 