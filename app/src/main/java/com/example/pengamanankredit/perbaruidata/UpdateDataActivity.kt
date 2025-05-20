package com.example.pengamanankredit.perbaruidata

import android.app.DatePickerDialog
import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.pengamanankredit.R
import com.example.pengamanankredit.tambahdata.ClassDataAdd
import com.google.firebase.database.*
import java.util.Calendar

class UpdateDataActivity : AppCompatActivity() {

    private lateinit var etNamaNasabah: EditText
    private var realisasiCount = 1

    private lateinit var spinnerStatusPeringatanUpdate: Spinner
    private lateinit var etTanggalPeringatanUpdate: EditText
    private lateinit var etUploadDataPeringatanUpdate: EditText
    private lateinit var etKeteranganPeringatanUpdate: EditText

    private lateinit var spinnerStatusPeringatan1Update: Spinner
    private lateinit var etTanggalPeringatan1Update: EditText
    private lateinit var etUploadDataPeringatan1Update: EditText
    private lateinit var etKeteranganPeringatan1Update: EditText

    private lateinit var spinnerStatusPeringatan2Update: Spinner
    private lateinit var etTanggalPeringatan2Update: EditText
    private lateinit var etUploadDataPeringatan2Update: EditText
    private lateinit var etKeteranganPeringatan2Update: EditText

    private lateinit var spinnerStatusPeringatan3Update: Spinner
    private lateinit var etTanggalPeringatan3Update: EditText
    private lateinit var etUploadDataPeringatan3Update: EditText
    private lateinit var etKeteranganPeringatan3Update: EditText

    private lateinit var spinnerStatusPanggilanUpdate: Spinner
    private lateinit var etTanggalPanggilanUpdate: EditText
    private lateinit var etUploadDataPanggilanUpdate: EditText
    private lateinit var etKeteranganPanggilanUpdate: EditText

    private lateinit var spinnerStatusPenyemprotanUpdate: Spinner
    private lateinit var etTanggalPenyemprotanUpdate: EditText
    private lateinit var etUploadDataPenyemprotanUpdate: EditText
    private lateinit var etKeteranganPenyemprotanUpdate: EditText


    private lateinit var spinnerStatusEksekusiUpdate: Spinner
    private lateinit var etTanggalEksekusiUpdate: EditText
    private lateinit var etUploadDataEksekusiUpdate: EditText
    private lateinit var etKeteranganEksekusiUpdate: EditText


    private lateinit var inputRealisasi1: EditText
    private lateinit var inputRealisasi2: EditText
    private lateinit var inputRealisasi3: EditText
    private lateinit var inputRealisasi4: EditText
    private lateinit var inputRealisasi5: EditText
    private lateinit var inputRealisasi6: EditText
    private lateinit var inputRealisasi7: EditText
    private lateinit var inputRealisasi8: EditText
    private lateinit var inputRealisasi9: EditText
    private lateinit var inputRealisasi10: EditText


    private lateinit var progressDialog: ProgressDialog


