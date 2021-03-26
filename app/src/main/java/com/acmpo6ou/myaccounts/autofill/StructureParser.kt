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

/**
 * Used to find email, username and password fields that need autofill in the given AssistStructure.
 */
class StructureParser {
    private val emailFields = mutableListOf<AssistStructure.ViewNode>()
    private val usernameFields = mutableListOf<AssistStructure.ViewNode>()
    private val passwordFields = mutableListOf<AssistStructure.ViewNode>()

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
                    emailFields.add(viewNode)
                } else if (View.AUTOFILL_HINT_USERNAME in autofillHints) {
                    usernameFields.add(viewNode)
                } else if (View.AUTOFILL_HINT_PASSWORD in autofillHints) {
                    passwordFields.add(viewNode)
                }
            } else {
                val id = viewNode.idEntry?.toLowerCase() ?: ""
                if ("email" in id) {
                    emailFields.add(viewNode)
                } else if ("username" in id) {
                    usernameFields.add(viewNode)
                } else if ("password" in id) {
                    passwordFields.add(viewNode)
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
     */
    fun traverseStructure(structure: AssistStructure) {
        val windowNodes: List<AssistStructure.WindowNode> =
            structure.run {
                (0 until windowNodeCount).map { getWindowNodeAt(it) }
            }

        windowNodes.forEach { windowNode: AssistStructure.WindowNode ->
            val viewNode: AssistStructure.ViewNode? = windowNode.rootViewNode
            traverseNode(viewNode)
        }
    }
}
