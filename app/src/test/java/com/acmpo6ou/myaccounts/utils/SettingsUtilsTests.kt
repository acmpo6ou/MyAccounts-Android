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

package com.acmpo6ou.myaccounts.utils

import android.app.Activity
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.util.DisplayMetrics
import android.view.Window
import android.view.WindowManager
import com.acmpo6ou.myaccounts.core.utils.SettingsUtils
import com.github.ivanshafran.sharedpreferencesmock.SPMockBuilder
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import java.util.*

class SettingsUtilsTests {
    private lateinit var testActivity: TestActivity
    lateinit var spyActivity: TestActivity

    private lateinit var mockResources: Resources
    private val mockConfig: Configuration = mock()
    private val displayMetrics: DisplayMetrics = mock()
    private val mockWindow: Window = mock()

    private val languageCode = "uk"
    private val expectedLocale = Locale(languageCode)
    private val secureFlag = WindowManager.LayoutParams.FLAG_SECURE

    @Before
    fun setup() {
        mockResources = mock {
            on { configuration } doReturn mockConfig
            on { displayMetrics } doReturn displayMetrics
        }

        testActivity = TestActivity()
        testActivity.prefs = SPMockBuilder().createSharedPreferences()
        testActivity.activity = mock {
            on { resources } doReturn mockResources
            on { window } doReturn mockWindow
        }
        spyActivity = spy(testActivity)
    }

    @Test
    fun `setLocale should change locale of resources configuration`() {
        testActivity.setLocale(languageCode)
        verify(mockConfig).setLocale(expectedLocale)
    }

    @Test
    fun `setLocale should update configuration of resources`() {
        testActivity.setLocale(languageCode)
        verify(mockResources).updateConfiguration(mockConfig, displayMetrics)
    }

    @Test
    fun `loadSettings should not call setLocale if 'language' setting is set to 'default'`() {
        spyActivity.prefs.edit().putString("language", "default").commit()
        spyActivity.loadSettings()
        verify(spyActivity, never()).setLocale(anyString())
    }

    @Test
    fun `loadSettings should call setLocale if 'language' setting is other then 'default'`() {
        spyActivity.prefs.edit().putString("language", languageCode).commit()
        spyActivity.loadSettings()
        verify(spyActivity).setLocale(languageCode)
    }

    @Test
    fun `loadSettings should block screen capture when 'block_screen_capture' is true`() {
        spyActivity.prefs.edit().putBoolean("block_screen_capture", true).commit()
        testActivity.loadSettings()
        verify(mockWindow).setFlags(secureFlag, secureFlag)
    }

    @Test
    fun `loadSettings should not block screen capture when 'block_screen_capture' is false`() {
        spyActivity.prefs.edit().putBoolean("block_screen_capture", false).commit()
        testActivity.loadSettings()
        verify(mockWindow, never()).setFlags(secureFlag, secureFlag)
    }
}

open class TestActivity : SettingsUtils {
    override lateinit var activity: Activity
    override lateinit var prefs: SharedPreferences
}
