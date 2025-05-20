package com.example.pengamanankredit.bottomnav.ui.profil


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import com.yalantis.ucrop.UCrop
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.pengamanankredit.R
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class EditProfilActivity : AppCompatActivity() {

    private lateinit var btnChangePhoto: Button
    private lateinit var btnSave: Button
    private lateinit var imageViewSelected: ImageView

    // Variabel untuk menyimpan URI gambar hasil crop sementara
    private var savedImageUri: Uri? = null

    // Deklarasi ActivityResultLauncher untuk membuka galeri
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            startCrop(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_profil)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false) // <-- penting!
        toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.blue))


        val backButton: ImageButton = findViewById(R.id.btn_back)
        backButton.setOnClickListener {
            finish()
        }

        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val userName = currentUser?.displayName ?: "Pengguna"
        val emailUser = currentUser?.email ?: "user@gmail.com"

        val nameTextView = findViewById<TextView>(R.id.et_name)
        nameTextView.text = userName

        val emailTextView = findViewById<TextView>(R.id.et_email)
        emailTextView.text = emailUser

        btnChangePhoto = findViewById(R.id.btn_change_photo)
        btnSave = findViewById(R.id.btn_save)
        imageViewSelected = findViewById(R.id.profile_image)

        val prefs = getSharedPreferences("profilePrefs", MODE_PRIVATE)

        // Simpan gambar sesuai akun pengguna
        val key = "profileImageUri_$emailUser"
        val uriString = prefs.getString(key, null)
        if (uriString != null) {
            savedImageUri = Uri.parse(uriString)
            imageViewSelected.setImageURI(savedImageUri)
        } else {
            imageViewSelected.setImageResource(R.drawable.person_white)
        }

        btnChangePhoto.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // Ambil referensi dari ikon edit dan EditText
        val editResortIcon: ImageView = findViewById(R.id.edit_resort)
        val etResort: EditText = findViewById(R.id.et_resort)

        // Buat key unik untuk menyimpan teks resort sesuai dengan email pengguna
        val resortKey = "resortText_$emailUser"

        // Ambil teks yang tersimpan sebelumnya dari SharedPreferences
        val savedResortText = prefs.getString(resortKey, "")
        etResort.setText(savedResortText)

        // Awalnya EditText tidak bisa diedit
        etResort.isEnabled = false

        editResortIcon.setOnClickListener {
            etResort.isEnabled = true
            etResort.requestFocus()

            // Tampilkan keyboard
            etResort.postDelayed({
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(etResort, InputMethodManager.SHOW_IMPLICIT)
            }, 100)
        }

        btnSave.setOnClickListener {
            if (savedImageUri != null) {
                prefs.edit().putString(key, savedImageUri.toString()).apply()
                Toast.makeText(this, "Gambar tersimpan", Toast.LENGTH_SHORT).show()
                imageViewSelected.setImageURI(savedImageUri)
            } else {
                Toast.makeText(this, "Belum ada gambar yang dipilih", Toast.LENGTH_SHORT).show()
            }

            // Simpan teks yang diedit oleh pengguna
            val newText = etResort.text.toString()
            prefs.edit().putString(resortKey, newText).apply()
            Toast.makeText(this, "Resort tersimpan", Toast.LENGTH_SHORT).show()

            // Nonaktifkan kembali EditText setelah menyimpan
            etResort.isEnabled = false
        }
    }

    private fun startCrop(sourceUri: Uri) {
        val destFile = File(cacheDir, "cropped_image_${System.currentTimeMillis()}.jpg")
        val destinationUri = Uri.fromFile(destFile)

        UCrop.of(sourceUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(500, 500)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                val resultUri: Uri? = UCrop.getOutput(data!!)
                resultUri?.let {
                    imageViewSelected.setImageURI(it)
                    savedImageUri = it
                }
            } else if (resultCode == UCrop.RESULT_ERROR) {
                val cropError = UCrop.getError(data!!)
                cropError?.printStackTrace()
            }
        }
    }
}