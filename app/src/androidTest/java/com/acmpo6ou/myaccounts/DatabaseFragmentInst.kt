package com.acmpo6ou.myaccounts

import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.acmpo6ou.myaccounts.core.DatabasesPresenterInter
import com.acmpo6ou.myaccounts.ui.DatabaseFragment
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseFragmentInst {

    lateinit var navController: TestNavHostController
    lateinit var databaseScenario: FragmentScenario<DatabaseFragment>

    // get string resources
    val context = InstrumentationRegistry.getInstrumentation().targetContext
    val successMessage = context.resources.getString(R.string.success_message)

    @Before
    fun setUp(){
        // Create a TestNavHostController
        navController = TestNavHostController(
                ApplicationProvider.getApplicationContext())
        navController.setGraph(R.navigation.mobile_navigation)

        // Create a graphical FragmentScenario for the DatabaseFragment
        databaseScenario = launchFragmentInContainer<DatabaseFragment>(
                themeResId = R.style.Theme_MyAccounts_NoActionBar)
    }

    @Test
    fun confirmDelete_should_call_deleteDatabase_when_Yes_is_chosen_in_dialog() {
        // create dialog and call confirmDelete
        val presenter = mock<DatabasesPresenterInter>()
        databaseScenario.onFragment {
            it.presenter = presenter
            it.confirmDelete("main")
        }

        // chose Yes
        onView(withId(android.R.id.button1)).perform(click())

        // verify that method was called
        verify(presenter).deleteDatabase(eq("main"))
    }

    @Test
    fun confirmDelete_should_not_call_deleteDatabase_when_No_is_chosen_in_dialog() {
        // create dialog and call confirmDelete
        val presenter = mock<DatabasesPresenterInter>()
        databaseScenario.onFragment {
            it.presenter = presenter
            it.confirmDelete("main")
        }

        // chose No
        onView(withId(android.R.id.button2)).perform(click())

        // verify that method was called
        verify(presenter, never()).deleteDatabase(eq("main"))
    }
}
