package com.example.pengamanankredit.tambahdata

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.pengamanankredit.R
import com.example.pengamanankredit.databinding.ActivityAddDataBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.text.SimpleDateFormat

class AddDataActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddDataBinding
    private var realisasiCount = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAddDataBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val btnBackImage = findViewById<ImageView>(R.id.btn_back)
        val btnBackButton = findViewById<Button>(R.id.button_kembali)


        btnBackImage.setOnClickListener {
            finish() // Menutup activity ini dan kembali ke activity sebelumnya
        }

        btnBackButton.setOnClickListener {
            finish()
        }

        val searchView = findViewById<SearchView>(R.id.searchView)
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("name_nasabah")
        val inputNamaNasabah = findViewById<EditText>(R.id.etNamaNasabah)


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { nomorCiff ->
                    // Konversi input user menjadi Double (karena di Firebase disimpan sebagai Number)
                    val nomorCiffDouble = nomorCiff.toDoubleOrNull()

                    if (nomorCiffDouble == null) {
                        inputNamaNasabah.setText("Format ID tidak valid")
                        return true
                    }

                    Log.d("SearchView", "Mencari ID: $nomorCiffDouble")
                    val searchedIds = nomorCiffDouble.toLong()
                    checkIfIdAlreadyUploaded(searchedIds)

                    reference.orderByChild("id").equalTo(nomorCiffDouble)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {
                                    for (data in snapshot.children) {
                                        val nama = data.child("name").value.toString()
                                        val status = data.child("status").value.toString()
                                        Log.d("Firebase", "Data ditemukan: $nama - $status")

                                        inputNamaNasabah.setText("$nama - $status")
                                        val searchedIds = nomorCiffDouble.toLong()
                                        Log.d("SearchView", "ID ditemukan: $searchedIds")
                                        checkIfIdAlreadyUploaded(searchedIds)
                                    }
                                } else {
                                    Log.d(
                                        "Firebase",
                                        "Data tidak ditemukan untuk ID: $nomorCiffDouble"
                                    )
                                    inputNamaNasabah.setText("Data tidak ditemukan")
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Log.e("Firebase", "Error: ${error.message}")
                            }
                        })
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.isIconified = false
        binding.searchView.isFocusable = true
        binding.searchView.isFocusableInTouchMode = true
        binding.searchView.requestFocus()
        binding.searchView.clearFocus()


        //PERINGATAN
        val contentPeringatan = findViewById<LinearLayout>(R.id.contentPeringatan)
        val iconToggle = findViewById<ImageView>(R.id.iconTogglePeringatan)

        iconToggle.setOnClickListener {
            if (contentPeringatan.visibility == View.GONE) {
                contentPeringatan.visibility = View.VISIBLE
                iconToggle.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentPeringatan.visibility = View.GONE
                iconToggle.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        val etTanggal = findViewById<EditText>(R.id.etTanggalPeringatan)

        etTanggal.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggal.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val spinnerStatus = findViewById<Spinner>(R.id.spinnerStatusPeringatan)

// Data pilihan untuk spinner
        val statusList = listOf("Pilih Status", "Pending", "Proses", "Selesai")

// Adapter untuk spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = adapter


        //SURAT PERINGATAN 1
        val contentPeringatan1 = findViewById<LinearLayout>(R.id.contentPeringatan1)
        val iconToggle1 = findViewById<ImageView>(R.id.iconTogglePeringatan1)

        iconToggle1.setOnClickListener {
            if (contentPeringatan1.visibility == View.GONE) {
                contentPeringatan1.visibility = View.VISIBLE
                iconToggle1.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentPeringatan1.visibility = View.GONE
                iconToggle1.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        val etTanggal1 = findViewById<EditText>(R.id.etTanggal1)

        etTanggal1.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggal1.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val spinnerStatus1 = findViewById<Spinner>(R.id.spinnerStatus1)

// Data pilihan untuk spinner
        val statusList1 = listOf("Pilih Status", "Pending", "Proses", "Selesai")

// Adapter untuk spinner
        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList1)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus1.adapter = adapter1



        //SURAT PERINGATAN 2
        val contentPeringatan2 = findViewById<LinearLayout>(R.id.contentPeringatan2)
        val iconToggle2 = findViewById<ImageView>(R.id.iconTogglePeringatan2)

        iconToggle2.setOnClickListener {
            if (contentPeringatan2.visibility == View.GONE) {
                contentPeringatan2.visibility = View.VISIBLE
                iconToggle2.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentPeringatan2.visibility = View.GONE
                iconToggle2.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        val etTanggal2 = findViewById<EditText>(R.id.etTanggal2)

        etTanggal2.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggal2.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val spinnerStatus2 = findViewById<Spinner>(R.id.spinnerStatus2)

// Data pilihan untuk spinner
        val statusList2 = listOf("Pilih Status", "Pending", "Proses", "Selesai")

// Adapter untuk spinner
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList2)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus2.adapter = adapter2



        //SURAT PERINGATAN 3
        val contentPeringatan3 = findViewById<LinearLayout>(R.id.contentPeringatan3)
        val iconToggle3 = findViewById<ImageView>(R.id.iconTogglePeringatan3)

        iconToggle3.setOnClickListener {
            if (contentPeringatan3.visibility == View.GONE) {
                contentPeringatan3.visibility = View.VISIBLE
                iconToggle3.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentPeringatan3.visibility = View.GONE
                iconToggle3.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        val etTanggal3 = findViewById<EditText>(R.id.etTanggal3)

        etTanggal3.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggal3.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val spinnerStatus3 = findViewById<Spinner>(R.id.spinnerStatus3)

// Data pilihan untuk spinner
        val statusList3 = listOf("Pilih Status", "Pending", "Proses", "Selesai")

// Adapter untuk spinner
        val adapter3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList3)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus3.adapter = adapter3

        //SURAT PANGGILAN
        val contentPanggilan = findViewById<LinearLayout>(R.id.contentPanggilan)
        val iconTogglePanggilan = findViewById<ImageView>(R.id.iconTogglePanggilan)

        iconTogglePanggilan.setOnClickListener {
            if (contentPanggilan.visibility == View.GONE) {
                contentPanggilan.visibility = View.VISIBLE
                iconTogglePanggilan.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentPanggilan.visibility = View.GONE
                iconTogglePanggilan.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        val etTanggalPanggilan = findViewById<EditText>(R.id.etTanggalPanggilan)

        etTanggalPanggilan.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPanggilan.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val spinnerStatusPanggilan = findViewById<Spinner>(R.id.spinnerStatusPanggilan)

// Data pilihan untuk spinner
        val statusListPanggilan = listOf("Pilih Status", "Pending", "Proses", "Selesai")

// Adapter untuk spinner
        val adapterPanggilan = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListPanggilan)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPanggilan.adapter = adapterPanggilan


        //SURAT PENYEMPROTAN/STICKER
        val contentPenyemprotan = findViewById<LinearLayout>(R.id.contentPenyemprotan)
        val iconTogglePenyemprotan = findViewById<ImageView>(R.id.iconTogglePenyemprotan)

        iconTogglePenyemprotan.setOnClickListener {
            if (contentPenyemprotan.visibility == View.GONE) {
                contentPenyemprotan.visibility = View.VISIBLE
                iconTogglePenyemprotan.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentPenyemprotan.visibility = View.GONE
                iconTogglePenyemprotan.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        val etTanggalPenyemprotan = findViewById<EditText>(R.id.etTanggalPenyemprotan)

        etTanggalPenyemprotan.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPenyemprotan.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val spinnerStatusPenyemprotan = findViewById<Spinner>(R.id.spinnerStatusPenyemprotan)

// Data pilihan untuk spinner
        val statusListPenyemprotan = listOf("Pilih Status", "Pending", "Proses", "Selesai")

// Adapter untuk spinner
        val adapterPenyemprotan = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListPenyemprotan)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPenyemprotan.adapter = adapterPenyemprotan


        //SURAT EKSEKUSI
        val contentEksekusi = findViewById<LinearLayout>(R.id.contentEksekusi)
        val iconToggleEksekusi = findViewById<ImageView>(R.id.iconToggleEksekusi)

        iconToggleEksekusi.setOnClickListener {
            if (contentEksekusi.visibility == View.GONE) {
                contentEksekusi.visibility = View.VISIBLE
                iconToggleEksekusi.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentEksekusi.visibility = View.GONE
                iconToggleEksekusi.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        val etTanggalEksekusi = findViewById<EditText>(R.id.etTanggalEksekusi)

        etTanggalEksekusi.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalEksekusi.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }

        val spinnerStatusEksekusi = findViewById<Spinner>(R.id.spinnerStatusEksekusi)

// Data pilihan untuk spinner
        val statusListEksekusi = listOf("Pilih Status", "Pending", "Proses", "Selesai")

// Adapter untuk spinner
        val adapterEksekusi = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListEksekusi)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusEksekusi.adapter = adapterEksekusi

        //REALISASI
        val contentRealisasi = findViewById<LinearLayout>(R.id.contentRealisasi)
        val iconToggleRealisasi = findViewById<ImageView>(R.id.iconToggleRealisasi)

        iconToggleRealisasi.setOnClickListener {
            if (contentRealisasi.visibility == View.GONE) {
                contentRealisasi.visibility = View.VISIBLE
                iconToggleRealisasi.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentRealisasi.visibility = View.GONE
                iconToggleRealisasi.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        binding.addRealisasi.setOnClickListener {
            if (realisasiCount < 10) {
                realisasiCount++

                val textView = when (realisasiCount) {
                    2 -> binding.textRealisasi2
                    3 -> binding.textRealisasi3
                    4 -> binding.textRealisasi4
                    5 -> binding.textRealisasi5
                    6 -> binding.textRealisasi6
                    7 -> binding.textRealisasi7
                    8 -> binding.textRealisasi8
                    9 -> binding.textRealisasi9
                    10 -> binding.textRealisasi10
                    else -> null
                }
                val editText = when (realisasiCount) {
                    2 -> binding.inputRealisasi2
                    3 -> binding.inputRealisasi3
                    4 -> binding.inputRealisasi4
                    5 -> binding.inputRealisasi5
                    6 -> binding.inputRealisasi6
                    7 -> binding.inputRealisasi7
                    8 -> binding.inputRealisasi8
                    9 -> binding.inputRealisasi9
                    10 -> binding.inputRealisasi10
                    else -> null
                }

                if (textView != null && editText != null) {
                    textView.visibility = View.VISIBLE
                    editText.visibility = View.VISIBLE
                    editText.setText("") // Kosongkan karena realisasi baru
                }
            } else {
                Toast.makeText(this, "Maksimal 10 realisasi!", Toast.LENGTH_SHORT).show()
            }
        }

        val btnSave = findViewById<Button>(R.id.btnSave)

        btnSave.setOnClickListener {
            simpanSemuaDataKeFirebase()
        }
    }

    private fun checkIfIdAlreadyUploaded(searchedIds: Long) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("add_data")
        val searchedIdsDouble = searchedIds.toDouble()

        // Cek apakah ID sudah ada di dalam database "add_data"
        databaseReference.orderByChild("noCiff").equalTo(searchedIdsDouble)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Jika ID ditemukan, beri pesan error bahwa ID sudah pernah diupload
                        Toast.makeText(
                            this@AddDataActivity,
                            "ID sudah pernah diupload!",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        // Jika ID belum ada, lanjutkan ke penyimpanan data
                        Log.d("Firebase", "ID belum pernah diupload, lanjutkan penyimpanan data")
                        // Panggil fungsi untuk menyimpan data
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error: ${error.message}")
                }
            })
    }

    private fun ambilDataInput(
        spinner: Spinner,
        etTanggal: EditText,
        etUpload: EditText,
        etKeterangan: EditText
    ): ClassSubData {
        val selectedStatus = spinner.selectedItem?.toString()
        val status = if (selectedStatus == "Pilih Status") "-" else selectedStatus ?: "-"
        val tanggal = etTanggal.text.toString().ifBlank { "-" }
        val uploadData = etUpload.text.toString().ifBlank { "-" }
        val keterangan = etKeterangan.text.toString().ifBlank { "-" }

        return ClassSubData(status, tanggal, uploadData, keterangan)
    }


    private fun simpanSemuaDataKeFirebase() {
        val database = FirebaseDatabase.getInstance().getReference("add_data")
        val namaNasabah = findViewById<EditText>(R.id.etNamaNasabah).text.toString().trim()
        val namaBersih = namaNasabah.substringBefore(" - ").trim()
        val databaseNasabah = FirebaseDatabase.getInstance().getReference("name_nasabah")
        val tanggalUpload = SimpleDateFormat("dd MMMM yyy", Locale.getDefault()).format(Date())
        val realisasi1 = findViewById<EditText>(R.id.input_realisasi).text.toString().ifBlank { "-" }
        val realisasi2 = findViewById<EditText>(R.id.input_realisasi2).text.toString().ifBlank { "-" }
        val realisasi3 = findViewById<EditText>(R.id.input_realisasi3).text.toString().ifBlank { "-" }
        val realisasi4 = findViewById<EditText>(R.id.input_realisasi4).text.toString().ifBlank { "-" }
        val realisasi5 = findViewById<EditText>(R.id.input_realisasi5).text.toString().ifBlank { "-" }
        val realisasi6 = findViewById<EditText>(R.id.input_realisasi6).text.toString().ifBlank { "-" }
        val realisasi7 = findViewById<EditText>(R.id.input_realisasi7).text.toString().ifBlank { "-" }
        val realisasi8 = findViewById<EditText>(R.id.input_realisasi8).text.toString().ifBlank { "-" }
        val realisasi9 = findViewById<EditText>(R.id.input_realisasi9).text.toString().ifBlank { "-" }
        val realisasi10 = findViewById<EditText>(R.id.input_realisasi10).text.toString().ifBlank { "-" }



        // Ambil semua input dari 7 set
        val data0 = ambilDataInput(
            binding.spinnerStatusPeringatan,
            binding.etTanggalPeringatan,
            binding.etUploadDataPeringatan,
            binding.etKeteranganPeringatan
        )
        val data1 = ambilDataInput(
            binding.spinnerStatus1,
            binding.etTanggal1,
            binding.etUploadData1,
            binding.etKeterangan1
        )
        val data2 = ambilDataInput(
            binding.spinnerStatus2,
            binding.etTanggal2,
            binding.etUploadData2,
            binding.etKeterangan2
        )
        val data3 = ambilDataInput(
            binding.spinnerStatus3,
            binding.etTanggal3,
            binding.etUploadData3,
            binding.etKeterangan3
        )
        val data4 = ambilDataInput(
            binding.spinnerStatusPanggilan,
            binding.etTanggalPanggilan,
            binding.etUploadDataPanggilan,
            binding.etKeteranganPanggilan
        )
        val data5 = ambilDataInput(
            binding.spinnerStatusPenyemprotan,
            binding.etTanggalPenyemprotan,
            binding.etUploadDataPenyemprotan,
            binding.etKeteranganPenyemprotan
        )
        val data6 = ambilDataInput(
            binding.spinnerStatusEksekusi,
            binding.etTanggalEksekusi,
            binding.etUploadDataEksekusi,
            binding.etKeteranganEksekusi
        )
        val dataRealisasi = mapOf(
            "realisasi_1" to realisasi1,
            "realisasi_2" to realisasi2,
            "realisasi_3" to realisasi3,
            "realisasi_4" to realisasi4,
            "realisasi_5" to realisasi5,
            "realisasi_6" to realisasi6,
            "realisasi_7" to realisasi7,
            "realisasi_8" to realisasi8,
            "realisasi_9" to realisasi9,
            "realisasi_10" to realisasi10
        )


        // Buat struktur map
        val allData = mapOf(
            "nama_nasabah" to namaNasabah,
            "tanggal_upload" to tanggalUpload,
            "peringatan" to data0,
            "surat_peringatan1" to data1,
            "surat_peringatan2" to data2,
            "surat_peringatan3" to data3,
            "surat_panggilan" to data4,
            "penyemprotan" to data5,
            "eksekusi" to data6,
            "realisasi" to dataRealisasi
        )

        // Ambil referensi ProgressBar
        val progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        // Simpan langsung ke node 'add_data'
        database.push().setValue(allData)
            .addOnSuccessListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Data berhasil disimpan!", Toast.LENGTH_SHORT).show()
                finish() // Tutup activity atau pindah ke fragment lain
            }
            .addOnFailureListener {
                progressBar.visibility = View.GONE
                Toast.makeText(this, "Gagal menyimpan data!", Toast.LENGTH_SHORT).show()
            }
    }

    }
