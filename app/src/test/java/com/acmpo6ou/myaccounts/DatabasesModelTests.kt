package com.acmpo6ou.myaccounts

import com.acmpo6ou.myaccounts.core.*
import com.macasaet.fernet.*
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
    private val jsonDatabase =
            "{\"gmail\":{\"account\":\"gmail\",\"name\":\"Tom\",\"email\":"+
            "\"tom@gmail.com\",\"password\":\"123\",\"date\":\"01.01.1990\","+
            "\"comment\":\"My gmail account.\"}}"


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

    /**
     * This is a helper method that simply creates empty database.
     *
     * It creates database with name `main` and password `123`.
     * Also it uses createDatabase method of DatabasesModel class for this.
     */
    private fun createEmptyDatabase(){
        // instantiate empty database with name, password and salt
        val database = Database(
                "main",
                "123",
                salt
        )
        model.createDatabase(database)
    }

    /**
     * This method decrypts given string.
     *
     * @param[string] string to decrypt.
     * @param[password] password for decryption.
     * @param[salt] salt for decryption.
     * @return decrypted string.
     */
    private fun decryptStr(string: String, password: String, salt: ByteArray): String{
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

    private fun encryptStr(map: Map<String, Account>, password: String, salt: ByteArray): String{
        val key = model.deriveKey(password!!, salt!!)
        val data = model.dumps(map)
        val token = Token.generate(key, data)
        return token.serialise()
    }

    private fun setUpDatabaseMap(): Map<String, Account> {
        val account = Account(
                account="gmail",
                name="Tom",
                email="tom@gmail.com",
                password="123",
                date="01.01.1990",
                comment="My gmail account.",
        )
        return mapOf("gmail" to account)
    }

    @Test
    fun `dumps should return empty string when passed empty map`(){
        val dumpStr = model.dumps(mapOf())
        assertTrue(dumpStr.isEmpty())
    }

    @Test
    fun `dumps should return serialized string when passed non empty map`(){
        // create database with account that we will serialize
        val database = setUpDatabaseMap()

        // serialize database and check resulting json string
        val dumpStr = model.dumps(database)
        val expectedStr = jsonDatabase
        assertEquals(
                "Incorrect serialization! dumps method",
                expectedStr,
                dumpStr
        )
    }

    @Test
    fun `loads should return empty map when passed empty string`(){
        val loadMap = model.loads("")
        assertTrue(loadMap.isEmpty())
    }

    @Test
    fun `loads should return non empty map when passed non empty string`(){
        // load database map from json string
        val map = model.loads(jsonDatabase)

        // get database map that we expect
        val expectedMap = setUpDatabaseMap()

        assertEquals(
                "Incorrect deserialization! loads method",
                expectedMap,
                map,
        )
    }
    @Test
    fun `encryptDatabase should return encrypted json string when given Database`(){
        // get database map
        val dataMap = setUpDatabaseMap()

        // create database
        val database = Database(
                "somedata",
                "some password",
                salt,
                dataMap
        )

        // get encrypted json string
        val jsonStr = model.encryptDatabase(database)

        // here we decrypt the json string using salt and password we defined earlier
        // to check if it were encrypted correctly
        val data = decryptStr(jsonStr, database.password!!, database.salt!!)

        assertEquals(
                "encryptDatabase has returned incorrectly encrypted json string!",
                jsonDatabase,
                data
        )
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

        // here we decrypt data saved to .db file to check that it was encrypted correctly
        val data = decryptStr(String(actualDb), "123", salt)
        assertEquals(
                "createDatabase creates incorrectly encrypted database!",
                "",
                data
        )
    }

    @Test
    fun `deleteDatabase removes db and bin files from disk`(){
        // create empty database so that we can delete it using deleteDatabase
        createEmptyDatabase()

        model.deleteDatabase("main")

        // files that should be deleted
        val binFile = File("$SRC_DIR/main.bin")
        val dbFile = File("$SRC_DIR/main.db")

        assertFalse(
                "deleteDatabase doesn't delete .bin file",
                binFile.exists()
        )
        assertFalse(
                "deleteDatabase doesn't delete .db file",
                dbFile.exists()
        )
    }

    @Test
    fun `decryptDatabase should return decrypted and deserialized map given string`(){
        // encrypt database so we can check how decryptDatabase will decrypt it
        val expectedMap = setUpDatabaseMap()
        val encryptedJson = encryptStr(
                expectedMap,
                "123",
                salt
        )

        val map = model.decryptDatabase(encryptedJson, "123", salt)
        assertEquals(
                "Incorrect decryption! decryptDatabase method",
                expectedMap,
                map
        )
    }

    @Test
    fun `openDatabase should return Database instance with non empty data property`(){
        // here we copy `main` database to the fake file system so that we can open it later
        copyDatabase("main")

        // create corresponding Database instance
        val db = Database(
                "main",
                "123",
                salt
        )

        val actualDatabase = model.openDatabase(db)
        val expectedDatabase = Database(
                "main",
                "123",
                salt,
                setUpDatabaseMap()
        )

        assertEquals(
                "openDatabase returns incorrect database!",
                expectedDatabase,
                actualDatabase
        )
    }

    @Test
    fun `saveDatabase should delete files of old database`(){
        // this database will be deleted by saveDatabase
        val db = Database(
                "test",
                "123",
                salt
        )
        model.createDatabase(db)

        // this database will be created by saveDatabase
        val newDb = Database(
                "test2",
                "321",
                salt.reversedArray(),
        )

        // save newDb deleting db
        model.saveDatabase("test", newDb)

        // check that there is no longer test.db and test.bin files
        val oldDb = File("$SRC_DIR/test.db")
        val oldBin = File("$SRC_DIR/test.bin")

        assertFalse(
                ".db file of old database is not deleted by saveDatabase method!",
                oldDb.exists()
        )
        assertFalse(
                ".bin file of old database is not deleted by saveDatabase method!",
                oldBin.exists()
        )
    }

}
