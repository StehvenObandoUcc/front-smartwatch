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
            engine?.language = Locale.getDefault()
            pending?.let { speak(it) }
            pending = null
        }

        private companion object {
            const val UTTERANCE_ID = "reply"
        }
    }
