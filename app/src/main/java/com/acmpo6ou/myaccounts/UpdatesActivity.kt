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

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.acmpo6ou.myaccounts.core.NetUtils
import com.acmpo6ou.myaccounts.databinding.UpdatesActivityBinding
import com.acmpo6ou.myaccounts.ui.UpdatesViewModel

class UpdatesActivity : AppCompatActivity(), NetUtils {
    private var binding: UpdatesActivityBinding? = null
    val b: UpdatesActivityBinding get() = binding!!

    lateinit var viewModel: UpdatesViewModel
    lateinit var updateVersion: String
    override lateinit var myContext: Context

    private val changelogObserver = Observer<String> {
        b.changelogText.text = fromHtml(it, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    private val downloadEnabledObserver = Observer<Boolean> {
        b.downloadUpdate.isEnabled = it
    }

    // install update when it is downloaded
    val onComplete = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            viewModel.installUpdate(context)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UpdatesActivityBinding.inflate(layoutInflater)
        setContentView(b.root)
        myContext = this

        // go back when clicking the `Later` button
        b.updateLater.setOnClickListener {
            super.onBackPressed()
        }

        b.downloadUpdate.setOnClickListener {
            if (!isInternetAvailable()) {
                Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            viewModel.removeOldApk()
            viewModel.downloadUpdate(
                updateVersion, downloadManager,
                Environment.DIRECTORY_DOWNLOADS
            )
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        viewModel = ViewModelProvider(this).get(UpdatesViewModel::class.java)

        viewModel.changelog.observe(this, changelogObserver)
        viewModel.downloadEnabled.observe(this, downloadEnabledObserver)

        // get update version and changelog and set them on appropriate text views
        val extras = intent.extras ?: return
        updateVersion = extras.getString("version")!!

        b.updateVersion.text = updateVersion
        viewModel.getChangelog(resources.getString(R.string.failed_to_load_changelog))
    }
}
