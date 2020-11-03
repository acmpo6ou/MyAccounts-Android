package com.acmpo6ou.myaccounts

import com.acmpo6ou.myaccounts.core.Database
import com.acmpo6ou.myaccounts.core.DatabasesModel
import com.nhaarman.mockitokotlin2.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.io.File
import java.security.SecureRandom
import kotlin.math.exp

class DatabasesTests {
    @Test
    fun `Database class should have isOpen property set to false when password is null`(){
        // we didn't  pass the password so it will be null by default
        val database = Database("Some name")

        // if password is null then database is closed
        assertFalse(
                "Password of Database is null but isOpen is not false!",
                database.isOpen,
        )
    }

    @Test
    fun `Database class should have isOpen property set to true when password is NOT null`(){
        // we passed the password, so it is not null
        val database = Database("Some name", "Some password")

        // when password is not null database is opened
        assertTrue(
                "Password of Database is NOT null but isOpen is false!",
                database.isOpen,
        )
    }
}

class DatabasesModelTests {
    // this is where DatabasesModel will create delete and edit databases during test
    // /dev/shm/ is a fake in-memory file system
    val SRC_DIR = "/dev/shm/accounts/src/"

    /**
     * This method creates empty src folder in a fake file system, it ensures that
     * directory will be empty.
     */
    @Before
    fun setUpScrFolder(){
        val srcFolder = File(SRC_DIR)

        // here we delete folder if it already exists to ensure that it will be empty as is
        // needed for our tests
        if(srcFolder.exists()){
            srcFolder.deleteRecursively()
        }
        srcFolder.mkdirs()
    }

    /**
     * This is a helper method that will copy our test  databases from sampledata folder to
     * the fake file system.
     *
     * @param[name] name of the database that we want to copy to the fake file system
     */
    fun copyDatabase(name: String ="database"){
        // this are were we want to copy database .bin and .db files
        val binDestination = File("$SRC_DIR$name.bin")
        val dbDestination = File("$SRC_DIR$name.db")

        // this are the database files that we want to copy
        val binFile = File("sampledata/$name.bin")
        val dbFile = File("sampledata/$name.db")

        // here we copy database files to the fake file system
        binFile.copyTo(binDestination)
        dbFile.copyTo(dbDestination)
    }

    @Ignore
    @Test
    fun `createDatabase should create new encrypted database given name and password`(){
        val expectedDb = File("sampledata/main.db").readBytes()
        val expectedBin: ByteArray = File("sampledata/main.bin").readBytes()

        // here we instantiate DatabasesModel and create empty database with it
        val model = DatabasesModel(SRC_DIR)
        model.createDatabase("main", "main", expectedBin)

        val actualDb = File("${SRC_DIR}main.db").readBytes()
        val actualBin = File("${SRC_DIR}main.bin").readBytes()

        assertEquals(
                "createDatabase has created incorrectly encrypted database!",
                expectedDb,
                actualDb,
        )

        assertEquals(
                "createDatabase has created incorrect salt file for database!",
                expectedBin,
                actualBin,
        )
    }
}
