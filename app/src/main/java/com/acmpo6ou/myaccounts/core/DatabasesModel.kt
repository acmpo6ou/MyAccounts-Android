package com.acmpo6ou.myaccounts.core

import android.os.Build
import androidx.annotation.RequiresApi
import com.macasaet.fernet.*
import java.io.File
import java.security.SecureRandom
import java.util.*
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class Account

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
                    val data: Map<String, Account> = emptyMap()){
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

    fun dumps(data: Map<String, Account>): String{
        return ""
    }

    fun loads(jsonStr: String): Map<String, Account>{
        return mapOf()
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

        // encrypt database
        val token = encryptDatabase(database)
        databaseFile.writeText(token)
    }
}