/*
 * Copyright (c) 2020-2022. Bohdan Kolvakh
 * This file is part of MyAccounts.
 *
 * MyAccounts is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyAccounts is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 */

package com.acmpo6ou.myaccounts

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LockActivity : AppCompatActivity() {
    @Inject
    lateinit var app: MyApp

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    private val authCallback = object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            super.onAuthenticationSucceeded(result)
            app.isLocked = false
            this@LockActivity.unlock()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_MyAccounts_NoActionBar)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)
        authenticateUser()
    }

    override fun onBackPressed() {
        // Do not allow going back from LockActivity
    }

    /**
     * Unlocks the app by going back from LockActivity.
     */
    private fun unlock() {
        super.onBackPressed()
    }

    /**
     * Builds and displays biometric authentication dialog.
     */
    fun authenticateUser(view: View? = null) {
        val executor = ContextCompat.getMainExecutor(this)
        biometricPrompt = BiometricPrompt(this, executor, authCallback)

        val APP_NAME = resources.getString(R.string.app_name)
        val SUBTITLE = resources.getString(R.string.app_is_locked)
        val APP_LOCK_TIP = resources.getString(R.string.app_lock_tip)

        promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(APP_NAME)
            .setSubtitle(SUBTITLE)
            .setDescription(APP_LOCK_TIP)
            .setAllowedAuthenticators(BIOMETRIC_STRONG or DEVICE_CREDENTIAL)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
