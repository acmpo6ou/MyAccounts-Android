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

import com.acmpo6ou.myaccounts.BuildConfig
import com.acmpo6ou.myaccounts.R
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Headers

/**
 * Retrofit service used to download latest release version from app's github repository.
 */
interface GitHubService {
    @Headers("Accept: application/json")
    @GET("repos/Acmpo6ou/MyAccounts/releases/latest")
    fun getLatestRelease(): Call<ResponseBody>
}

/**
 * Super class for MainPresenter and AccountsPresenter.
 */
abstract class SuperPresenter : SuperPresenterInter {
    abstract val view: SuperActivityInter

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://api.github.com")
            .build()
    var service: GitHubService = retrofit.create(GitHubService::class.java)

    /**
     * This method is called when user clicks `Check for updates` in navigation drawer.
     *
     * Uses [service] to get json of latest release from which it extracts app's latest
     * version using regex.
     * If response is unsuccessful display snackbar about failure to check for updates.
     */
    override fun checkUpdatesSelected(){
        service.getLatestRelease().enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val json = response.body()!!.string()
                    val regex = Regex("""name":"(v\d+\.\d+\.\d+)""")
                    val version = regex.find(json)!!.groupValues.last()
                    checkForUpdates(version)
                }
                else {
                    view.updatesCheckFailed()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                view.updatesCheckFailed()
                t.printStackTrace()
            }
        })
    }

    /**
     * This methods determines whether there are updates available by given [latestVersion].
     *
     * If [latestVersion] is different then currently installed one, then there are updates
     * available, otherwise they aren't.
     * Depending on whether there are updates or not we launch UpdatesActivity or display a
     * snackbar that there are no updates.
     * @param[latestVersion] latest app version that is available on github releases.
     */
    open fun checkForUpdates(latestVersion: String) {
        if(BuildConfig.VERSION_NAME != latestVersion) {
            view.app.latestVersion = latestVersion // save latestVersion for UpdatesActivity
            view.startUpdatesActivity()
        }
        else{
            view.noUpdates()
        }
    }

    // Called when user clicks `Changelog` in navigation drawer.
    override fun navigateToChangelog() = view.navigateTo(R.id.actionChangelog)

    // Called when user clicks `Settings` in navigation drawer.
    override fun navigateToSettings() = view.navigateTo(R.id.actionSettings)

    // Called when user clicks `About` in navigation drawer.
    override fun navigateToAbout() = view.navigateTo(R.id.actionAbout)
}