package com.example.flutter_secure_app

import android.annotation.TargetApi
import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.annotation.NonNull
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import kotlin.random.Random

@TargetApi(Build.VERSION_CODES.M)
class MainActivity: FlutterActivity() {
    @TargetApi(Build.VERSION_CODES.M)
    override fun configureFlutterEngine(@NonNull flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, "example.com/channel").setMethodCallHandler {
                call, result ->
            if(call.method == "encypted") {
                val rand = Random.nextInt(100)
              // result.success(rand)
                val plainText = call.argument<String>("plain_text");
                val alias = "myKeyAlias"

                val plainTextdata=plainText.toString();

                val encryptedText: String = encryptString(alias, plainTextdata)
                println("Encrypted Text: $encryptedText")
               val encryptedData="Encrypted Text: $encryptedText";


                result.success(encryptedText)

            }
           else if(call.method == "decrypt") {
                val rand = Random.nextInt(100)
                // result.success(rand)
                val encypteText = call.argument<String>("encypte_text");
                val alias = "myKeyAlias"
                 var dataValue=encypteText.toString();

                val decryptedText: String = decryptString(alias, dataValue)
                println("Decrypted Text: $decryptedText")
                val DecryptedData="Decrypted Text: $decryptedText";

                result.success(DecryptedData)

            }
            else {
                result.notImplemented()
            }
        }
    }



init {

    val keyStore = KeyStore.getInstance("AndroidKeyStore")
    keyStore.load(null)

// Alias for the key

// Alias for the key
    val alias = "myKeyAlias"

// Check if the key already exists

// Check if the key already exists
    if (!keyStore.containsAlias(alias)) {
        val keyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                alias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build()
        )
        keyGenerator.generateKey()
    }
}
    @Throws(Exception::class)
    fun encryptData(alias: String, data: ByteArray): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val secretKey = (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        // Get the IV (Initialization Vector)
        val iv = cipher.iv

        // Encrypt the data
        val encryption = cipher.doFinal(data)

        // Concatenate IV and encrypted data
        val outputStream = ByteArrayOutputStream()
        outputStream.write(iv)
        outputStream.write(encryption)

        return outputStream.toByteArray()
    }

    @Throws(Exception::class)
    fun decryptData(alias: String, encryptedData: ByteArray): ByteArray {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore.load(null)

        val secretKey = (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        // Extract IV from the encrypted data
        val iv = encryptedData.copyOfRange(0, 12)
        val ciphertext = encryptedData.copyOfRange(12, encryptedData.size)

        val spec = GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

        // Decrypt the data
        return cipher.doFinal(ciphertext)
    }

    fun encryptString(alias: String, plainText: String): String {
        val data = plainText.toByteArray(StandardCharsets.UTF_8)
        val encryptedData = encryptData(alias, data)

        // Convert encrypted byte array to a Base64 string for easy storage
        return Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    fun decryptString(alias: String, encryptedText: String): String {
        // Convert the Base64 string back to a byte array
        val encryptedData = Base64.decode(encryptedText, Base64.DEFAULT)

        // Decrypt the byte array
        val decryptedData = decryptData(alias, encryptedData)

        // Convert decrypted byte array to string
        return String(decryptedData, StandardCharsets.UTF_8)
    }

}
















/*
   companion object {
       const val TRANSFORMATION = "AES/GCM/NoPadding"
       const val ANDROID_KEY_STORE = "AndroidKeyStore"
       const val ALIAS = "MyApp"
       const val TAG = "KeyStoreManager"
   }
   private var keyStore: KeyStore

    init {
        keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)
    }
    @TargetApi(Build.VERSION_CODES.FROYO)
    fun encryptData(text: String): Pair<ByteArray, String>? {
        try {

            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKet(ALIAS))
            val iv = cipher.iv

            val result = cipher.doFinal(text.toByteArray(Charsets.ISO_8859_1))
            val resultIv = Base64.encodeToString(iv, Base64.NO_WRAP)
            println("$TAG encrypted data $result")
            println("$TAG encrypted iv $iv")
           // return  null;
            return if (result != null) {
                Pair(result, resultIv)
            } else {
                null
            }
        } catch (e: Exception) {
            println("$TAG error encryptData"+ e)
            return null
        }
    }

    fun encryptString(text: String): SecuredData? {
        return try {
            val result = encryptData(text)
            if (result != null) {
                SecuredData(result.first.toString(Charsets.ISO_8859_1), result.second)
            } else {
                null
            }
        } catch (e: Exception) {
            println("$TAG error encryptString"+e)
            null
        }
    }

    *//**
    Get pair of encrypted value and iv
     *//*
    @TargetApi(Build.VERSION_CODES.FROYO)
    fun decryptString(encryptedString: String, iv: String): String {
        return try {
            val result = decryptData(
                encryptedString.toByteArray(Charsets.ISO_8859_1),
                Base64.decode(iv, Base64.NO_WRAP)
            )
            result
        } catch (e: java.lang.Exception) {
            println("Error in convert to Base64")
            encryptedString
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun decryptData(encryptedData: ByteArray, iv: ByteArray): String {
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, getSecretKet(ALIAS), spec)
            val result = cipher.doFinal(encryptedData).toString(Charsets.ISO_8859_1)
            println("$TAG decrypted data $result")
            result
        } catch (e: Exception) {

            println("$TAG decryptData error may string was not encrypted"+ e)
            encryptedData.toString()
        }
    }

    private fun getSecretKet(alias: String): Key {

        if (keyStore.containsAlias(alias)) {
            // Try for existing key
            return keyStore.getKey(alias, null)
        } else {
            // Key is not present, create new one.
            val keyGenerator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val kGenerator =
                    KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
                val specs = KeyGenParameterSpec
                    .Builder(
                        alias,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                    )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
                kGenerator.init(specs)
                kGenerator
            } else {
                KeyGenerator.getInstance(ANDROID_KEY_STORE)
            }
            return keyGenerator.generateKey()
        }
    }

   // @Keep
    data class SecuredData(val value: String, val iv: String)*/
