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

    /**
     * Used to export database to user defined location.
     *
     * Calls model.exportDatabase() in try-catch block handling all errors. When error
     * occurred calls view.showError() passing through appropriate error details to display
     * dialog about error.
     */
    override fun exportDatabase(location: String) {
        val resources = view.myContext?.resources
        var errorDetails = ""

        try {
            // export database
            exportIndex?.let {
                val name = databases[it].name
                model.exportDatabase(name, location)
            }
            // if there are no errors display snackbar about success
            view.showSuccess()
        }
        // handle all possible errors
        catch (e: NoSuchFileException){
            errorDetails = resources.getString(R.string.export_no_such_file_details)
        }
        catch (e: IOException){
            errorDetails = resources.getString(R.string.io_error)
        }
        catch (e: Exception){
            errorDetails = "${e.javaClass.name} ${e.message}"
        }

        // if there are any errors errorDetails will be filled with appropriate details string
        // if so, display error dialog
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

    /**
     * Calls model.deleteDatabase() in try-catch block handling all errors.
     *
     * @param[i] database index.
     */
    override fun deleteDatabase(i: Int) {
        try {
            model.deleteDatabase(databases[i].name)
            view.notifyRemoved(i)
            view.showSuccess()
        }
        catch (e: Exception){
            val errorTitle = view.myContext.resources
                    .getString(R.string.delete_error_title)
            val errorDetails = "${e.javaClass.name} ${e.message}"
            view.showError(errorTitle, errorDetails)
        }
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

    /**
     * Used to reset database password and in this way 'closing' it.
     *
     * @param[i] - database index.
     */
    override fun closeDatabase(i: Int) {
        databases[i].password = null
        view.notifyChanged(i)
    }

    override fun openDatabase(i: Int) {
        if(databases[i].isOpen){
            val databaseJson = model.dumps(databases[i].data)
            view.startDatabase(databaseJson)
        }
        else {
            view.navigateToOpen(i)
        }
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
        val diskDatabase: Database?
        try {
            diskDatabase = model.openDatabase(actualDatabase)
        }
        catch (e: NoSuchFileException){
            // if database on disk doesn't exist then it definitely
            // differs from the one in memory
            return false
        }
        return actualDatabase.data == diskDatabase.data
    }
}