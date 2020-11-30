package com.acmpo6ou.myaccounts.core


class DatabasesPresenter(
        private val view: DatabaseFragmentInter
): DatabasesPresenterInter {
    override var databases: List<Database> = listOf()
    var exportIndex: Int? = null

    override fun exportSelected(i: Int) {
        exportIndex = i
        view.exportDialog(i)
    }

    override fun exportDatabase(location: String) {

    }

    override fun deleteSelected(i: Int) {
        view.confirmDelete(i)
    }

    override fun deleteDatabase(i: Int) {

    }

    override fun closeSelected(i: Int) {

    }

    override fun editSelected(i: Int) {

    }

    override fun closeDatabase(i: Int) {

    }

    override fun openDatabase(i: Int) {

    }
}