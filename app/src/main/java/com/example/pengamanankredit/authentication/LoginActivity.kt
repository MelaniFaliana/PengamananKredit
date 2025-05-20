package com.example.pengamanankredit.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.pengamanankredit.MainActivity
import com.example.pengamanankredit.R
import com.example.pengamanankredit.admin.AdminActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    lateinit var email: TextInputEditText
    lateinit var password: PasswordEditText
    lateinit var btnLogin: Button
    lateinit var forgotPasswordTextView: TextView
    lateinit var progressDialog: ProgressDialog

    private var firebaseAuth = FirebaseAuth.getInstance()
    private var loginFailedCount = 0  // Menyimpan jumlah gagal login

    override fun onStart() {
        super.onStart()

        val currentUser = firebaseAuth.currentUser

        if (currentUser != null) {
            val email = currentUser.email

            if (email != null) {
                // Cek apakah akun admin
                if (email == "admpengamanankredit@gmail.com") {
                    val intent = Intent(this, AdminActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                finish() // Hapus LoginActivity dari backstack
            } else {
                // Jika email belum ready, tunggu sebentar dan coba lagi
                // (Opsional, hanya jika sering terjadi delay)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.ed_login_email)
        password = findViewById(R.id.ed_login_password)
        btnLogin = findViewById(R.id.loginButton)
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView)

        progressDialog = ProgressDialog(this).apply {
            setTitle("Logging")
            setMessage("Silakan tunggu...")
        }

        val textViewLogin: TextView = findViewById(R.id.infoTextViewLogin2)
        textViewLogin.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Nonaktifkan forgot password di awal
        forgotPasswordTextView.isEnabled = false

        // Login button listener
        btnLogin.setOnClickListener {
            if (email.text!!.isNotEmpty() && password.text!!.isNotEmpty()) {
                prosesLogin()
            } else {
                Toast.makeText(this, "Semua data wajib diisi!", Toast.LENGTH_SHORT).show()
            }
        }

        // Forgot Password listener
        forgotPasswordTextView.setOnClickListener {
            val emailUser = email.text.toString()

            if (emailUser.isEmpty()) {
                Toast.makeText(this, "Masukkan email terlebih dahulu!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(this).apply {
                setTitle("Konfirmasi Ubah Kata Sandi")
                setMessage("Apakah Anda yakin ingin mengubah kata sandi? Email reset akan dikirim ke $emailUser.")
                setPositiveButton("Ya") { _, _ ->
                    firebaseAuth.sendPasswordResetEmail(emailUser)
                        .addOnSuccessListener {
                            Toast.makeText(this@LoginActivity, "Email reset password telah dikirim ke $emailUser", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this@LoginActivity, "Gagal mengirim email: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                create().show()
            }
        }
    }

    private fun prosesLogin() {
        val emailEdit = email.text.toString()
        val passwordEdit = password.text.toString()

        progressDialog.show()
        firebaseAuth.signInWithEmailAndPassword(emailEdit, passwordEdit)
            .addOnSuccessListener {
                loginFailedCount = 0 // Reset hitungan jika login berhasil

                // Cek apakah email dan password adalah akun admin
                if (emailEdit == "admpengamanankredit@gmail.com") {
                    val intent = Intent(this, AdminActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                } else {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }


                finish() // Menutup LoginActivity agar tidak bisa kembali dengan tombol back
            }
            .addOnFailureListener {
                progressDialog.dismiss()
                loginFailedCount++

                when (loginFailedCount) {
                    1, 2 -> {
                        forgotPasswordTextView.isEnabled = true
                        Toast.makeText(this, "Kata sandi Anda salah, silakan coba lagi!", Toast.LENGTH_SHORT).show()
                    }
                    3 -> {
                        Toast.makeText(this, "Kata sandi salah, silakan reset kata sandi Anda!", Toast.LENGTH_LONG).show()
                    }
                }
            }
            .addOnCompleteListener {
                progressDialog.dismiss()
            }
    }

}