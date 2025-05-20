package com.example.pengamanankredit

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainActivity : AppCompatActivity() {


    private val db = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        navView.setupWithNavController(navController)


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