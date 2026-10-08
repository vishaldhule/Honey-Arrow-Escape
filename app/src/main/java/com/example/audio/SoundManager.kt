package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.math.sin

object SoundManager {
    var isSoundEnabled: Boolean = true
    var isMusicEnabled: Boolean = true

    private val scope = CoroutineScope(Dispatchers.Default)
    private const val SAMPLE_RATE = 22050 // Optimized 22kHz for smooth low-latency gaming sound
    private var musicTrack: AudioTrack? = null
    private var sfxTrack: AudioTrack? = null
    private var sfxJob: Job? = null
    private val sfxQueue = ConcurrentLinkedQueue<ShortArray>()

    init {
        initSfxWorker()
    }

    private fun initSfxWorker() {
        sfxJob?.cancel()
        sfxJob = scope.launch {
            val minBufSize = AudioTrack.getMinBufferSize(
                SAMPLE_RATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(4096)

            try {
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(minBufSize * 2)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()

                sfxTrack = track
                track.play()

                while (isActive) {
                    val nextBuffer = sfxQueue.poll()
                    if (nextBuffer != null) {
                        try {
                            track.write(nextBuffer, 0, nextBuffer.size)
                        } catch (_: Exception) {}
                    } else {
                        kotlinx.coroutines.delay(10)
                    }
                }
            } catch (_: Exception) {
                // Audio system fallback
            }
        }
    }

    /**
     * Starts cheerful, smooth background music using a SINGLE looping AudioTrack (0 GC lag!)
     */
    fun startBackgroundMusic() {
        if (!isMusicEnabled) return
        if (musicTrack != null) {
            try {
                musicTrack?.play()
                return
            } catch (_: Exception) {}
        }

        scope.launch {
            try {
                val melody = doubleArrayOf(
                    261.63, 329.63, 392.00, 440.00,
                    392.00, 329.63, 293.66, 261.63,
                    329.63, 392.00, 523.25, 440.00,
                    392.00, 329.63, 293.66, 261.63
                )
                val noteDurationMs = 240
                val samplesPerNote = (SAMPLE_RATE * noteDurationMs) / 1000
                val totalLoopSamples = samplesPerNote * melody.size
                val loopBuffer = ShortArray(totalLoopSamples)

                for (n in melody.indices) {
                    val freq = melody[n]
                    val offset = n * samplesPerNote
                    for (i in 0 until samplesPerNote) {
                        val t = i.toDouble() / SAMPLE_RATE
                        val progress = i.toDouble() / samplesPerNote
                        val envelope = kotlin.math.exp(-progress * 4.5)
                        val sample = sin(2.0 * Math.PI * freq * t) *
                                envelope * Short.MAX_VALUE * 0.08
                        loopBuffer[offset + i] = sample.toInt().toShort()
                    }
                }

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(SAMPLE_RATE)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(loopBuffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(loopBuffer, 0, loopBuffer.size)
                track.setLoopPoints(0, totalLoopSamples, -1) // Infinite smooth loop, zero allocations!
                musicTrack = track
                if (isMusicEnabled) {
                    track.play()
                }
            } catch (_: Exception) {}
        }
    }

    fun stopBackgroundMusic() {
        try {
            musicTrack?.pause()
        } catch (_: Exception) {}
    }

    fun playTap() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 30
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val freq = 560.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val envelope = 1.0 - (i.toDouble() / numSamples)
                val sample = sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.35
                buffer[i] = sample.toInt().toShort()
            }
            enqueueSfx(buffer)
        }
    }

    fun playTick() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 35
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)
            val freq = 880.0

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val envelope = kotlin.math.exp(-progress * 8.0)
                val sample = sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.30
                buffer[i] = sample.toInt().toShort()
            }
            enqueueSfx(buffer)
        }
    }

    fun playLevelSelect() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 65
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val freq = 660.0 + (progress * 220.0)
                val envelope = sin(progress * Math.PI)
                val sample = sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.40
                buffer[i] = sample.toInt().toShort()
            }
            enqueueSfx(buffer)
        }
    }

    fun playEscape(comboStreak: Int = 1) {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 150
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            val pitchMultiplier = when ((comboStreak - 1).coerceIn(0, 6)) {
                0 -> 1.0
                1 -> 1.15
                2 -> 1.25
                3 -> 1.35
                4 -> 1.5
                5 -> 1.7
                else -> 2.0
            }

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val freq = (440.0 + (500.0 * progress)) * pitchMultiplier
                val envelope = sin(progress * Math.PI)
                val sample = sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.45
                buffer[i] = sample.toInt().toShort()
            }
            enqueueSfx(buffer)
        }
    }

    fun playBlocked() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 90
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val envelope = (1.0 - progress)
                val sample = (sin(2.0 * Math.PI * 180.0 * t) + sin(2.0 * Math.PI * 140.0 * t) * 0.5) *
                        envelope * Short.MAX_VALUE * 0.35
                buffer[i] = sample.toInt().toShort()
            }
            enqueueSfx(buffer)
        }
    }

    fun playHint() {
        if (!isSoundEnabled) return
        scope.launch {
            val durationMs = 180
            val numSamples = (SAMPLE_RATE * durationMs) / 1000
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / SAMPLE_RATE
                val progress = i.toDouble() / numSamples
                val freq = 1318.0 + (progress * 400.0)
                val envelope = sin(progress * Math.PI)
                val sample = sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.40
                buffer[i] = sample.toInt().toShort()
            }
            enqueueSfx(buffer)
        }
    }

    fun playCoinReward() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = doubleArrayOf(987.77, 1318.51)
            val noteDurationMs = 50
            val samplesPerNote = (SAMPLE_RATE * noteDurationMs) / 1000
            val totalSamples = samplesPerNote * notes.size
            val buffer = ShortArray(totalSamples)

            for (n in notes.indices) {
                val freq = notes[n]
                val offset = n * samplesPerNote
                for (i in 0 until samplesPerNote) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / samplesPerNote
                    val envelope = 1.0 - (progress * 0.8)
                    val sample = sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.40
                    buffer[offset + i] = sample.toInt().toShort()
                }
            }
            enqueueSfx(buffer)
        }
    }

    fun playVictory() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
            val noteDurationMs = 100
            val samplesPerNote = (SAMPLE_RATE * noteDurationMs) / 1000
            val totalSamples = samplesPerNote * notes.size
            val buffer = ShortArray(totalSamples)

            for (n in notes.indices) {
                val freq = notes[n]
                val offset = n * samplesPerNote
                for (i in 0 until samplesPerNote) {
                    val t = i.toDouble() / SAMPLE_RATE
                    val progress = i.toDouble() / samplesPerNote
                    val envelope = 1.0 - (progress * 0.5)
                    val sample = sin(2.0 * Math.PI * freq * t) * envelope * Short.MAX_VALUE * 0.45
                    buffer[offset + i] = sample.toInt().toShort()
                }
            }
            enqueueSfx(buffer)
        }
    }

    private fun enqueueSfx(buffer: ShortArray) {
        if (sfxQueue.size < 4) { // Bound queue to prevent memory spikes
            sfxQueue.offer(buffer)
        }
    }
}
