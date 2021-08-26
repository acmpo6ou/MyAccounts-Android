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

package com.acmpo6ou.myaccounts

import android.app.Application
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.preference.PreferenceManager
import com.acmpo6ou.myaccounts.database.databases_list.Database
import com.macasaet.fernet.Key

open class MyApp : Application(), LifecycleObserver {
    // list of Databases that is used almost by every fragment and activity
    open var databases = mutableListOf<Database>()

    // cache of cryptography keys generated by deriveKey
    // generation of such a key involves 100 000 iterations which takes a long time,
    // so the keys have to be cached
    var keyCache = mutableMapOf<String, Key>()

    // Used to access resources of MainActivity.
    // Because resources from MainActivity are correctly translated.
    open lateinit var res: Resources

    // Used by MyAccountsBoard service to safely copy and paste password
    var password = ""

    // path to directory that contains src folder
    open val ACCOUNTS_DIR get() = getExternalFilesDir(null)?.path + "/"

    // path to directory that contains databases
    open val SRC_DIR get() = "$ACCOUNTS_DIR/src/"

    lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
    }

    open fun startLockActivity() {
        Intent(this, AccountsActivity::class.java).apply {
            flags = FLAG_ACTIVITY_NEW_TASK
            putExtra("databaseIndex", 0)
            startActivity(this)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    open fun onAppForegrounded() {
        val openedDbs = databases.filter { it.isOpen }
        if (prefs.getBoolean("lock_app", true) && openedDbs.isNotEmpty())
            startLockActivity()
    }
}
