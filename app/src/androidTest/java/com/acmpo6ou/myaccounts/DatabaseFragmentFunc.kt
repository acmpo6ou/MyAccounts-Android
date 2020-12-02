package com.acmpo6ou.myaccounts

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseFragmentInst {
    lateinit var databaseScenario: FragmentScenario<DatabaseFragment>
    lateinit var presenter: DatabasesPresenterInter

    @Before
    fun setUp(){
        // Create a graphical FragmentScenario for the DatabaseFragment
        databaseScenario = launchFragmentInContainer<DatabaseFragment>(
                themeResId = R.style.Theme_MyAccounts_NoActionBar)

        // mock presenter with fake databases
        val databases = listOf(
                Database("main")
        )
        presenter = mock()
        whenever(presenter.databases).thenReturn(databases)
        databaseScenario.onFragment {
            it.presenter = presenter
        }
    }

    @Test
    fun confirmDelete_should_call_deleteDatabase_when_Yes_is_chosen_in_dialog() {
        // create dialog and call confirmDelete
        databaseScenario.onFragment {
            it.confirmDelete(0)
        }
        // wait for dialog to appear
        onView(isRoot()).perform(waitForView(android.R.id.button1, 5000))

        // chose Yes
        onView(withId(android.R.id.button1)).perform(click())

        // verify that method was called
        verify(presenter).deleteDatabase(0)
    }

    @Test
    fun confirmDelete_should_not_call_deleteDatabase_when_No_is_chosen_in_dialog() {
        // create dialog and call confirmDelete
        databaseScenario.onFragment {
            it.confirmDelete(0)
        }
        // wait for dialog to appear
        onView(isRoot()).perform(waitForView(android.R.id.button2, 5000))

        // chose No
        onView(withId(android.R.id.button2)).perform(click())

        // verify that method was called
        verify(presenter, never()).deleteDatabase(0)
    }

    @Test
    fun confirmClose_should_call_closeDatabase_when_Yes_is_chosen_in_dialog() {
        // create dialog and call confirmClose
        databaseScenario.onFragment {
            it.confirmClose(0)
        }
        // wait for dialog to appear
        onView(isRoot()).perform(waitForView(android.R.id.button1, 5000))

        // chose Yes
        onView(withId(android.R.id.button1)).perform(click())

        // verify that method was called
        verify(presenter).closeDatabase(0)
    }

    @Test
    fun confirmClose_should_not_call_closeDatabase_when_No_is_chosen_in_dialog() {
        // create dialog and call confirmClose
        databaseScenario.onFragment {
            it.confirmClose(0)
        }
        // wait for dialog to appear
        onView(isRoot()).perform(waitForView(android.R.id.button2, 5000))

        // chose No
        onView(withId(android.R.id.button2)).perform(click())

        // verify that method was called
        verify(presenter, never()).closeDatabase(0)
    }

}
