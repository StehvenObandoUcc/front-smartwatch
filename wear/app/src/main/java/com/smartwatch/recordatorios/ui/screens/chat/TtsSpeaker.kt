package com.smartwatch.recordatorios.ui.screens.chat

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsSpeaker
    @Inject
    constructor(
        @param:ApplicationContext private val context: Context,
    ) : TextSpeaker {
        private var engine: TextToSpeech? = null
        private var ready = false
        private var pending: String? = null

        override fun speak(text: String) {
            val current = engine
            if (current != null && ready) {
                current.speak(text, TextToSpeech.QUEUE_FLUSH, null, UTTERANCE_ID)
            } else {
                pending = text
                if (current == null) engine = TextToSpeech(context, ::onInit)
            }
        }

        // El motor avisa cuando está listo; entonces lee lo que quedó esperando.
        private fun onInit(status: Int) {
            ready = status == TextToSpeech.SUCCESS
            if (!ready) return
            // Sin datos de voz para la variante del país, al menos español genérico.
            engine?.let { if (it.setLanguage(speechLocale()) < TextToSpeech.LANG_AVAILABLE) it.language = SPANISH }
            pending?.let { speak(it) }
            pending = null
        }

        private companion object {
            const val UTTERANCE_ID = "reply"
        }
    }

private val SPANISH: Locale = Locale.forLanguageTag("es")

/**
 * La app está solo en español: se escucha y se lee en español aunque el reloj esté en otro idioma
 * (si no, el reconocedor espera inglés y la voz lee el español con acento inglés).
 */
fun speechLocale(): Locale =
    Locale.getDefault().takeIf { it.language == SPANISH.language } ?: Locale.forLanguageTag("es-CO")
