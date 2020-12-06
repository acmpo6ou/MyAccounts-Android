package com.acmpo6ou.myaccounts.core

import com.acmpo6ou.myaccounts.R
import java.io.IOException


open class DatabasesPresenter(
        private val view: DatabaseFragmentInter,
): DatabasesPresenterInter {
    var model: DatabasesModelInter = DatabasesModel()
    override var databases: List<Database> = listOf()
    var exportIndex: Int? = null

    /**
     * Called when user selects `Export` in database item popup menu.
     *
     * Should save [i] in [exportIndex] as it will be used by exportDatabase to determine
     * what database to export.
     * Also it calls exportDialog to display export dialog where user can chose export
     * location.
     * @param[i] index of database we want to export.
     */
    override fun exportSelected(i: Int) {
        exportIndex = i
        view.exportDialog(i)
    }

    override fun exportDatabase(location: String) {
        val resources = view.myContext?.resources
        var errorDetails = ""
        try {
            exportIndex?.let {
                val name = databases[it].name
                model.exportDatabase(name, location)
            }
            view.showSuccess()
        }
        catch (e: NoSuchFileException){
            errorDetails = resources.getString(R.string.export_no_such_file_details)
        }
        catch (e: IOException){
            errorDetails = resources.getString(R.string.io_error)
        }

        if(errorDetails.isNotEmpty()){
            val errorTitle = resources.getString(R.string.export_error_title)
            view.showError(errorTitle, errorDetails)
        }
    }

    /**
     * Called when user selects `Delete` in database item popup menu.
     *
     * Calls confirmDelete to display a dialog about confirmation of database deletion.
     * @param[i] index of database we want to delete.
     */
    override fun deleteSelected(i: Int) {
        view.confirmDelete(i)
    }

    override fun deleteDatabase(i: Int) {

    }

    /**
     * Called when user selects `Close` in database item popup menu.
     *
     * Using isDatabaseSaved checks whether database we want to close is saved, if it is –
     * calls closeDatabase to close the database, if it's not – calls confirmClose to ask
     * user for confirmation.
     * @param[i] index of database we want to close.
     */
    override fun closeSelected(i: Int) {
        if(isDatabaseSaved(i)) {
            closeDatabase(i)
        }
        else{
            view.confirmClose(i)
        }
    }

    /**
     * Called when user selects `Edit` in database item popup menu.
     *
     * Using navigateToEdit navigates to EditDatabaseFragment passing through serialised
     * database string.
     * @param[i] index of database we want to edit.
     */
    override fun editSelected(i: Int) {
        val database = databases[i]
        val databaseJson = model.dumps(database.data)
        view.navigateToEdit(databaseJson)
    }

    override fun closeDatabase(i: Int) {
        databases[i].password = null
        view.notifyChanged(i)
    }

    override fun openDatabase(i: Int) {

    }

    /**
     * Checks whether given database is saved i.e. the database data that is on the disk
     * is same as the data that is in memory.
     *
     * This method is needed when we want to close database, using isDatabaseSaved we
     * can determine whether it's better to show confirmation dialog about
     * unsaved data to user or not.
     * @param[i] index of database we want to check.
     */
    override fun isDatabaseSaved(i: Int): Boolean{
        val actualDatabase = databases[i]
        val diskDatabase = model.openDatabase(actualDatabase)
        return actualDatabase.data == diskDatabase.data
    }
}