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

package com.acmpo6ou.myaccounts.autofill

import android.app.assist.AssistStructure
import android.view.View
import android.view.autofill.AutofillId
import com.acmpo6ou.myaccounts.database.Account

abstract class AccountValue(val id: AutofillId) {
    lateinit var text: String
    lateinit var presentationText: String

    /**
     * Initializes [text] and [presentationText] properties using given [account].
     *
     * Because instances of AccountValue class will be created when traversing the AssistStructure
     * and at that moment [text] and [presentationText] properties can't be initialized because
     * we don't have the account that contains needed email, username or password.
     */
    abstract fun setTextFrom(account: Account)
}

class EmailValue(id: AutofillId) : AccountValue(id) {
    override fun setTextFrom(account: Account) {
        text = account.email
        presentationText = "${account.accountName} email"
    }
}

class UsernameValue(id: AutofillId) : AccountValue(id) {
    override fun setTextFrom(account: Account) {
        text = account.username
        presentationText = "${account.accountName} username"
    }
}

class PasswordValue(id: AutofillId) : AccountValue(id) {
    override fun setTextFrom(account: Account) {
        text = account.password
        presentationText = "${account.accountName} password"
    }
}

/**
 * Used to find email, username and password fields that need autofill in the given AssistStructure.
 */
class StructureParser {
    private val emailValues = mutableListOf<EmailValue>()
    private val usernameValues = mutableListOf<UsernameValue>()
    private val passwordValues = mutableListOf<PasswordValue>()

    /**
     * Used to traverse nodes of AssistStructure searching for fields that require autofill.
     *
     * This is done by looking at autofill hints or if there are none – field ids.
     * @param[viewNode] root node to traverse.
     */
    private fun traverseNode(viewNode: AssistStructure.ViewNode?) {
        if (viewNode?.className?.contains("EditText") == true) {
            if (viewNode.autofillHints?.isNotEmpty() == true) {
                val autofillHints = viewNode.autofillHints!!
                if (View.AUTOFILL_HINT_EMAIL_ADDRESS in autofillHints) {
                    val value = EmailValue(viewNode.autofillId!!)
                    emailValues.add(value)
                } else if (View.AUTOFILL_HINT_USERNAME in autofillHints) {
                    val value = UsernameValue(viewNode.autofillId!!)
                    usernameValues.add(value)
                } else if (View.AUTOFILL_HINT_PASSWORD in autofillHints) {
                    val value = PasswordValue(viewNode.autofillId!!)
                    passwordValues.add(value)
                }
            } else {
                val id = viewNode.idEntry?.toLowerCase() ?: ""
                if ("email" in id) {
                    val value = EmailValue(viewNode.autofillId!!)
                    emailValues.add(value)
                } else if ("username" in id) {
                    val value = UsernameValue(viewNode.autofillId!!)
                    usernameValues.add(value)
                } else if ("password" in id) {
                    val value = PasswordValue(viewNode.autofillId!!)
                    passwordValues.add(value)
                }
            }
        }

        val children: List<AssistStructure.ViewNode>? =
            viewNode?.run {
                (0 until childCount).map { getChildAt(it) }
            }

        children?.forEach { childNode: AssistStructure.ViewNode ->
            traverseNode(childNode)
        }
    }

    /**
     * Used to traverse [structure] nodes using [traverseNode].
     *
     * Note: this method is completely copied from android documentation,
     * see [https://developer.android.com/guide/topics/text/autofill-services] for more details.
     * @return list of account values that need to be autofilled.
     */
    fun traverseStructure(structure: AssistStructure): MutableList<AccountValue> {
        val windowNodes: List<AssistStructure.WindowNode> =
            structure.run {
                (0 until windowNodeCount).map { getWindowNodeAt(it) }
            }

        windowNodes.forEach { windowNode: AssistStructure.WindowNode ->
            val viewNode: AssistStructure.ViewNode? = windowNode.rootViewNode
            traverseNode(viewNode)
        }

        val values = mutableListOf<AccountValue>()
        listOf(emailValues, usernameValues, passwordValues).forEach {
            // we will autofill only first fields of each type
            if (it.isNotEmpty()) values.add(it.first())
        }
        return values
    }
}
