/*
 * Copyright (c) 2020-2021. Bohdan Kolvakh
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

package com.acmpo6ou.myaccounts.core.utils

import android.app.Activity
import android.content.SharedPreferences
import android.view.WindowManager
import java.util.*

/**
 * Contains helper methods to load app settings.
 * Should be used by all activities.
 */
interface SettingsUtils {
    val activity: Activity
    val prefs: SharedPreferences

    /**
     * Changes activity locale.
     * @param[languageCode] language code to change locale such as `uk` for Ukraine.
     */
    fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val resources = activity.resources
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    /**
     * Loads defined by user settings, such as app locale and screen capture.
     */
    fun loadSettings() {
        // set locale
        val localeCode = prefs.getString("language", "default")
        if (localeCode != "default") setLocale(localeCode!!)

        // block screen capture if needed
        val screenCapture = prefs.getBoolean("block_screen_capture", true)
        if (screenCapture)
            activity.window.setFlags(
                WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE
            )
    }
}
