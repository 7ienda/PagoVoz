package com.pagovoz.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale
import java.util.UUID

/**
 * Envuelve TextToSpeech para anunciar pagos por voz, incluso con la
 * pantalla bloqueada (TTS funciona en segundo plano sin problema porque
 * no depende de la UI, solo del audio del sistema).
 */
class VoiceAnnouncer(context: Context) {

    private var tts: TextToSpeech? = null
    private var ready = false
    private val queueBeforeReady = mutableListOf<String>()
    private val prefs = PrefsManager(context)

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale("es", "PE")
                ready = true
                queueBeforeReady.forEach { speakInternal(it) }
                queueBeforeReady.clear()
            }
        }
    }

    fun announcePayment(appName: String, amountFormatted: String, senderName: String?) {
        val template = prefs.announceTemplate.ifBlank { "Pago recibido de {app}. {monto}" }
        var text = template
            .replace("{app}", appName)
            .replace("{monto}", amountFormatted)
            .replace("{nombre}", senderName ?: "")
            .replace("  ", " ")
            .trim()

        speak(text)
    }

    fun speak(text: String) {
        if (!prefs.voiceEnabled) return
        if (ready) speakInternal(text) else queueBeforeReady.add(text)
    }

    private fun speakInternal(text: String) {
        tts?.setSpeechRate(prefs.voiceSpeed)
        tts?.setPitch(prefs.voicePitch)
        val utteranceId = UUID.randomUUID().toString()
        tts?.speak(text, TextToSpeech.QUEUE_ADD, null, utteranceId)
    }

    fun setListener(onDone: () -> Unit) {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}
            override fun onDone(utteranceId: String?) { onDone() }
            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {}
        })
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}
