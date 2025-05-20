package com.example.pengamanankredit.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pengamanankredit.R
import com.example.pengamanankredit.authentication.AuthenticationActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AdminActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin)

        supportActionBar?.title = "Admin"
        supportActionBar?.setBackgroundDrawable(
            resources.getDrawable(R.color.blue) // Ganti dengan warna yang diinginkan
        )

        val btnFormNasabah = findViewById<Button>(R.id.btn_form_nasabah)
        btnFormNasabah.setOnClickListener {
            val intent = Intent(this, FormDataNasabahActivity::class.java)
            startActivity(intent)
        }

        val btnDataPemanggilan = findViewById<Button>(R.id.btn_pemanggilan)
        btnDataPemanggilan.setOnClickListener {
            val intent = Intent(this, DataPemanggilanActivity::class.java)
            startActivity(intent)
        }

        val btnDataNamaNasabah = findViewById<Button>(R.id.btn_nama_nasabah)
        btnDataNamaNasabah.setOnClickListener{
            val intent = Intent(this, DataNamaNasabahActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Logika logout
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        // Kembali ke login atau halaman awal
        val intent = Intent(this, AuthenticationActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }


    override fun onStart() {
        super.onStart()

        val db = FirebaseDatabase.getInstance().reference
        val nasabahRef = db.child("name_nasabah")

        nasabahRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (nasabahSnap in snapshot.children) {
                    val id = nasabahSnap.child("id").value?.toString()
                    val name = nasabahSnap.child("name").value?.toString()
                    val status = nasabahSnap.child("status").value?.toString()

                    if (id != null && name != null && status != null) {
                        val fullName = "$name - $status"

                        // 🔁 Update di /add_data
                        db.child("add_data").get().addOnSuccessListener { addDataSnap ->
                            for (dataSnap in addDataSnap.children) {
                                val existingName = dataSnap.child("nama_nasabah").value?.toString()
                                if (existingName?.startsWith(name.split(" ")[0]) == true) {
                                    db.child("add_data").child(dataSnap.key!!)
                                        .child("nama_nasabah").setValue(fullName)
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error: ${error.message}")
            }
        })
    }

}