package com.acmpo6ou.myaccounts.database.create_edit_database

import androidx.lifecycle.MutableLiveData
import com.acmpo6ou.myaccounts.MyApp
import com.acmpo6ou.myaccounts.core.superclass.ErrorViewModel
import com.acmpo6ou.myaccounts.core.superclass.NameErrorModel
import com.acmpo6ou.myaccounts.database.superclass.DbNameModel
import com.acmpo6ou.myaccounts.database.superclass.ValidateDbName
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
open class RenameDatabaseViewModel @Inject constructor(
    override val app: MyApp,
) : NameErrorModel(), DbNameModel, ValidateDbName, ErrorViewModel {

    override val errorMsg = MutableLiveData("")
    override val itemNames get() = app.databases.map { it.name }
    override var databaseIndex by Delegates.notNull<Int>()

    override fun fixName(name: String) = super<DbNameModel>.fixName(name)
    override fun validateName(name: String) =
        super<ValidateDbName>.validateName(name)
    override fun superValidateName(name: String) =
        super<NameErrorModel>.validateName(name)

    open fun savePressed(name: String) {
        try {
            val db = app.databases[databaseIndex]
            val oldName = db.name
            val newName = fixName(name)

            val oldFile = File("${app.SRC_DIR}/$oldName.dba")
            val newFile = File("${app.SRC_DIR}/$newName.dba")

            oldFile.renameTo(newFile)
            db.name = newName
        } catch (e: Exception) {
            e.printStackTrace()
            errorMsg.value = e.toString()
        }
    }
}
