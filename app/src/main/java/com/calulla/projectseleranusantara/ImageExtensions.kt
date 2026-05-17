package com.calulla.projectseleranusantara

import android.graphics.BitmapFactory
import android.widget.ImageView
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL

fun ImageView.loadImage(url: String?, placeholderResId: Int = R.drawable.gudeg) {
    // 1. Tampilkan placeholder loading dulu
    setImageResource(placeholderResId)
    
    if (url.isNullOrEmpty()) return

    // 2. Koreksi IP untuk emulator dan tambahkan cache-buster agar update gambar langsung tampil
    val correctedUrl = url
        .replace("localhost", "10.0.2.2")
        .replace("127.0.0.1", "10.0.2.2") + "?t=" + System.currentTimeMillis()

    // 3. Jalankan pemuatan gambar di Background Thread (IO)
    CoroutineScope(Dispatchers.IO).launch {
        var connection: HttpURLConnection? = null
        try {
            val serverUrl = URL(correctedUrl)
            connection = serverUrl.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connectTimeout = 5000
            connection.readTimeout = 5000
            // Set header Connection: close secara eksplisit untuk bypass bug keep-alive Windows
            connection.setRequestProperty("Connection", "close")
            connection.connect()

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val inputStream = connection.inputStream
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    // Pindah kembali ke Main Thread untuk mengupdate UI
                    withContext(Dispatchers.Main) {
                        setImageBitmap(bitmap)
                    }
                    return@launch
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }

        // Jika terjadi kegagalan, tampilkan kembali gambar placeholder/default
        withContext(Dispatchers.Main) {
            setImageResource(placeholderResId)
        }
    }
}
