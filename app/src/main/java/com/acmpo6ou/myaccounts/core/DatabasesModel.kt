package com.acmpo6ou.myaccounts.core

import android.os.Build
import androidx.annotation.RequiresApi
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
 * @property isOpen dynamically returns whether database is open or not, the database is
 * considered open when [password] is not null.
 */
data class Database(val name: String,
                    val password: String? = null,
                    val data: Map<String, Account>? = emptyMap()){
    var isOpen: Boolean = false
        get() = password != null
        private set
}

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
     * @return created fernet key string
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun deriveKey(password: String, salt: ByteArray): String {
        val iterations = 100000
        val derivedKeyLength = 256
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength)
        val secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val key = secretKeyFactory.generateSecret(spec).encoded
        return Base64.getUrlEncoder().encodeToString(key)
    }

    fun createDatabase(name: String, password: String, salt: ByteArray) {
        val databaseFile = File("$SRC_DIR$name.db")
        val saltFile = File("$SRC_DIR$name.bin")

        databaseFile.createNewFile()
        saltFile.createNewFile()

    }

}