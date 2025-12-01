package com.calulla.projectseleranusantara

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import android.content.Intent

class Login : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnSignIn = findViewById<Button>(R.id.btnSignIn)
        val goRegister = findViewById<TextView>(R.id.tvSignUp)

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)

        btnSignIn.setOnClickListener {

            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Isi username dan password!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        goRegister.setOnClickListener {
            startActivity(Intent(this, Register::class.java))
            finish()
        }
    }
}
