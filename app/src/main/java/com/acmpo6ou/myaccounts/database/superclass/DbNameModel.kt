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

package com.acmpo6ou.myaccounts.database.superclass

interface DbNameModel {
    /**
     * Removes all unsupported characters from given database name.
     *
     * Supported characters are lower and upper ASCII letters, digits and .-_()
     * We should clean the name because it is used as a name for database files.
     *
     * @param[name] name to clean.
     * @return cleaned from unsupported characters name.
     */
    fun fixName(name: String): String {
        val supported =
            (('A'..'Z') + ('a'..'z') + ('0'..'9')).joinToString("") + ".-_()"
        return name.filter { it in supported }
    }
}