/*
 * Copyright (c) 2020. Kolvakh Bohdan
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

package com.acmpo6ou.myaccounts.core

import com.acmpo6ou.myaccounts.R

/**
 * Contains various methods for business logic of MainActivity.
 */
open class MainPresenter(var view: MainActivityInter): MainPresenterInter {
    var model: MainModelInter = MainModel(view.ACCOUNTS_DIR)

    init{
        // This methods are called on app startup
        fixSrcFolder()
        autocheckForUpdates()
    }

    /**
     * This method is called on app startup, it checks for updates if it's time to.
     */
    fun autocheckForUpdates() {
        if(isTimeToUpdate()) {
            checkUpdatesSelected()
        }
    }

    /**
     * This method is called on app startup, if src folder doesn't exist method will create it.
     * Usually src folder is ` /storage/emulated/0/MyAccounts/src`.
     */
    private fun fixSrcFolder(){}

    /**
     * This method is called when user clicks `Import database` in navigation drawer.
     */
    override fun importSelected() {
        view.importDialog()
    }

    /**
     * This method is called when user clicks `Check for updates` in navigation drawer.
     */
    override fun checkUpdatesSelected() {
        if(checkForUpdates()) {
            view.startUpdatesActivity()
        }
        else{
            view.noUpdates()
        }
    }

    /**
     * This method is called when user clicks `Changelog` in navigation drawer.
     */
    override fun navigateToChangelog() {

    }

    /**
     * This method is called when user clicks `Settings` in navigation drawer.
     */
    override fun navigateToSettings() {

    }

    /**
     * This method is called when user clicks `About` in navigation drawer.
     */
    override fun navigateToAbout() {

    }

    /**
     * This method checks given tar file on validity.
     *
     * It checks whether the tar file has appropriate files, does it have appropriate
     * number of them and so on.
     */
    override fun checkTarFile(location: String) {
        val resources = view.myContext?.resources
        if(model.countFiles(location) != 2) {
            val errorTitle = resources.getString(R.string.import_error_title)
            val errorDetails = resources.getString(R.string.import_2_files)
            view.showError(errorTitle, errorDetails)
        }
    }

    override fun checkForUpdates(): Boolean{
        return false
    }

    /**
     * This method checks whether it's time to check for updates.
     * Application should check for updates only once a day.
     *
     * @return boolean value indicating whether it is time to check for updates.
     */
    override fun isTimeToUpdate(): Boolean {
        return false
    }
}