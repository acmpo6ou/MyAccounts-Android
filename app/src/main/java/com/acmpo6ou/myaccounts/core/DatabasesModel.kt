package com.acmpo6ou.myaccounts.core

import android.os.Build
import androidx.annotation.RequiresApi
import com.macasaet.fernet.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import java.io.File
import java.security.SecureRandom
import java.time.*
import java.time.temporal.TemporalAmount
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Class that represents Account, it stores all account data such
 * as name, password, email, etc.
 *
 * @param[account] account name.
 * @param[name] account username.
 * @param[email] account email.
 * @param[password] account password.
 * @param[date] account date of birth.
 * @param[comment] account comment.
 */
@Serializable
data class Account(
        val account: String,
        val name: String,
        val email: String,
        val password: String,
        val date: String,
        val comment: String,
)

/**
 * Class that represents database of Accounts.
 *
 * @param[name] name of the database.
 * @param[password] password of the database.
 * @param[data] map of account names to corresponding Account instances.
 * @param[salt] salt of the database.
 * @property isOpen dynamically returns whether database is open or not, the database is
 * considered open when [password] is not null.
 */
data class Database(val name: String,
                    val password: String? = null,
                    val salt: ByteArray? = null,
                    var data: Map<String, Account> = emptyMap()){
    var isOpen: Boolean = false
        get() = password != null
        private set
}

/**
 * Class that contains various functions related to database operations such as encrypting,
 * decrypting, deleting and creating databases.
 *
 * @param[SRC_DIR] path to directory that contains databases, default path is Internal Storage.
 */
class DatabasesModel(val SRC_DIR: String = "/storage/emulated/0/"){

    /**
     * This method generates purely random salt for encryption.
     *
     * @return salt for encryption
     */
    fun generateSalt(): ByteArray {
        val random = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }

    /**
     * This method creates fernet key given password and salt.
     *
     * @param[password] key password
     * @param[salt] salt for key
     * @return created fernet key
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun deriveKey(password: String, salt: ByteArray): Key {
        val iterations = 100000
        val derivedKeyLength = 256
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = secretKeyFactory.generateSecret(spec).encoded
        val strKey = Base64.getUrlEncoder().encodeToString(key)
        return Key(strKey)
    }

    /**
     * Method used to serialize database map to json string.
     *
     * @param[data] map to serialize.
     * @return when [data] is empty returns empty string, when [data] is not empty –
     * serialized json string.
     */
    fun dumps(data: Map<String, Account>): String{
        var json = ""
        if (data.isNotEmpty()){
            json = Json.encodeToString(data)
        }
        return json
    }

    /**
     * Used to deserialize json string to database map.
     *
     * @param[jsonStr] json string to deserialize.
     * @return when [jsonStr] is empty returns empty map, when [data] is not empty –
     * deserialized database map.
     */
    fun loads(jsonStr: String): Map<String, Account>{
        var map = mapOf<String, Account>()
        if (jsonStr.isNotEmpty()){
            map = Json.decodeFromString(jsonStr)
        }
        return map
    }

    /**
     * This method is for database serialization and encryption.
     *
     * @param[database] Database instance to encrypt.
     * @return encrypted json string.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun encryptDatabase(database: Database): String{
        val key = deriveKey(database.password!!, database.salt!!)
        val data = dumps(database.data)
        val token = Token.generate(key, data)
        return token.serialise()
    }

    /**
     * Creates .db and .bin files for database given Database instance.
     *
     * @param[database] Database instance from which database name, password and salt are
     * extracted for database files creation.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun createDatabase(database: Database) {
        val name = database.name

        // create salt file
        val saltFile = File("$SRC_DIR$name.bin")
        saltFile.createNewFile()
        saltFile.writeBytes(database.salt!!)

        // create database file
        val databaseFile = File("$SRC_DIR$name.db")
        databaseFile.createNewFile()

        // encrypt and write database to .db file
        val token = encryptDatabase(database)
        databaseFile.writeText(token)
    }

    /**
     * This method deletes .db and .bin files of database given its name.
     *
     * @param[name] name of database to delete.
     */
    fun deleteDatabase(name: String){
        val binFile = File("$SRC_DIR/$name.bin")
        binFile.delete()

        val dbFile = File("$SRC_DIR/$name.db")
        dbFile.delete()
    }

    /**
     * Used to decrypt and deserialize encrypted json string to a database map.
     *
     * @param[string] encrypted json string to decrypt.
     * @param[password] password for decryption.
     * @param[salt] salt for decryption.
     * @return decrypted database map.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun decryptDatabase(string: String, password: String, salt: ByteArray): Map<String, Account> {
        // get key and validator
        val key = deriveKey(password, salt)
        val validator: Validator<String> = object : StringValidator {
            // this checks whether our encrypted json string is expired or not
            // in our app we don't care about expiration so we return Instant.MAX.epochSecond
            @RequiresApi(Build.VERSION_CODES.O)
            override fun getTimeToLive(): TemporalAmount {
                return Duration.ofSeconds(Instant.MAX.epochSecond)
            }
        }

        // decrypt and deserialize string
        val token = Token.fromString(string)
        val decrypted = token.validateAndDecrypt(key, validator)
        return loads(decrypted)
    }

    /**
     * Used to open databases by given Database instance.
     *
     * In particular opening database means reading content of corresponding .db file,
     * decrypting and deserializing it, then assigning deserialized database map to `data`
     * property of given Database.
     *
     * @param[database] Database instance with password, name and salt to open database.
     * @return same Database instance but with `data` property filled with deserialized
     * database map.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun openDatabase(database: Database): Database {
        val jsonStr = File("$SRC_DIR/${database.name}.db").readText()
        val data = decryptDatabase(jsonStr, database.password!!, database.salt!!)
        database.data = data
        return database
    }
}