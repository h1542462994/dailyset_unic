package org.tty.dailyset.dailyset_unic.component

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.tty.dailyset.dailyset_unic.util.byte2Hex
import org.tty.dailyset.dailyset_unic.util.hex2Byte
import java.security.SecureRandom
import javax.annotation.PostConstruct
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.spec.SecretKeySpec

@Component
class EncryptProvider {

    @Autowired
    private lateinit var environmentVars: EnvironmentVars

    private lateinit var key: SecretKeySpec

    private val logger = LoggerFactory.getLogger(EncryptProvider::class.java)

    fun aesEncrypt(uid: String, password: String): String? {
        return runCatching {
            // create cipher and initialize it for encryption.
            val cipher = Cipher.getInstance(AES)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            // create content
            val content = "$uid${environmentVars.encryptSalt}$password"
            // encode content
            val encoded = cipher.doFinal(content.toByteArray(Charsets.UTF_8))
            // return hex string
            byte2Hex(encoded)
        }.onFailure {
            logger.error("AES encrypt failed: ${it.message}")
        }.getOrNull()
    }

    fun aesDecrypt(uid: String, encrypted: String): String? {
        return runCatching {
            // create cipher and initialize it for decryption.
            val cipher = Cipher.getInstance(AES)
            cipher.init(Cipher.DECRYPT_MODE, key)
            // decode content
            val decoded = cipher.doFinal(hex2Byte(encrypted))

            // return content
            val content = String(decoded, Charsets.UTF_8)
            // check content
            check(content.startsWith(uid + environmentVars.encryptSalt)) {
                "AES decrypt failed: content not match"
            }
            return content.substring(uid.length + environmentVars.encryptSalt.length)
        }.onFailure {
            logger.error("AES decrypt failed: ${it.message}")
        }.getOrNull()
    }

    @PostConstruct
    fun createEncrypt() {
        // construct key generator aes
        val keyGen = KeyGenerator.getInstance(AES)
        // init the key generator with seed
        val random = SecureRandom.getInstance(SHA1PRNG)
        random.setSeed(environmentVars.encryptAESKey.encodeToByteArray())
        keyGen.init(128, random)
        // generate originalKey
        val originalKey = keyGen.generateKey()
        val raw = originalKey.encoded
        // generate aes key
        val key = SecretKeySpec(raw, AES)
        this.key = key
    }


    companion object {
        private const val AES = "AES"
        private const val SHA1PRNG = "SHA1PRNG"
    }
}