package com.calulla.projectseleranusantara

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        val btnStart = findViewById<Button>(R.id.btnStart)

        btnStart.setOnClickListener {
            // Pindah ke halaman Login (atau Main)
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
            finish()
        }
    }
}
