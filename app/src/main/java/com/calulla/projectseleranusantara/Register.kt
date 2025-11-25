package com.calulla.projectseleranusantara

import android.content.Intent
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button

class Register : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}