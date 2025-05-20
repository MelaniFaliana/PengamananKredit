package com.example.pengamanankredit.authentication

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.widget.Button
import com.example.pengamanankredit.MainActivity
import com.example.pengamanankredit.R
import com.example.pengamanankredit.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth

class AuthenticationActivity : AppCompatActivity() {

    var firebaseAuth = FirebaseAuth.getInstance()

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val email = currentUser.email
            val intent = if (email == "admpengamanankredit@gmail.com") {
                Intent(this, AdminActivity::class.java)
            } else {
                Intent(this, MainActivity::class.java)
            }
            // Cegah kembali ke AuthenticationActivity dengan tombol back
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_authentication)

        val btnLogin: Button = findViewById(R.id.button_login)
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        val btnRegister: Button = findViewById(R.id.button_register)
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}