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

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.fromHtml
import androidx.lifecycle.Observer
import com.acmpo6ou.myaccounts.core.UpdatesViewModel
import com.acmpo6ou.myaccounts.core.utils.NetUtils
import com.acmpo6ou.myaccounts.databinding.UpdatesActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

@AndroidEntryPoint
class UpdatesActivity : AppCompatActivity(), NetUtils {
    private var binding: UpdatesActivityBinding? = null
    val b: UpdatesActivityBinding get() = binding!!

    @Inject
    @ActivityContext
    override lateinit var myContext: Context

    @Inject
    lateinit var downloadManager: DownloadManager

    val viewModel: UpdatesViewModel by viewModels()
    lateinit var updateVersion: String

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

            viewModel.removeOldApk()
            viewModel.downloadUpdate(
                updateVersion, downloadManager,
                Environment.DIRECTORY_DOWNLOADS
            )
        }

        registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        viewModel.changelog.observe(this, changelogObserver)
        viewModel.downloadEnabled.observe(this, downloadEnabledObserver)

        // get update version and changelog and set them on appropriate text views
        val extras = intent?.extras ?: return
        updateVersion = extras.getString("version")!!
        b.updateVersion.text = updateVersion
        viewModel.getChangelog(resources.getString(R.string.failed_to_load_changelog))
    }
}
