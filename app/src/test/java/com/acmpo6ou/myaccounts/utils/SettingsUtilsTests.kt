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

class SettingsUtilsTests {
    private lateinit var testActivity: TestActivity
    lateinit var spyActivity: TestActivity

    private lateinit var mockResources: Resources
    private val mockConfig: Configuration = mock()
    private val displayMetrics: DisplayMetrics = mock()
    private val mockWindow: Window = mock()
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