    private lateinit var btnSimpan: Button
    private var dataRefToUpdate: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_data)

        //Section Peringatan
        val contentPeringatan = findViewById<LinearLayout>(R.id.contentPeringatanUpdate)
        val iconToggle = findViewById<ImageView>(R.id.iconTogglePeringatanUpdate)

        iconToggle.setOnClickListener {
            if (contentPeringatan.visibility == View.GONE) {
                contentPeringatan.visibility = View.VISIBLE
                iconToggle.setImageResource(R.drawable.ic_expand_less)
            } else {
                contentPeringatan.visibility = View.GONE
                iconToggle.setImageResource(R.drawable.ic_expand_more)
            }
        }

        etTanggalPeringatanUpdate = findViewById(R.id.etTanggalPeringatanUpdate)
        spinnerStatusPeringatanUpdate = findViewById(R.id.spinnerStatusPeringatanUpdate)


        etTanggalPeringatanUpdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPeringatanUpdate.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }


        // Data pilihan untuk spinner
        val statusList = listOf("Pilih Status", "Pending", "Proses", "Selesai")

        // Adapter untuk spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPeringatanUpdate.adapter = adapter


        //Section Surat Peringatan 1
        val contentPeringatan1 = findViewById<LinearLayout>(R.id.contentPeringatan1Update)
        val iconToggle1 = findViewById<ImageView>(R.id.iconTogglePeringatan1Update)

        iconToggle1.setOnClickListener {
            if (contentPeringatan1.visibility == View.GONE) {
                contentPeringatan1.visibility = View.VISIBLE
                iconToggle1.setImageResource(R.drawable.ic_expand_less)
            } else {
                contentPeringatan1.visibility = View.GONE
                iconToggle1.setImageResource(R.drawable.ic_expand_more)
            }
        }

        etTanggalPeringatan1Update = findViewById(R.id.etTanggalPeringatan1Update)
        spinnerStatusPeringatan1Update = findViewById(R.id.spinnerStatusPeringatan1Update)


        etTanggalPeringatan1Update.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPeringatan1Update.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }


        // Data pilihan untuk spinner
        val statusListPeringatan1 = listOf("Pilih Status", "Pending", "Proses", "Selesai")

        // Adapter untuk spinner
        val adapterPeringatan1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListPeringatan1)
        adapterPeringatan1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPeringatan1Update.adapter = adapterPeringatan1



        //Section Surat Peringatan 2
        val contentPeringatan2 = findViewById<LinearLayout>(R.id.contentPeringatan2Update)
        val iconToggle2 = findViewById<ImageView>(R.id.iconTogglePeringatan2Update)

        iconToggle2.setOnClickListener {
            if (contentPeringatan2.visibility == View.GONE) {
                contentPeringatan2.visibility = View.VISIBLE
                iconToggle2.setImageResource(R.drawable.ic_expand_less)
            } else {
                contentPeringatan2.visibility = View.GONE
                iconToggle2.setImageResource(R.drawable.ic_expand_more)
            }
        }

        etTanggalPeringatan2Update = findViewById(R.id.etTanggalPeringatan2Update)
        spinnerStatusPeringatan2Update = findViewById(R.id.spinnerStatusPeringatan2Update)


        etTanggalPeringatan2Update.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPeringatan2Update.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }


        // Data pilihan untuk spinner
        val statusListPeringatan2 = listOf("Pilih Status", "Pending", "Proses", "Selesai")

        // Adapter untuk spinner
        val adapterPeringatan2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListPeringatan2)
        adapterPeringatan2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPeringatan2Update.adapter = adapterPeringatan2




        //Section Surat Peringatan 3
        val contentPeringatan3 = findViewById<LinearLayout>(R.id.contentPeringatan3Update)
        val iconToggle3 = findViewById<ImageView>(R.id.iconTogglePeringatan3Update)

        iconToggle3.setOnClickListener {
            if (contentPeringatan3.visibility == View.GONE) {
                contentPeringatan3.visibility = View.VISIBLE
                iconToggle3.setImageResource(R.drawable.ic_expand_less)
            } else {
                contentPeringatan3.visibility = View.GONE
                iconToggle3.setImageResource(R.drawable.ic_expand_more)
            }
        }

        etTanggalPeringatan3Update = findViewById(R.id.etTanggalPeringatan3Update)
        spinnerStatusPeringatan3Update = findViewById(R.id.spinnerStatusPeringatan3Update)


        etTanggalPeringatan3Update.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPeringatan3Update.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }


        // Data pilihan untuk spinner
        val statusListPeringatan3 = listOf("Pilih Status", "Pending", "Proses", "Selesai")

        // Adapter untuk spinner
        val adapterPeringatan3 = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListPeringatan3)
        adapterPeringatan3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPeringatan3Update.adapter = adapterPeringatan3


        //Section Surat Panggilan
        val contentPanggilan = findViewById<LinearLayout>(R.id.contentPanggilanUpdate)
        val iconTogglePanggilan = findViewById<ImageView>(R.id.iconTogglePanggilanUpdate)

        iconTogglePanggilan.setOnClickListener {
            if (contentPanggilan.visibility == View.GONE) {
                contentPanggilan.visibility = View.VISIBLE
                iconTogglePanggilan.setImageResource(R.drawable.ic_expand_less)
            } else {
                contentPanggilan.visibility = View.GONE
                iconTogglePanggilan.setImageResource(R.drawable.ic_expand_more)
            }
        }

        etTanggalPanggilanUpdate = findViewById(R.id.etTanggalPanggilanUpdate)
        spinnerStatusPanggilanUpdate = findViewById(R.id.spinnerStatusPanggilanUpdate)


        etTanggalPanggilanUpdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPanggilanUpdate.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }


        // Data pilihan untuk spinner
        val statusListPanggilan = listOf("Pilih Status", "Pending", "Proses", "Selesai")

        // Adapter untuk spinner
        val adapterPanggilan = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListPanggilan)
        adapterPanggilan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPanggilanUpdate.adapter = adapterPanggilan



        //Section Penyemprotan/Sticker
        val contentPenyemprotan = findViewById<LinearLayout>(R.id.contentPenyemprotanUpdate)
        val iconTogglePenyemprotan = findViewById<ImageView>(R.id.iconTogglePenyemprotanUpdate)

        iconTogglePenyemprotan.setOnClickListener {
            if (contentPenyemprotan.visibility == View.GONE) {
                contentPenyemprotan.visibility = View.VISIBLE
                iconTogglePenyemprotan.setImageResource(R.drawable.ic_expand_less)
            } else {
                contentPenyemprotan.visibility = View.GONE
                iconTogglePenyemprotan.setImageResource(R.drawable.ic_expand_more)
            }
        }

        etTanggalPenyemprotanUpdate = findViewById(R.id.etTanggalPenyemprotanUpdate)
        spinnerStatusPenyemprotanUpdate = findViewById(R.id.spinnerStatusPenyemprotanUpdate)


        etTanggalPenyemprotanUpdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalPenyemprotanUpdate.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }


        // Data pilihan untuk spinner
        val statusListPenyemprotan = listOf("Pilih Status", "Pending", "Proses", "Selesai")

        // Adapter untuk spinner
        val adapterPenyemprotan = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListPenyemprotan)
        adapterPenyemprotan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPenyemprotanUpdate.adapter = adapterPenyemprotan



        //Section Eksekusi
        val contentEksekusi = findViewById<LinearLayout>(R.id.contentEksekusiUpdate)
        val iconToggleEksekusi = findViewById<ImageView>(R.id.iconToggleEksekusiUpdate)

        iconToggleEksekusi.setOnClickListener {
            if (contentEksekusi.visibility == View.GONE) {
                contentEksekusi.visibility = View.VISIBLE
                iconToggleEksekusi.setImageResource(R.drawable.ic_expand_less)
            } else {
                contentEksekusi.visibility = View.GONE
                iconToggleEksekusi.setImageResource(R.drawable.ic_expand_more)
            }
        }

        etTanggalEksekusiUpdate = findViewById(R.id.etTanggalEksekusiUpdate)
        spinnerStatusEksekusiUpdate = findViewById(R.id.spinnerStatusEksekusiUpdate)


        etTanggalEksekusiUpdate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                etTanggalEksekusiUpdate.setText(selectedDate)
            }, year, month, day)

            datePicker.show()
        }


        // Data pilihan untuk spinner
        val statusListEksekusi = listOf("Pilih Status", "Pending", "Proses", "Selesai")

        // Adapter untuk spinner
        val adapterEksekusi = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusListEksekusi)
        adapterEksekusi.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusEksekusiUpdate.adapter = adapterEksekusi



        //REALISASI
        val contentRealisasi = findViewById<LinearLayout>(R.id.contentRealisasiUpdate)
        val iconToggleRealisasi = findViewById<ImageView>(R.id.iconToggleRealisasiUpdate)

        iconToggleRealisasi.setOnClickListener {
            if (contentRealisasi.visibility == View.GONE) {
                contentRealisasi.visibility = View.VISIBLE
                iconToggleRealisasi.setImageResource(R.drawable.ic_expand_less) // ubah icon ke panah atas
            } else {
                contentRealisasi.visibility = View.GONE
                iconToggleRealisasi.setImageResource(R.drawable.ic_expand_more) // ubah icon ke panah bawah
            }
        }

        val addRealisasiButton: ImageView = findViewById(R.id.add_realisasi)
        addRealisasiButton.setOnClickListener {
            if (realisasiCount < 10) {
                realisasiCount++

                val textView = when (realisasiCount) {
                    2 -> findViewById<TextView>(R.id.text_realisasi2)
                    3 -> findViewById<TextView>(R.id.text_realisasi3)
                    4 -> findViewById<TextView>(R.id.text_realisasi4)
                    5 -> findViewById<TextView>(R.id.text_realisasi5)
                    6 -> findViewById<TextView>(R.id.text_realisasi6)
                    7 -> findViewById<TextView>(R.id.text_realisasi7)
                    8 -> findViewById<TextView>(R.id.text_realisasi8)
                    9 -> findViewById<TextView>(R.id.text_realisasi9)
                    10 -> findViewById<TextView>(R.id.text_realisasi10)
                    else -> null
                }
                val editText = when (realisasiCount) {
                    2 -> findViewById<EditText>(R.id.input_realisasi2_update)
                    3 -> findViewById<EditText>(R.id.input_realisasi3_update)
                    4 -> findViewById<EditText>(R.id.input_realisasi4_update)
                    5 -> findViewById<EditText>(R.id.input_realisasi5_update)
                    6 -> findViewById<EditText>(R.id.input_realisasi6_update)
                    7 -> findViewById<EditText>(R.id.input_realisasi7_update)
                    8 -> findViewById<EditText>(R.id.input_realisasi8_update)
                    9 -> findViewById<EditText>(R.id.input_realisasi9_update)
                    10 -> findViewById<EditText>(R.id.input_realisasi10_update)
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



        // Inisialisasi view
        etNamaNasabah = findViewById(R.id.etNamaNasabah)
        etUploadDataPeringatanUpdate = findViewById(R.id.etUploadDataKeteranganUpdate)
        etKeteranganPeringatanUpdate = findViewById(R.id.etKeteranganPeringatanUpdate)
        spinnerStatusPeringatanUpdate = findViewById(R.id.spinnerStatusPeringatanUpdate)
        etTanggalPeringatanUpdate = findViewById(R.id.etTanggalPeringatanUpdate)


        etUploadDataPeringatan1Update = findViewById(R.id.etUploadDataPeringatan1Update)
        etKeteranganPeringatan1Update = findViewById(R.id.etKeteranganPeringatan1Update)
        spinnerStatusPeringatan1Update = findViewById(R.id.spinnerStatusPeringatan1Update)
        etTanggalPeringatan1Update = findViewById(R.id.etTanggalPeringatan1Update)


        etUploadDataPeringatan2Update = findViewById(R.id.etUploadDataPeringatan2Update)
        etKeteranganPeringatan2Update = findViewById(R.id.etKeteranganPeringatan2Update)
        spinnerStatusPeringatan2Update = findViewById(R.id.spinnerStatusPeringatan2Update)
        etTanggalPeringatan2Update = findViewById(R.id.etTanggalPeringatan2Update)


        etUploadDataPeringatan3Update = findViewById(R.id.etUploadDataPeringatan3Update)
        etKeteranganPeringatan3Update = findViewById(R.id.etKeteranganPeringatan3Update)
        spinnerStatusPeringatan3Update = findViewById(R.id.spinnerStatusPeringatan3Update)
        etTanggalPeringatan3Update = findViewById(R.id.etTanggalPeringatan3Update)


        etUploadDataPanggilanUpdate = findViewById(R.id.etUploadDataPanggilanUpdate)
        etKeteranganPanggilanUpdate = findViewById(R.id.etKeteranganPanggilanUpdate)
        spinnerStatusPanggilanUpdate = findViewById(R.id.spinnerStatusPanggilanUpdate)
        etTanggalPanggilanUpdate = findViewById(R.id.etTanggalPanggilanUpdate)


        etUploadDataPenyemprotanUpdate = findViewById(R.id.etUploadDataPenyemprotanUpdate)
        etKeteranganPenyemprotanUpdate = findViewById(R.id.etKeteranganPenyemprotanUpdate)
        spinnerStatusPenyemprotanUpdate = findViewById(R.id.spinnerStatusPenyemprotanUpdate)
        etTanggalPenyemprotanUpdate = findViewById(R.id.etTanggalPenyemprotanUpdate)



        etUploadDataEksekusiUpdate = findViewById(R.id.etUploadDataEksekusiUpdate)
        etKeteranganEksekusiUpdate = findViewById(R.id.etKeteranganEksekusiUpdate)
        spinnerStatusEksekusiUpdate = findViewById(R.id.spinnerStatusEksekusiUpdate)
        etTanggalEksekusiUpdate = findViewById(R.id.etTanggalEksekusiUpdate)


        inputRealisasi1 = findViewById(R.id.input_realisasi_update)
        inputRealisasi2 = findViewById(R.id.input_realisasi2_update)
        inputRealisasi3 = findViewById(R.id.input_realisasi3_update)
        inputRealisasi4 = findViewById(R.id.input_realisasi4_update)
        inputRealisasi5 = findViewById(R.id.input_realisasi5_update)
        inputRealisasi6 = findViewById(R.id.input_realisasi6_update)
        inputRealisasi7 = findViewById(R.id.input_realisasi7_update)
        inputRealisasi8 = findViewById(R.id.input_realisasi8_update)
        inputRealisasi9 = findViewById(R.id.input_realisasi9_update)
        inputRealisasi10 = findViewById(R.id.input_realisasi10_update)



        btnSimpan = findViewById(R.id.btnSave)

        val namaNasabah = intent.getStringExtra("namaNasabah") // Menerima namaNasabah yang dikirim


        // Ambil data dari Firebase berdasarkan namaNasabah
        val ref = FirebaseDatabase.getInstance()
            .getReference("add_data")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var found = false
                for (child in snapshot.children) {
                    val nama = child.child("nama_nasabah").getValue(String::class.java)
                    if (nama == namaNasabah) {
                        dataRefToUpdate = child.ref

                        // Isi data ke form
                        etNamaNasabah.setText(nama ?: "")

                        val peringatan = child.child("peringatan")
                        etTanggalPeringatanUpdate.setText(peringatan.child("tanggal").value?.toString() ?: "")
                        etUploadDataPeringatanUpdate.setText(peringatan.child("uploadData").value?.toString() ?: "")
                        etKeteranganPeringatanUpdate.setText(peringatan.child("keterangan").value?.toString() ?: "")
                        setSpinnerSelection(spinnerStatusPeringatanUpdate, peringatan.child("status").value?.toString() ?: "")

                        val peringatan1 = child.child("surat_peringatan1")
                        etTanggalPeringatan1Update.setText(peringatan1.child("tanggal").value?.toString() ?: "")
                        etUploadDataPeringatan1Update.setText(peringatan1.child("uploadData").value?.toString() ?: "")
                        etKeteranganPeringatan1Update.setText(peringatan1.child("keterangan").value?.toString() ?: "")
                        setSpinnerSelection(spinnerStatusPeringatan1Update, peringatan1.child("status").value?.toString() ?: "")

                        val peringatan2 = child.child("surat_peringatan2")
                        etTanggalPeringatan2Update.setText(peringatan2.child("tanggal").value?.toString() ?: "")
                        etUploadDataPeringatan2Update.setText(peringatan2.child("uploadData").value?.toString() ?: "")
                        etKeteranganPeringatan2Update.setText(peringatan2.child("keterangan").value?.toString() ?: "")
                        setSpinnerSelection(spinnerStatusPeringatan2Update, peringatan2.child("status").value?.toString() ?: "")

                        val peringatan3 = child.child("surat_peringatan3")
                        etTanggalPeringatan3Update.setText(peringatan3.child("tanggal").value?.toString() ?: "")
                        etUploadDataPeringatan3Update.setText(peringatan3.child("uploadData").value?.toString() ?: "")
                        etKeteranganPeringatan3Update.setText(peringatan3.child("keterangan").value?.toString() ?: "")
                        setSpinnerSelection(spinnerStatusPeringatan3Update, peringatan3.child("status").value?.toString() ?: "")


                        val panggilan = child.child("surat_panggilan")
                        etTanggalPanggilanUpdate.setText(panggilan.child("tanggal").value?.toString() ?: "")
                        etUploadDataPanggilanUpdate.setText(panggilan.child("uploadData").value?.toString() ?: "")
                        etKeteranganPanggilanUpdate.setText(panggilan.child("keterangan").value?.toString() ?: "")
                        setSpinnerSelection(spinnerStatusPanggilanUpdate, panggilan.child("status").value?.toString() ?: "")


                        val penyemprotan = child.child("penyemprotan")
                        etTanggalPenyemprotanUpdate.setText(penyemprotan.child("tanggal").value?.toString() ?: "")
                        etUploadDataPenyemprotanUpdate.setText(penyemprotan.child("uploadData").value?.toString() ?: "")
                        etKeteranganPenyemprotanUpdate.setText(penyemprotan.child("keterangan").value?.toString() ?: "")
                        setSpinnerSelection(spinnerStatusPenyemprotanUpdate, penyemprotan.child("status").value?.toString() ?: "")


                        val eksekusi = child.child("eksekusi")
                        etTanggalEksekusiUpdate.setText(eksekusi.child("tanggal").value?.toString() ?: "")
                        etUploadDataEksekusiUpdate.setText(eksekusi.child("uploadData").value?.toString() ?: "")
                        etKeteranganEksekusiUpdate.setText(eksekusi.child("keterangan").value?.toString() ?: "")
                        setSpinnerSelection(spinnerStatusEksekusiUpdate, eksekusi.child("status").value?.toString() ?: "")


                        val realisasi = child.child("realisasi")
                        inputRealisasi1.setText(realisasi.child("realisasi_1").value?.toString() ?: "")
                        inputRealisasi2.setText(realisasi.child("realisasi_2").value?.toString() ?: "")
                        inputRealisasi3.setText(realisasi.child("realisasi_3").value?.toString() ?: "")
                        inputRealisasi4.setText(realisasi.child("realisasi_4").value?.toString() ?: "")
                        inputRealisasi5.setText(realisasi.child("realisasi_5").value?.toString() ?: "")
                        inputRealisasi6.setText(realisasi.child("realisasi_6").value?.toString() ?: "")
                        inputRealisasi7.setText(realisasi.child("realisasi_7").value?.toString() ?: "")
                        inputRealisasi8.setText(realisasi.child("realisasi_8").value?.toString() ?: "")
                        inputRealisasi9.setText(realisasi.child("realisasi_9").value?.toString() ?: "")
                        inputRealisasi10.setText(realisasi.child("realisasi_10").value?.toString() ?: "")


                        found = true
                        break
                    }
                }

                if (!found) {
                    Toast.makeText(this@UpdateDataActivity, "Nama nasabah tidak ditemukan!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UpdateDataActivity, "Gagal memuat data!", Toast.LENGTH_SHORT).show()
            }
        })


        // Tombol untuk menyimpan perubahan
        btnSimpan.setOnClickListener {
            if (dataRefToUpdate != null) {
                // Tampilkan progress dialog
                progressDialog = ProgressDialog(this)
                progressDialog.setMessage("Memproses pembaruan data, mohon tunggu..")
                progressDialog.setCancelable(false) // agar tidak bisa ditutup saat loading
                progressDialog.show()

                updateData()
            } else {
                Toast.makeText(this, "Referensi data belum siap!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Fungsi untuk memperbarui data di Firebase
    private fun updateData() {

        val status = spinnerStatusPeringatanUpdate.selectedItem?.toString()?.takeIf { it != "Pilih Status" } ?: "-"
        val status1 = spinnerStatusPeringatan1Update.selectedItem?.toString()?.takeIf { it != "Pilih Status" } ?: "-"
        val status2 = spinnerStatusPeringatan2Update.selectedItem?.toString()?.takeIf { it != "Pilih Status" } ?: "-"
        val status3 = spinnerStatusPeringatan3Update.selectedItem?.toString()?.takeIf { it != "Pilih Status" } ?: "-"
        val statuspanggilan = spinnerStatusPanggilanUpdate.selectedItem?.toString()?.takeIf { it != "Pilih Status" } ?: "-"
        val statuspenyemprotan = spinnerStatusPenyemprotanUpdate.selectedItem?.toString()?.takeIf { it != "Pilih Status" } ?: "-"
        val statuseksekusi = spinnerStatusEksekusiUpdate.selectedItem?.toString()?.takeIf { it != "Pilih Status" } ?: "-"

        val updatedPeringatan = mapOf(
            "status" to status,
            "tanggal" to etTanggalPeringatanUpdate.text.toString(),
            "uploadData" to etUploadDataPeringatanUpdate.text.toString(),
            "keterangan" to etKeteranganPeringatanUpdate.text.toString()
        )

        val updatedPeringatan1 = mapOf(
            "status" to status1,
            "tanggal" to etTanggalPeringatan1Update.text.toString(),
            "uploadData" to etUploadDataPeringatan1Update.text.toString(),
            "keterangan" to etKeteranganPeringatan1Update.text.toString()
        )

        val updatedPeringatan2 = mapOf(
            "status" to status2,
            "tanggal" to etTanggalPeringatan2Update.text.toString(),
            "uploadData" to etUploadDataPeringatan2Update.text.toString(),
            "keterangan" to etKeteranganPeringatan2Update.text.toString()
        )

        val updatedPeringatan3 = mapOf(
            "status" to status3,
            "tanggal" to etTanggalPeringatan3Update.text.toString(),
            "uploadData" to etUploadDataPeringatan3Update.text.toString(),
            "keterangan" to etKeteranganPeringatan3Update.text.toString()
        )

        val updatedPanggilan = mapOf(
            "status" to statuspanggilan,
            "tanggal" to etTanggalPanggilanUpdate.text.toString(),
            "uploadData" to etUploadDataPanggilanUpdate.text.toString(),
            "keterangan" to etKeteranganPanggilanUpdate.text.toString()
        )


        val updatedPenyemprotan = mapOf(
            "status" to statuspenyemprotan,
            "tanggal" to etTanggalPenyemprotanUpdate.text.toString(),
            "uploadData" to etUploadDataPenyemprotanUpdate.text.toString(),
            "keterangan" to etKeteranganPenyemprotanUpdate.text.toString()
        )


        val updatedEksekusi = mapOf(
            "status" to statuseksekusi,
            "tanggal" to etTanggalEksekusiUpdate.text.toString(),
            "uploadData" to etUploadDataEksekusiUpdate.text.toString(),
            "keterangan" to etKeteranganEksekusiUpdate.text.toString()
        )

        val updatedRealisasi = mapOf(
            "realisasi_1" to inputRealisasi1.text.toString(),
            "realisasi_2" to inputRealisasi2.text.toString(),
            "realisasi_3" to inputRealisasi3.text.toString(),
            "realisasi_4" to inputRealisasi4.text.toString(),
            "realisasi_5" to inputRealisasi5.text.toString(),
            "realisasi_6" to inputRealisasi6.text.toString(),
            "realisasi_7" to inputRealisasi7.text.toString(),
            "realisasi_8" to inputRealisasi8.text.toString(),
            "realisasi_9" to inputRealisasi9.text.toString(),
            "realisasi_10" to inputRealisasi10.text.toString()
        )

        val updates = mapOf(
            "peringatan" to updatedPeringatan,
            "surat_peringatan1" to updatedPeringatan1,
            "surat_peringatan2" to updatedPeringatan2,
            "surat_peringatan3" to updatedPeringatan3,
            "surat_panggilan" to updatedPanggilan,
            "penyemprotan" to updatedPenyemprotan,
            "eksekusi" to updatedEksekusi,
            "realisasi" to updatedRealisasi
        )

        dataRefToUpdate?.updateChildren(updates)?.addOnSuccessListener {
            Toast.makeText(this, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
            finish()
        }?.addOnFailureListener {
            Toast.makeText(this, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
        }
    }

    // Fungsi untuk mengatur pemilihan nilai di Spinner
    private fun setSpinnerSelection(spinner: Spinner, value: String) {
        val adapter = spinner.adapter ?: return
        for (i in 0 until adapter.count) {
            if (adapter.getItem(i).toString().equals(value, ignoreCase = true)) {
                spinner.setSelection(i)
                break
            }
        }
    }
}