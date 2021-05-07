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

package com.acmpo6ou.myaccounts.core.superclass

import android.util.Log
import com.acmpo6ou.myaccounts.BuildConfig
import com.acmpo6ou.myaccounts.core.utils.GitHubService
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

abstract class SuperPresenter : SuperPresenterI {
    abstract val view: SuperActivityI

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .build()
    var service: GitHubService = retrofit.create(GitHubService::class.java)

    /**
     * Called when user clicks `Check for updates` in navigation drawer
     * or when auto checking for updates.
     *
     * Uses [service] to get json of latest release from which it extracts app's latest
     * version using regex.
     * If response is unsuccessful displays snackbar about failure to check for updates.
     *
     * @param[isAutoCheck] set this to true when auto checking for updates to avoid displaying
     * of updates snackbars.
     */
    override fun checkUpdatesSelected(isAutoCheck: Boolean) {
        if (!view.isInternetAvailable()) {
            view.noInternetConnection(isAutoCheck)
            return
        }

        service.getLatestRelease().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = response.body()!!.string()
                    val regex = Regex("""name":"v(\d+\.\d+\.\d+)""")
                    val version = regex.find(json)!!.groupValues.last()
                    checkForUpdates(version, isAutoCheck)
                } else {
                    Log.i("APP", response.errorBody()!!.string())
                    view.updatesCheckFailed(isAutoCheck)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                t.printStackTrace()
                view.updatesCheckFailed(isAutoCheck)
            }
        })
    }

    /**
     * Determines whether there are updates available by given [latestVersion].
     *
     * If [latestVersion] is different then currently installed one, then there are updates
     * available, otherwise they aren't.
     * Depending on whether there are updates or not we launch UpdatesActivity or display a
     * snackbar saying that there are no updates.
     *
     * @param[latestVersion] latest app version that is available on github releases.
     * @param[isAutoCheck] set this to true when auto checking for updates to avoid displaying
     * of updates snackbars.
     */
    open fun checkForUpdates(latestVersion: String, isAutoCheck: Boolean = false) {
        if (BuildConfig.VERSION_NAME != latestVersion) {
            view.startUpdatesActivity(latestVersion)
        } else {
            view.noUpdates(isAutoCheck)
        }
    }
}
