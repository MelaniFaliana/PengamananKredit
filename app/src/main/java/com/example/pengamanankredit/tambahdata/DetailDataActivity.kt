package com.example.pengamanankredit.tambahdata

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pengamanankredit.R
import com.example.pengamanankredit.perbaruidata.UpdateDataActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class DetailDataActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_data)
        FirebaseApp.initializeApp(this)

        val btnBackImage = findViewById<ImageView>(R.id.btn_back)
        val btnBackButton = findViewById<Button>(R.id.btnBack)
        val updateButton  = findViewById<Button>(R.id.btnUpdate)

        btnBackImage.setOnClickListener {
            finish() // Menutup activity ini dan kembali ke activity sebelumnya
        }

        btnBackButton.setOnClickListener {
            finish()
        }


        updateButton.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("add_data")
            ref.get().addOnSuccessListener { snapshot ->
                var namaNasabahFound = false

                for (idSnapshot in snapshot.children) {
                    val namaNasabah = idSnapshot.child("nama_nasabah").value?.toString() ?: ""

                    Log.d("UpdateDataActivity", "ID: ${idSnapshot.key}, Nama Nasabah: $namaNasabah")

                    if (namaNasabah.equals(intent.getStringExtra("namaNasabah"), ignoreCase = true)) {
                        namaNasabahFound = true
                        val intent = Intent(this, UpdateDataActivity::class.java)
                        intent.putExtra("namaNasabah", namaNasabah)
                        startActivity(intent)
                        break
                    }
                }

                if (!namaNasabahFound) {
                    Toast.makeText(this, "Nama nasabah tidak ditemukan!", Toast.LENGTH_SHORT).show()
                }
            }
        }




        val namaNasabah = intent.getStringExtra("namaNasabah") ?: return

        // Data bagian peringatan
        val contentPeringatanDetail = findViewById<LinearLayout>(R.id.contentPeringatanDetail)
        val iconToggle = findViewById<ImageView>(R.id.iconTogglePeringatanDetail)

        // Data bagian surat peringatan1
        val contentSuratPeringatan1Detail = findViewById<LinearLayout>(R.id.contentSuratPeringatan1Detail)
        val iconToggleSuratPeringatan1Detail = findViewById<ImageView>(R.id.iconToggleSuratPeringatan1Detail)

        // Data bagian surat peringatan2
        val contentSuratPeringatan2Detail = findViewById<LinearLayout>(R.id.contentSuratPeringatan2Detail)
        val iconToggleSuratPeringatan2Detail = findViewById<ImageView>(R.id.iconToggleSuratPeringatan2Detail)

        // Data bagian surat peringatan3
        val contentSuratPeringatan3Detail = findViewById<LinearLayout>(R.id.contentSuratPeringatan3Detail)
        val iconToggleSuratPeringatan3Detail = findViewById<ImageView>(R.id.iconToggleSuratPeringatan3Detail)

        // Data bagian surat panggilan
        val contentSuratPanggilanDetail = findViewById<LinearLayout>(R.id.contentSuratPanggilanDetail)
        val iconToggleSuratPanggilanDetail = findViewById<ImageView>(R.id.iconToggleSuratPanggilanDetail)

        // Data bagian penyemprotan/sticker
        val contentPenyemprotanDetail = findViewById<LinearLayout>(R.id.contentPenyemprotanDetail)
        val iconTogglePenyemprotanDetail = findViewById<ImageView>(R.id.iconTogglePenyemprotanDetail)

        // Data bagian eksekusi
        val contentEksekusiDetail = findViewById<LinearLayout>(R.id.contentEksekusiDetail)
        val iconToggleEksekusiDetail = findViewById<ImageView>(R.id.iconToggleEksekusiDetail)

        // Data bagian realisasi
        val contentRealisasiDetail = findViewById<LinearLayout>(R.id.contentRealisasiDetail)
        val iconToggleRealisasiDetail = findViewById<ImageView>(R.id.iconToggleRealisasiDetail)

        database = FirebaseDatabase.getInstance().reference

        val namaNasabah2 = intent.getStringExtra("namaNasabah") ?: ""

        val nasabahRef = database.child("add_data")
        nasabahRef.orderByChild("nama_nasabah").equalTo(namaNasabah)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val id = snapshot.children.firstOrNull()?.key ?: return
                        loadData(id, namaNasabah2) // atau fungsi lain yang kamu panggil
                    } else {
                        Toast.makeText(this@DetailDataActivity, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@DetailDataActivity, "Terjadi kesalahan", Toast.LENGTH_SHORT).show()
                }
            })


        // Menangani klik toggle icon untuk expand/collapse
        iconToggle.setOnClickListener {
            if (contentPeringatanDetail.visibility == View.GONE) {
                contentPeringatanDetail.visibility = View.VISIBLE
                iconToggle.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentPeringatanDetail.visibility = View.GONE
                iconToggle.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        iconToggleSuratPeringatan1Detail.setOnClickListener {
            if (contentSuratPeringatan1Detail.visibility == View.GONE) {
                contentSuratPeringatan1Detail.visibility = View.VISIBLE
                iconToggleSuratPeringatan1Detail.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentSuratPeringatan1Detail.visibility = View.GONE
                iconToggleSuratPeringatan1Detail.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        iconToggleSuratPeringatan2Detail.setOnClickListener {
            if (contentSuratPeringatan2Detail.visibility == View.GONE) {
                contentSuratPeringatan2Detail.visibility = View.VISIBLE
                iconToggleSuratPeringatan2Detail.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentSuratPeringatan2Detail.visibility = View.GONE
                iconToggleSuratPeringatan2Detail.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        iconToggleSuratPeringatan3Detail.setOnClickListener {
            if (contentSuratPeringatan3Detail.visibility == View.GONE) {
                contentSuratPeringatan3Detail.visibility = View.VISIBLE
                iconToggleSuratPeringatan3Detail.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentSuratPeringatan3Detail.visibility = View.GONE
                iconToggleSuratPeringatan3Detail.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        iconToggleSuratPanggilanDetail.setOnClickListener {
            if (contentSuratPanggilanDetail.visibility == View.GONE) {
                contentSuratPanggilanDetail.visibility = View.VISIBLE
                iconToggleSuratPanggilanDetail.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentSuratPanggilanDetail.visibility = View.GONE
                iconToggleSuratPanggilanDetail.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        iconTogglePenyemprotanDetail.setOnClickListener {
            if (contentPenyemprotanDetail.visibility == View.GONE) {
                contentPenyemprotanDetail.visibility = View.VISIBLE
                iconTogglePenyemprotanDetail.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentPenyemprotanDetail.visibility = View.GONE
                iconTogglePenyemprotanDetail.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }


        iconToggleEksekusiDetail.setOnClickListener {
            if (contentEksekusiDetail.visibility == View.GONE) {
                contentEksekusiDetail.visibility = View.VISIBLE
                iconToggleEksekusiDetail.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentEksekusiDetail.visibility = View.GONE
                iconToggleEksekusiDetail.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        iconToggleRealisasiDetail.setOnClickListener {
            if (contentRealisasiDetail.visibility == View.GONE) {
                contentRealisasiDetail.visibility = View.VISIBLE
                iconToggleRealisasiDetail.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentRealisasiDetail.visibility = View.GONE
                iconToggleRealisasiDetail.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }
    }

    private fun loadData(id: String, namaNasabah: String) {
        val id = intent.getStringExtra("id") ?: ""
        val namaNasabah1 = intent.getStringExtra("namaNasabah") ?: ""

        val tanggalUploadRef = database.child("add_data").child(id).child("tanggal_upload")

        tanggalUploadRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tanggalUpload = snapshot.getValue(String::class.java) ?: "-"
                findViewById<TextView>(R.id.tvUploader).text = "Diunggah pada tanggal $tanggalUpload"
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat tanggal upload", Toast.LENGTH_SHORT).show()
            }
        })


        val peringatanRef = database.child("add_data").child(id).child("peringatan")

        peringatanRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tanggal = snapshot.child("tanggal").getValue(String::class.java) ?: "-"
                val uploadData = snapshot.child("uploadData").getValue(String::class.java) ?: "-"
                val keterangan = snapshot.child("keterangan").getValue(String::class.java) ?: "-"
                val status = snapshot.child("status").getValue(String::class.java) ?: "-"

                findViewById<TextView>(R.id.etTanggalPeringatanDetail).text = tanggal
                findViewById<TextView>(R.id.etUploadDataPeringatanDetail).text = uploadData
                findViewById<TextView>(R.id.etKeteranganPeringatanDetail).text = keterangan
                findViewById<TextView>(R.id.spinnerStatusPeringatanDetail).text = status

                findViewById<TextView>(R.id.tvTitle).text = namaNasabah1
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })


        val suratperingatan1Ref = database.child("add_data").child(id).child("surat_peringatan1")

        suratperingatan1Ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tanggal = snapshot.child("tanggal").getValue(String::class.java) ?: "-"
                val uploadData = snapshot.child("uploadData").getValue(String::class.java) ?: "-"
                val keterangan = snapshot.child("keterangan").getValue(String::class.java) ?: "-"
                val status = snapshot.child("status").getValue(String::class.java) ?: "-"

                // Set ke tampilan
                findViewById<TextView>(R.id.etTanggalSuratPeringatan1Detail).text = tanggal
                findViewById<TextView>(R.id.etUploadDataSuratPeringatan1Detail).text = uploadData
                findViewById<TextView>(R.id.etKeteranganSuratPeringatan1Detail).text = keterangan
                findViewById<TextView>(R.id.spinnerStatusSuratPeringatan1Detail).text = status
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })

        val suratperingatan2Ref = database.child("add_data").child(id).child("surat_peringatan2")

        suratperingatan2Ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tanggal = snapshot.child("tanggal").getValue(String::class.java) ?: "-"
                val uploadData = snapshot.child("uploadData").getValue(String::class.java) ?: "-"
                val keterangan = snapshot.child("keterangan").getValue(String::class.java) ?: "-"
                val status = snapshot.child("status").getValue(String::class.java) ?: "-"

                // Set ke tampilan
                findViewById<TextView>(R.id.etTanggalSuratPeringatan2Detail).text = tanggal
                findViewById<TextView>(R.id.etUploadDataSuratPeringatan2Detail).text = uploadData
                findViewById<TextView>(R.id.etKeteranganSuratPeringatan2Detail).text = keterangan
                findViewById<TextView>(R.id.spinnerStatusSuratPeringatan2Detail).text = status
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })


        val suratperingatan3Ref = database.child("add_data").child(id).child("surat_peringatan3")

        suratperingatan3Ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tanggal = snapshot.child("tanggal").getValue(String::class.java) ?: "-"
                val uploadData = snapshot.child("uploadData").getValue(String::class.java) ?: "-"
                val keterangan = snapshot.child("keterangan").getValue(String::class.java) ?: "-"
                val status = snapshot.child("status").getValue(String::class.java) ?: "-"

                // Set ke tampilan
                findViewById<TextView>(R.id.etTanggalSuratPeringatan3Detail).text = tanggal
                findViewById<TextView>(R.id.etUploadDataSuratPeringatan3Detail).text = uploadData
                findViewById<TextView>(R.id.etKeteranganSuratPeringatan3Detail).text = keterangan
                findViewById<TextView>(R.id.spinnerStatusSuratPeringatan3Detail).text = status
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })


        val suratpanggilanRef = database.child("add_data").child(id).child("surat_panggilan")

        suratpanggilanRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tanggal = snapshot.child("tanggal").getValue(String::class.java) ?: "-"
                val uploadData = snapshot.child("uploadData").getValue(String::class.java) ?: "-"
                val keterangan = snapshot.child("keterangan").getValue(String::class.java) ?: "-"
                val status = snapshot.child("status").getValue(String::class.java) ?: "-"

                // Set ke tampilan
                findViewById<TextView>(R.id.etTanggalSuratPanggilanDetail).text = tanggal
                findViewById<TextView>(R.id.etUploadDataSuratPanggilanDetail).text = uploadData
                findViewById<TextView>(R.id.etKeteranganSuratPanggilanDetail).text = keterangan
                findViewById<TextView>(R.id.spinnerStatusSuratPanggilanDetail).text = status
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })


        val penyemprotanRef = database.child("add_data").child(id).child("penyemprotan")

        penyemprotanRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tanggal = snapshot.child("tanggal").getValue(String::class.java) ?: "-"
                val uploadData = snapshot.child("uploadData").getValue(String::class.java) ?: "-"
                val keterangan = snapshot.child("keterangan").getValue(String::class.java) ?: "-"
                val status = snapshot.child("status").getValue(String::class.java) ?: "-"

                // Set ke tampilan
                findViewById<TextView>(R.id.etTanggalPenyemprotanDetail).text = tanggal
                findViewById<TextView>(R.id.etUploadDataPenyemprotanDetail).text = uploadData
                findViewById<TextView>(R.id.etKeteranganPenyemprotanDetail).text = keterangan
                findViewById<TextView>(R.id.spinnerStatusPenyemprotanDetail).text = status
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })


        val eksekusiRef = database.child("add_data").child(id).child("eksekusi")

        eksekusiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tanggal = snapshot.child("tanggal").getValue(String::class.java) ?: "-"
                val uploadData = snapshot.child("uploadData").getValue(String::class.java) ?: "-"
                val keterangan = snapshot.child("keterangan").getValue(String::class.java) ?: "-"
                val status = snapshot.child("status").getValue(String::class.java) ?: "-"

                // Set ke tampilan
                findViewById<TextView>(R.id.etTanggalEksekusiDetail).text = tanggal
                findViewById<TextView>(R.id.etUploadDataEksekusiDetail).text = uploadData
                findViewById<TextView>(R.id.etKeteranganEksekusiDetail).text = keterangan
                findViewById<TextView>(R.id.spinnerStatusEksekusiDetail).text = status
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })


        val realisasiRef = database.child("add_data").child(id).child("realisasi")

        realisasiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val realisasi1 = snapshot.child("realisasi_1").getValue(String::class.java) ?: "-"
                val realisasi2 = snapshot.child("realisasi_2").getValue(String::class.java) ?: "-"
                val realisasi3 = snapshot.child("realisasi_3").getValue(String::class.java) ?: "-"
                val realisasi4 = snapshot.child("realisasi_4").getValue(String::class.java) ?: "-"
                val realisasi5 = snapshot.child("realisasi_5").getValue(String::class.java) ?: "-"
                val realisasi6 = snapshot.child("realisasi_6").getValue(String::class.java) ?: "-"
                val realisasi7 = snapshot.child("realisasi_7").getValue(String::class.java) ?: "-"
                val realisasi8 = snapshot.child("realisasi_8").getValue(String::class.java) ?: "-"
                val realisasi9 = snapshot.child("realisasi_9").getValue(String::class.java) ?: "-"
                val realisasi10 = snapshot.child("realisasi_10").getValue(String::class.java) ?: "-"

                // Set ke tampilan
                findViewById<TextView>(R.id.realisasi_1).text = realisasi1
                findViewById<TextView>(R.id.realisasi_2).text = realisasi2
                findViewById<TextView>(R.id.realisasi_3).text = realisasi3
                findViewById<TextView>(R.id.realisasi_4).text = realisasi4
                findViewById<TextView>(R.id.realisasi_5).text = realisasi5
                findViewById<TextView>(R.id.realisasi_6).text = realisasi6
                findViewById<TextView>(R.id.realisasi_7).text = realisasi7
                findViewById<TextView>(R.id.realisasi_8).text = realisasi8
                findViewById<TextView>(R.id.realisasi_9).text = realisasi9
                findViewById<TextView>(R.id.realisasi_10).text = realisasi10
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DetailDataActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onResume() {
        super.onResume()
        val namaNasabah = intent.getStringExtra("namaNasabah") ?: return

        val nasabahRef = database.child("add_data")
        nasabahRef.orderByChild("nama_nasabah").equalTo(namaNasabah)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val id = snapshot.children.firstOrNull()?.key ?: return
                        loadData(id, namaNasabah)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

}