package com.acmpo6ou.myaccounts

import com.acmpo6ou.myaccounts.core.*
import com.macasaet.fernet.*
import com.nhaarman.mockitokotlin2.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.*
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.TemporalAmount

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
    // this is where DatabasesModel will create delete and edit databases during testing
    // /dev/shm/ is a fake in-memory file system
    val SRC_DIR = "/dev/shm/accounts/src/"
    lateinit var model: DatabasesModel
    lateinit var salt: ByteArray

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

    @Before
    fun setUp(){
        model = DatabasesModel(SRC_DIR)
        salt = "0123456789abcdef".toByteArray() // 16 bytes of salt
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
    @Test
    fun `dumps should return empty string when passed empty map`(){
        val dumpStr = model.dumps(mapOf())
        assertTrue(dumpStr.isEmpty())
    }

    @Test
    fun `loads should return empty map when passed empty string`(){
        val loadMap = model.loads("")
        assertTrue(loadMap.isEmpty())
    }

    fun decryptStr(string: String, password: String, salt: ByteArray): String{
        val key = model.deriveKey(password, salt)
        val validator: Validator<String> = object : StringValidator {
            // this checks whether our encrypted json string is expired or not
            // in our app we don't care about expiration so we return Instant.MAX.epochSecond
            override fun getTimeToLive(): TemporalAmount {
                return Duration.ofSeconds(Instant.MAX.epochSecond)
            }
        }
        val token = Token.fromString(string)
        return token.validateAndDecrypt(key, validator)
    }

    @Test
    fun `encryptDatabase should return encrypted json string when given empty Database object`(){
        // create database using name, password and salt
        val database = Database(
                "somedata",
                "some password",
                salt
        )

        // get encrypted json string
        val jsonStr = model.encryptDatabase(database)

        // here we gonna decrypt the json string using salt and password we defined earlier
        // to check if it were encrypted correctly
        val data = decryptStr(jsonStr, database.password!!, database.salt!!)

        assertTrue(
                "encryptDatabase has returned incorrectly encrypted json string!",
                data.isEmpty()
        )
    }

    fun createEmptyDatabase(){
        // instantiate empty database with name, password and salt
        val database = Database(
                "main",
                "123",
                salt
        )
        model.createDatabase(database)
    }

    @Test
    fun `createDatabase should create salt file given Database instance`(){
        createEmptyDatabase()

        // this is a salt file that createDatabase should create for us
        val actualBin = File("$SRC_DIR/main.bin").readBytes()

        assertEquals(
                "createDatabase created incorrect salt file!",
                String(salt),
                String(actualBin)
        )
    }

    @Test
    fun `createDatabase should create database file given Database instance`(){
        createEmptyDatabase()

        // this is a .db file that createDatabase should create for us
        val actualDb = File("$SRC_DIR/main.db").readBytes()

        // here we decrypt data saved to .db file to check whether it was encrypted correctly
        val data = decryptStr(String(actualDb), "123", salt)
    }

}
