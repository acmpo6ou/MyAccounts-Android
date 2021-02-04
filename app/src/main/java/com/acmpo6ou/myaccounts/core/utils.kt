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

package com.acmpo6ou.myaccounts.core

import kotlin.reflect.KProperty1

/**
 * Helper function to get property from [instance] by [propertyName] string using reflection.
 * @param[instance] instance to get property from.
 * @param[propertyName] string with property name.
 * @return property named [propertyName] got from [instance].
 */
@Suppress("UNCHECKED_CAST")
fun <R> getProperty(instance: Any, propertyName: String): R {
    val property =
        instance::class.members.first { it.name == propertyName } as KProperty1<Any, *>
    return property.get(instance) as R
}

