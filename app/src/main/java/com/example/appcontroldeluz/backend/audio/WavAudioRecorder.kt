package com.example.appcontroldeluz.backend.audio

import android.Manifest
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.annotation.RequiresPermission
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.concurrent.thread

class WavAudioRecorder {
    private val sampleRate = 16000
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT

    private val minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)
    private val bufferSize = (minBufferSize * 2).coerceAtLeast(4096)

    @Volatile
    private var isRecording = false
    private var audioRecord: AudioRecord? = null
    private var outputFile: File? = null

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    fun start(output: File): Result<Unit> = runCatching {
        if (isRecording) return@runCatching

        outputFile = output
        val recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        if (recorder.state != AudioRecord.STATE_INITIALIZED) {
            throw IllegalStateException("No se pudo inicializar la grabacion de audio")
        }

        audioRecord = recorder
        isRecording = true
        recorder.startRecording()

        thread(name = "wav-recorder") {
            writeWavFile(recorder, output)
        }
    }

    fun stop(): File? {
        if (!isRecording) return outputFile
        isRecording = false
        audioRecord?.let { recorder ->
            runCatching { recorder.stop() }
            recorder.release()
        }
        audioRecord = null
        return outputFile
    }

    private fun writeWavFile(recorder: AudioRecord, file: File) {
        val tempFile = File(file.parentFile, "${file.nameWithoutExtension}.pcm")
        val buffer = ByteArray(bufferSize)
        var totalAudioLen = 0L

        FileOutputStream(tempFile).use { pcmOut ->
            while (isRecording) {
                val read = recorder.read(buffer, 0, buffer.size)
                if (read > 0) {
                    pcmOut.write(buffer, 0, read)
                    totalAudioLen += read
                }
            }
        }

        val totalDataLen = totalAudioLen + 36
        val byteRate = sampleRate * 2L // mono 16-bit

        FileOutputStream(file).use { wavOut ->
            writeWavHeader(
                wavOut,
                totalAudioLen = totalAudioLen,
                totalDataLen = totalDataLen,
                sampleRate = sampleRate,
                byteRate = byteRate
            )
            tempFile.inputStream().use { input ->
                input.copyTo(wavOut)
            }
        }

        tempFile.delete()
    }

    @Throws(IOException::class)
    private fun writeWavHeader(
        out: FileOutputStream,
        totalAudioLen: Long,
        totalDataLen: Long,
        sampleRate: Int,
        byteRate: Long
    ) {
        val header = ByteArray(44)

        header[0] = 'R'.code.toByte()
        header[1] = 'I'.code.toByte()
        header[2] = 'F'.code.toByte()
        header[3] = 'F'.code.toByte()
        writeInt(header, 4, totalDataLen.toInt())
        header[8] = 'W'.code.toByte()
        header[9] = 'A'.code.toByte()
        header[10] = 'V'.code.toByte()
        header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte()
        header[13] = 'm'.code.toByte()
        header[14] = 't'.code.toByte()
        header[15] = ' '.code.toByte()
        writeInt(header, 16, 16)
        writeShort(header, 20, 1.toShort())
        writeShort(header, 22, 1.toShort()) // mono
        writeInt(header, 24, sampleRate)
        writeInt(header, 28, byteRate.toInt())
        writeShort(header, 32, 2.toShort())
        writeShort(header, 34, 16.toShort()) // 16-bit
        header[36] = 'd'.code.toByte()
        header[37] = 'a'.code.toByte()
        header[38] = 't'.code.toByte()
        header[39] = 'a'.code.toByte()
        writeInt(header, 40, totalAudioLen.toInt())

        out.write(header, 0, 44)
    }

    private fun writeInt(data: ByteArray, offset: Int, value: Int) {
        data[offset] = (value and 0xff).toByte()
        data[offset + 1] = ((value shr 8) and 0xff).toByte()
        data[offset + 2] = ((value shr 16) and 0xff).toByte()
        data[offset + 3] = ((value shr 24) and 0xff).toByte()
    }

    private fun writeShort(data: ByteArray, offset: Int, value: Short) {
        data[offset] = (value.toInt() and 0xff).toByte()
        data[offset + 1] = ((value.toInt() shr 8) and 0xff).toByte()
    }
}
