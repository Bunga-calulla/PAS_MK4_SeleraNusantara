package com.calulla.projectseleranusantara

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders

object Constants {
    // Emulator → 10.0.2.2 | HP Fisik → IP komputer (cek: ipconfig)
    const val BASE_URL = "http://10.0.2.2:8000/api/"
    const val PREF_NAME = "satoerasa_prefs"
    const val KEY_TOKEN  = "auth_token"
    const val KEY_USER_NAME = "user_name"

    // Menghasilkan objek GlideUrl dengan Header Connection: close untuk bypass bug Keep-Alive PHP Windows
    fun getGlideUrl(url: String?): Any? {
        if (url.isNullOrEmpty()) return null
        val correctedUrl = url
            .replace("localhost", "10.0.2.2")
            .replace("127.0.0.1", "10.0.2.2") + "?t=" + System.currentTimeMillis()
        return GlideUrl(
            correctedUrl,
            LazyHeaders.Builder()
                .addHeader("Connection", "close")
                .build()
        )
    }
}
