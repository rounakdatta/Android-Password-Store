package com.zeapo.pwdstore

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.github.ajalt.timberkt.Timber.d
import com.zeapo.pwdstore.utils.BiometricAuthenticator
import com.zeapo.pwdstore.utils.PreferenceKeys
import com.zeapo.pwdstore.utils.sharedPrefs

abstract class BaseActivity : AppCompatActivity() {

    val application by lazy { getApplication() as Application }

    override fun onResume() {
        super.onResume()
        val prefs = sharedPrefs
        d { "1 requiresAuthentication : ${application.requiresAuthentication}  isAuthenticating : ${application.isAuthenticating} " }
        if (application.isAuthenticationEnabled && application.requiresAuthentication && !application.isAuthenticating) {
            val view = findViewById<View>(android.R.id.content)
            view.isVisible = false
            application.isAuthenticating = true
            d { "2 requiresAuthentication : ${application.requiresAuthentication}  isAuthenticating : ${application.isAuthenticating} " }
            BiometricAuthenticator.authenticate(this) {
                view.isVisible = true
                d { "3 requiresAuthentication : ${application.requiresAuthentication}  isAuthenticating : ${application.isAuthenticating} " }
                when (it) {
                    is BiometricAuthenticator.Result.Success -> {
                        application.isAuthenticating = false
                        application.requiresAuthentication = false
                    }
                    is BiometricAuthenticator.Result.HardwareUnavailableOrDisabled -> {
                        prefs.edit { remove(PreferenceKeys.BIOMETRIC_AUTH) }
                        application.isAuthenticating = false
                        application.requiresAuthentication = false
                    }
                    is BiometricAuthenticator.Result.Failure, BiometricAuthenticator.Result.Cancelled -> {
                        application.isAuthenticating = false
                        application.requiresAuthentication = true
                        finishAffinity()
                    }
                }
            }
        }

//        d { "4 requiresAuthentication : ${application.requiresAuthentication}  isAuthenticating : ${application.isAuthenticating} " }
////        if (application.isAuthenticating) {
////            application.isAuthenticating = false
////            application.requiresAuthentication = false
////            d { "5 requiresAuthentication : ${application.requiresAuthentication}  isAuthenticating : ${application.isAuthenticating} " }
////        }
    }

    class ProcessLifecycleObserver(private val application: Application) : DefaultLifecycleObserver {

        override fun onStop(owner: LifecycleOwner) {
            d { "6 requiresAuthentication : ${application.requiresAuthentication}  isAuthenticating : ${application.isAuthenticating} " }
            if (application.isAuthenticating) {
                application.requiresAuthentication = false
                application.isAuthenticating = false
            } else {
                application.requiresAuthentication = true
            }
            d { "7 requiresAuthentication : ${application.requiresAuthentication}  isAuthenticating : ${application.isAuthenticating} " }
            super.onStop(owner)
        }
    }
}

