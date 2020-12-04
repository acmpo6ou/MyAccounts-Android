package com.acmpo6ou.myaccounts.core


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

    }

    override fun openDatabase(i: Int) {

    }

    override fun isDatabaseSaved(i: Int): Boolean{
        val actualDatabase = databases[i]
        val diskDatabase = model.openDatabase(actualDatabase)
        return actualDatabase.data == diskDatabase.data
    }
}