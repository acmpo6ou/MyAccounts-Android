package com.acmpo6ou.myaccounts.database.create_edit_database

import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.NameErrorModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
open class RenameDatabaseViewModel @Inject constructor(
    override val app: MyApp,
) : NameErrorModel() {
    override val itemNames get() = app.databases.map { it.name }
    var databaseIndex by Delegates.notNull<Int>()

    open fun savePressed(newName: String) {
        val oldName = app.databases[databaseIndex].name
        val oldFile = File("${app.SRC_DIR}/$oldName.dba")
        val newFile = File("${app.SRC_DIR}/$newName.dba")
        oldFile.renameTo(newFile)
    }
}