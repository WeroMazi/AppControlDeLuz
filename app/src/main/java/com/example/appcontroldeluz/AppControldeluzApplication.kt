package com.example.appcontroldeluz

import android.app.Application
import com.example.appcontroldeluz.data.CacheManager

class AppControldeluzApplication : Application() {
    companion object {
        lateinit var cacheManager: CacheManager
            private set
    }

    override fun onCreate() {
        super.onCreate()
        cacheManager = CacheManager(this)
    }
}
