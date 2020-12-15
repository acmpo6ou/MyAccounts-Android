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

open class MainPresenter(
        var view: MainActivityInter
): MainPresenterInter {

    init{
        fixSrcFolder()
        autocheckForUpdates()
    }

    fun autocheckForUpdates() {
        if(isTimeToUpdate()) {
            checkUpdatesSelected()
        }
    }

    private fun fixSrcFolder(){}

    override fun importSelected() {
        view.importDialog()
    }

    override fun checkUpdatesSelected() {
        if(checkForUpdates()) {
            view.startUpdatesActivity()
        }
        else{
            view.noUpdates()
        }
    }

    override fun navigateToChangelog() {

    }

    override fun navigateToSettings() {

    }

    override fun navigateToAbout() {

    }

    override fun checkTarFile(location: String) {

    }

    override fun checkForUpdates(): Boolean{
        return false
    }

    override fun isTimeToUpdate(): Boolean {
        return false
    }
}