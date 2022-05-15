package com.example.swopper.utils

import android.content.Context
import com.example.swopper.database.KEY
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.security.Key
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.*
import javax.crypto.Cipher

fun generateKeys(context: Context?): String {
    val generator = KeyPairGenerator.getInstance("RSA")
    generator.initialize(1024)
    val pair = generator.generateKeyPair()
    val publicKey = Base64.getEncoder().encodeToString(pair.public.encoded)
    val privateKey = Base64.getEncoder().encodeToString(pair.private.encoded)
    save("tpE9SNTekXnc0eW", privateKey, context)
    KEY = privateKey
    return publicKey
}

fun save(filename: String, text: String, context: Context?) {
    val fos: FileOutputStream
    try {
        if (context != null) {
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE)
            fos.write(text.toByteArray());
            fos.close();
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
}

fun load(filename: String, context: Context?): String? {
    val fis: FileInputStream
    try {
        if (context != null) {
            fis = context.openFileInput(filename)
            val bytes = fis.readBytes();
            fis.close();
            return bytes.toString()
        }
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}

private fun getPublicKey(key64: String): Key {
    val data: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
    return KeyFactory.getInstance("RSA").generatePublic(X509EncodedKeySpec(data))
}

private fun getPrivateKey(key64: String): PrivateKey {
    val data: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
    val privateKey = KeyFactory.getInstance("RSA").generatePrivate(PKCS8EncodedKeySpec(data))
    Arrays.fill(data, 0.toByte())
    return privateKey
}

fun encryptMessage(message: String, publicKey: String): String {
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey))
        return Base64.getEncoder().encodeToString(cipher.doFinal(message.toByteArray()))
}

fun decryptMessage(message: String?, privateKey: String): String {
    val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
    cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey))
    return String(cipher.doFinal(Base64.getDecoder().decode(message)))
}