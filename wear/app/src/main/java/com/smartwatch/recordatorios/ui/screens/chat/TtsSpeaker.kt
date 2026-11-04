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
            // Sin datos de voz para la variante del país, al menos el idioma genérico.
            engine?.let {
                val locale = speechLocale()
                if (it.setLanguage(locale) <
                    TextToSpeech.LANG_AVAILABLE
                ) {
                    it.language = Locale.forLanguageTag(locale.language)
                }
            }
            pending?.let { speak(it) }
            pending = null
        }

        private companion object {
            const val UTTERANCE_ID = "reply"
        }
    }

/**
 * Se escucha y se lee en el idioma de la app (español o inglés). En español, sin país, se usa el de
 * Colombia; cualquier otro idioma del reloj cae en español, como los textos de la app.
 */
fun speechLocale(): Locale {
    val current = Locale.getDefault()
    return when {
        current.language == ENGLISH -> current
        current.language == SPANISH && current.country.isNotEmpty() -> current
        else -> Locale.forLanguageTag("es-CO")
    }
}

private const val ENGLISH = "en"
private const val SPANISH = "es"
