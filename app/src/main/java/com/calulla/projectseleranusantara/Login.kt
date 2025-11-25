    package com.calulla.projectseleranusantara

    import android.os.Bundle
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AppCompatActivity
    import androidx.core.view.ViewCompat
    import androidx.core.view.WindowInsetsCompat
    import android.widget.Button
    import android.content.Intent
    import android.widget.TextView

    class Login : AppCompatActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)

            val btnSignIn = findViewById<Button>(R.id.btnSignIn)
            val goRegister = findViewById<TextView>(R.id.tvSignUp)   // <-- ini ID yang benar

            btnSignIn.setOnClickListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }

            goRegister.setOnClickListener {
                startActivity(Intent(this, Register::class.java))
                finish()
            }
        }
    }
