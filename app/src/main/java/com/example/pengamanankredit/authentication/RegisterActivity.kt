package com.example.pengamanankredit.authentication

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pengamanankredit.R
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class RegisterActivity : AppCompatActivity() {
    // Deklarasi view
    lateinit var fullName: TextInputEditText
    lateinit var email: TextInputEditText
    lateinit var password: PasswordEditText
    lateinit var btnRegister: Button
    lateinit var progressDialog: ProgressDialog

    // Instance FirebaseAuth
    var firebaseAuth = FirebaseAuth.getInstance()

    // Handler untuk polling verifikasi email
    private val handler = Handler(Looper.getMainLooper())
    // Delay polling, misalnya setiap 5 detik
    private val pollDelay: Long = 5000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        // Inisialisasi semua view
        fullName = findViewById(R.id.ed_register_name)
        email = findViewById(R.id.ed_register_email)
        password = findViewById(R.id.ed_register_password)
        btnRegister = findViewById(R.id.registerButton)

        val scrollView = findViewById<ScrollView>(R.id.scrollView)
        scrollView.setOnTouchListener { _, _ -> true } // Mencegah scrolling


        // Inisialisasi ProgressDialog (tidak bisa dibatalkan agar tetap muncul)
        progressDialog = ProgressDialog(this).apply {
            setTitle("Registrasi")
            setMessage("Menunggu verifikasi email...\nHarap cek email Anda dan klik link verifikasi.")
            setCancelable(false)
        }

        // Tombol untuk berpindah ke halaman LoginActivity jika user memilih login
        val textViewLogin: TextView = findViewById(R.id.infoTextView2)
        textViewLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Set onClickListener pada tombol Register
        btnRegister.setOnClickListener {
            if (fullName.text!!.isNotEmpty() && email.text!!.isNotEmpty() && password.text!!.isNotEmpty()) {
                processRegister()
            } else {
                Toast.makeText(this, "Semua data wajib diisi!", LENGTH_SHORT).show()
            }
        }
    }

    private fun processRegister() {
        val FullName = fullName.text.toString()
        val emailEdit = email.text.toString()
        val Password = password.text.toString()

        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(emailEdit, Password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        // Update profil pengguna (misalnya, menambahkan display name)
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(FullName)
                            .build()

                        user.updateProfile(profileUpdates)
                            .addOnCompleteListener {
                                // Kirim email verifikasi
                                user.sendEmailVerification().addOnCompleteListener { verifyTask ->
                                    if (verifyTask.isSuccessful) {
                                        Toast.makeText(
                                            this,
                                            "Email verifikasi telah dikirim. Silakan cek email Anda.",
                                            LENGTH_SHORT
                                        ).show()
                                        // Jangan langsung sign out; mulai polling untuk cek verifikasi
                                        checkEmailVerification()
                                    } else {
                                        progressDialog.dismiss()
                                        Toast.makeText(
                                            this,
                                            verifyTask.exception?.localizedMessage,
                                            LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                            .addOnFailureListener { error ->
                                progressDialog.dismiss()
                                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
                            }
                    }
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, task.exception?.localizedMessage, LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { error ->
                progressDialog.dismiss()
                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
            }
    }

    // Fungsi untuk polling pengecekan verifikasi email
    private fun checkEmailVerification() {
        val user = firebaseAuth.currentUser
        user?.reload()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                if (user.isEmailVerified) {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Email terverifikasi!", LENGTH_SHORT).show()
                    // Setelah verifikasi, sign out dan arahkan ke LoginActivity
                    firebaseAuth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    // Jika belum terverifikasi, polling lagi setelah delay
                    handler.postDelayed({ checkEmailVerification() }, pollDelay)
                }
            } else {
                // Jika gagal reload, coba polling lagi setelah delay
                handler.postDelayed({ checkEmailVerification() }, pollDelay)
            }
        }
    }
}