package com.smartwatch.recordatorios.data.remote

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/** Guarda los tokens del reloj cifrados con una clave AES del Android Keystore. */
@Singleton
class TokenStore
    @Inject
    constructor(
        @param:ApplicationContext context: Context,
    ) {
        private val prefs = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)
        private val _paired = MutableStateFlow(read(KEY_REFRESH) != null)

        /** true mientras el reloj tenga sesión; el arranque lo usa para elegir la pantalla. */
        val paired: StateFlow<Boolean> = _paired.asStateFlow()

        val accessToken: String? get() = read(KEY_ACCESS)
        val refreshToken: String? get() = read(KEY_REFRESH)

        @Synchronized
        fun save(
            access: String,
            refresh: String,
        ) {
            prefs
                .edit()
                .putString(KEY_ACCESS, encrypt(access))
                .putString(KEY_REFRESH, encrypt(refresh))
                .apply()
            _paired.value = true
        }

        @Synchronized
        fun clear() {
            prefs.edit().clear().apply()
            _paired.value = false
        }

        private fun read(key: String): String? =
            prefs.getString(key, null)?.let { runCatching { decrypt(it) }.getOrNull() }

        private fun secretKey(): SecretKey {
            val store = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            (store.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }
            val spec =
                KeyGenParameterSpec
                    .Builder(KEY_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            return KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
                .apply { init(spec) }
                .generateKey()
        }

        private fun encrypt(plain: String): String {
            val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.ENCRYPT_MODE, secretKey()) }
            val bytes = cipher.iv + cipher.doFinal(plain.toByteArray())
            return Base64.encodeToString(bytes, Base64.NO_WRAP)
        }

        private fun decrypt(encoded: String): String {
            val bytes = Base64.decode(encoded, Base64.NO_WRAP)
            val spec = GCMParameterSpec(TAG_BITS, bytes, 0, IV_BYTES)
            val cipher = Cipher.getInstance(TRANSFORMATION).apply { init(Cipher.DECRYPT_MODE, secretKey(), spec) }
            return String(cipher.doFinal(bytes, IV_BYTES, bytes.size - IV_BYTES))
        }

        private companion object {
            const val FILE = "session"
            const val KEY_ACCESS = "access"
            const val KEY_REFRESH = "refresh"
            const val ANDROID_KEYSTORE = "AndroidKeyStore"
            const val KEY_ALIAS = "session-key"
            const val TRANSFORMATION = "AES/GCM/NoPadding"
            const val IV_BYTES = 12
            const val TAG_BITS = 128
        }
    }
