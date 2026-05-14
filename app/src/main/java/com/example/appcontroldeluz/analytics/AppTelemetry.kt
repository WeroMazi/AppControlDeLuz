package com.example.appcontroldeluz.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics

object AppTelemetry {
    fun logEvent(context: Context, name: String, block: (Bundle.() -> Unit)? = null) {
        runCatching {
            val analytics = FirebaseAnalytics.getInstance(context)
            val params = Bundle().apply { block?.invoke(this) }
            analytics.logEvent(name, params)
        }
    }

    fun recordException(throwable: Throwable, message: String? = null) {
        runCatching {
            if (!message.isNullOrBlank()) {
                FirebaseCrashlytics.getInstance().log(message)
            }
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }
}
