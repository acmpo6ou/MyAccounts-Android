package com.acmpo6ou.myaccounts

import com.acmpo6ou.myaccounts.core.Database
import org.junit.Assert.*
import org.junit.Test

class DatabasesModelTests {
    @Test
    fun `Database class should have isOpen property set to false when password is empty`(){
        val database = Database("Some name")
        assertFalse(
                "Password of Database is empty but isOpen is not false!",
                database.isOpen,
        )
    }

    @Test
    fun `Database class should have isOpen property set to true when password is NOT empty`(){
        val database = Database("Some name", "Some password")
        assertTrue(
                "Password of Database is NOT empty but isOpen is false!",
                database.isOpen,
        )
    }
}