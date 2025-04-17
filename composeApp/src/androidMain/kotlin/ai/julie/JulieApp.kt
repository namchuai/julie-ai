package ai.julie

import ai.julie.di.appModule
import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class JulieApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@JulieApp)
            modules(
                listOf(
                    appModule,
                )
            )
        }
    }
}
