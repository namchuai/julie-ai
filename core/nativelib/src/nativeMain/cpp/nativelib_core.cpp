#include "nativelib_core.h"

// Define the global string within the core implementation
namespace {
    const std::string global_string_core = "A global String from C++ Core";
}

// Implementation of the core string processing function
std::string core_process_string(const std::string& input_str) {
    return "That is '" + input_str + "' from C++ Core";
}

// Implementation of the function to get the global string
std::string core_get_global_string() {
    return global_string_core;
} 