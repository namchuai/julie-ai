package ai.julie.core.fs.di

import org.koin.dsl.module

val fsModule = module {
    // Call the expect function. The actual platform
    // implementation will provide the specific binding.
    bindFileSystem()
}