package com.acmpo6ou.myaccounts.core

/** This module contains classes related to DatabasesModel */


class Account{}

/**
 * Class that represents database of Accounts.
 *
 * @param[name] name of the database.
 * @param[password] password of the database.
 * @param[data] map of account names to corresponding Account instances.
 * @property isOpen dynamically returns whether database is open or not, the database is
 * considered open when [password] is not null.
 */
data class Database(val name: String,
                    val password: String? = null,
                    val data: Map<String, Account>? = null){
    var isOpen: Boolean = false
        get() = password != null
        private set
}