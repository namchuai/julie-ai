package ai.julie

import ai.julie.di.appModule
import android.app.Application
import org.koin.core.context.startKoin

class JulieApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            modules(
                listOf(
                    appModule,
                )
            )
        }
    }
}