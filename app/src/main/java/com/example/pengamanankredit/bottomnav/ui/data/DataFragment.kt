package com.example.pengamanankredit.bottomnav.ui.data

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pengamanankredit.R
import com.example.pengamanankredit.databinding.FragmentDataBinding
import com.example.pengamanankredit.tambahdata.AddDataActivity
import com.example.pengamanankredit.tambahdata.ClassDataAdd
import com.example.pengamanankredit.tambahdata.DetailDataActivity
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DataFragment : Fragment() {

    private var _binding: FragmentDataBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FirebaseRecyclerAdapter<ClassDataAdd, ReportViewHolder>
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressDialog: ProgressDialog
    private var selectedItems = mutableSetOf<Int>()
    private var isSelectionMode = false
    private lateinit var checkBoxSelectAll: CheckBox
    private lateinit var layoutSelectionMode: LinearLayout
    private lateinit var imgDownload: ImageView
    private val database = FirebaseDatabase.getInstance().getReference("add_data")
    private var isSearching = false


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dataViewModel =
            ViewModelProvider(this).get(DataViewModel::class.java)

        _binding = FragmentDataBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // >>> Tambahkan ini untuk mengubah nama Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Data"
        binding.searchViewData.setIconifiedByDefault(false)
        binding.searchViewData.isIconified = false
        binding.searchViewData.isFocusable = true
        binding.searchViewData.isFocusableInTouchMode = true
        binding.searchViewData.isSubmitButtonEnabled = true
        binding.searchViewData.clearFocus()
        binding.searchViewData.queryHint = "Cari data nasabah..."

        requireActivity().window.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING
        )

        handleBackPress()

        binding.searchViewData.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchDataSelengkapnya(it)
                    searchData(query ?: "")
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                searchData(newText ?: "")
                return true
            }
        })

        val fabAdd: FloatingActionButton = view.findViewById(R.id.fabAdd)

        fabAdd.setOnClickListener {
            val intent = Intent(requireContext(), AddDataActivity::class.java)
            startActivity(intent)
        }


        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext()).apply {
            reverseLayout = true
        }

        layoutSelectionMode = binding.layoutSelectionMode
        checkBoxSelectAll = binding.checkBoxSelectAll
        imgDownload = binding.imgDownload

        progressDialog = ProgressDialog(requireContext()).apply {
            setTitle("Loading")
            setMessage("Silakan tunggu...")
        }

        loadDataFromDatabase()
        adapter.notifyDataSetChanged()

        imgDownload.setOnClickListener {
            Log.d("EXPORT_DEBUG", "🔹 Tombol Download ditekan")

            if (selectedItems.isNotEmpty()) {
                val selectedData = mutableListOf<ClassDataAdd>()
                for (index in selectedItems) {
                    val model = adapter.getItem(index)
                    selectedData.add(model)
                }
                Log.d("EXPORT_DEBUG", "🔹 Menyimpan data yang dipilih ke Excel")
                fetchDataFromFirebase(selectedData)
            } else {
                // Tampilkan Toast jika tidak ada item yang dipilih
                Toast.makeText(requireContext(), "Pilih minimal satu item untuk diunduh!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    // Navigasi ke HomeFragment
                    findNavController().navigate(R.id.navigation_home)
                }
            }
        )
    }

    private fun loadDataFromDatabase() {
        progressDialog.show()
        val query = database.orderByChild("timestamp")

        val options = FirebaseRecyclerOptions.Builder<ClassDataAdd>()
            .setQuery(query, ClassDataAdd::class.java)
            .build()

        if (::adapter.isInitialized) {
            adapter.stopListening()
        }

        adapter = object : FirebaseRecyclerAdapter<ClassDataAdd, ReportViewHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {

                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_add_data, parent, false)
                return ReportViewHolder(view)
            }

            override fun onBindViewHolder(holder: ReportViewHolder, position: Int, model: ClassDataAdd) {
                Log.d("Adapter", "Binding data: ${model.nama_nasabah}, ${model.tanggal_upload}")
                progressDialog.dismiss()

                // Setel visibilitas checkbox berdasarkan mode seleksi
                holder.checkBox.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
                holder.checkBox.isChecked = selectedItems.contains(position)

                val namaStatusGabung = "${model.nama_nasabah}"
                holder.tvNameStatus.text = namaStatusGabung

                // Tampilkan tanggal upload
                val tanggalUploadStr = model.tanggal_upload ?: "Tanggal tidak tersedia"
                holder.tvTimestamp.text = "Diunggah pada tanggal $tanggalUploadStr"


                val itemKey = getRef(position).key


                // Klik item → Buka detail jika tidak dalam mode seleksi
                holder.itemView.setOnClickListener {
                    if (isSelectionMode) {
                        toggleSelection(position)
                    } else {
                        val context = holder.itemView.context
                        val intent = Intent(context, DetailDataActivity::class.java).apply {
                            putExtra("id", itemKey)
                            putExtra("namaNasabah", model.nama_nasabah)
                        }
                        context.startActivity(intent)
                    }
                }

                // Tekan lama → Masuk mode seleksi dan tampilkan checkbox
                holder.itemView.setOnLongClickListener {
                    if (!isSelectionMode) {
                        isSelectionMode = true
                        notifySelectionModeChanged()
                        notifyDataSetChanged()
                    }
                    toggleSelection(position)
                    true
                }
            }


            override fun onDataChanged() {
                super.onDataChanged()
                recyclerView.post {
                    adapter.notifyDataSetChanged()
                }
                progressDialog.dismiss()
                if (!isSearching) { // ✅ Tampilkan FAB hanya jika tidak sedang mencari
                    binding.fabAdd.visibility = View.VISIBLE
                }
            }
        }

        recyclerView.adapter = adapter
        adapter.startListening() // ✅ Mulai mendengarkan perubahan data

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                recyclerView.postDelayed({
                    recyclerView.scrollToPosition(adapter.itemCount - 1) // ✅ Scroll ke atas
                }, 300)
                progressDialog.dismiss()
            }
        })

        checkBoxSelectAll?.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedItems.clear()
                for (i in 0 until adapter.itemCount) {
                    selectedItems.add(i)
                }
            } else {
                selectedItems.clear()
            }
            notifySelectionModeChanged()
            adapter.notifyDataSetChanged()
        }
    }

    class ReportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNameStatus: TextView = itemView.findViewById(R.id.tvNameStatus)
        val tvTimestamp: TextView = itemView.findViewById(R.id.tvTimestamp)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkBox)

    }

    private fun toggleSelection(position: Int) {
        if (selectedItems.contains(position)) {
            selectedItems.remove(position)
        } else {
            selectedItems.add(position)
        }

        // Jika tidak ada item yang dipilih, keluar dari mode seleksi
        if (selectedItems.isEmpty()) {
            isSelectionMode = false
            notifySelectionModeChanged()
        }

        notifySelectionModeChanged()
        adapter.notifyDataSetChanged() // Refresh RecyclerView
    }

    private fun notifySelectionModeChanged() {
        if (isSelectionMode) {
            layoutSelectionMode.visibility = View.VISIBLE // ✅ Ubah jadi terlihat
        } else {
            layoutSelectionMode.visibility =
                View.GONE // ✅ Sembunyikan saat keluar dari mode seleksi
        }

        checkBoxSelectAll.isChecked = selectedItems.size == adapter.itemCount
    }


    fun formatTimestamp(
        tanggalUpload: String?, // String dari database
        tanggalUpdate: String?, // String dari database
        subNode: String,
        namaNasabah: String,
        callback: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance()
            .getReference("add_data").child(subNode)

        database.get().addOnSuccessListener { snapshot ->
            var userName = "Unknown"
            var updatedBy: String? = null

            for (child in snapshot.children) {
                Log.d("DEBUG_DATA", "Key: ${child.key}, Value: ${child.value}, Class: ${child.value?.javaClass}")
                val dbNamaNasabah = child.child("namaNasabah").getValue(String::class.java)
                if (dbNamaNasabah == namaNasabah) {
                    userName = child.child("namaPengguna").getValue(String::class.java) ?: "Unknown"
                    updatedBy = child.child("namaUpdate").getValue(String::class.java) ?: userName
                    break
                }
            }

            // Fungsi untuk memformat tanggal dari "dd/MM/yyyy" ke "dd MMM yyyy"
            fun formatDate(dateStr: String?): String {
                return dateStr?.let {
                    try {
                        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                        val date = inputFormat.parse(it)
                        outputFormat.format(date ?: Date())
                    } catch (e: Exception) {
                        "Format tanggal salah"
                    }
                } ?: "Tanggal tidak tersedia"
            }

            val uploadDateFormatted = formatDate(tanggalUpload)
            val formattedTanggalUpdate = formatDate(tanggalUpdate)

            // Tentukan teks yang akan ditampilkan
            val result = if (tanggalUpdate.isNullOrEmpty()) {
                "Diupload oleh $userName pada tanggal $uploadDateFormatted"
            } else {
                "Diperbarui oleh $updatedBy pada tanggal $formattedTanggalUpdate"
            }

            // Kirim hasil ke callback
            callback(result)
        }.addOnFailureListener {
            callback("Gagal mengambil data pengguna")
        }
    }


    private fun exportToExcelFromMap(dataList: List<Map<String, String>>) {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Data Nasabah")

        if (dataList.isEmpty()) {
            Toast.makeText(requireContext(), "Tidak ada data untuk diekspor", Toast.LENGTH_SHORT).show()
            return
        }

        // Ambil semua kunci unik
        val allKeys = dataList.flatMap { it.keys }.toSet().toList()

        // Header
        val headerRow = sheet.createRow(0)
        for ((index, key) in allKeys.withIndex()) {
            headerRow.createCell(index).setCellValue(key)
        }

        // Data
        for ((rowIndex, dataMap) in dataList.withIndex()) {
            val row = sheet.createRow(rowIndex + 1)
            for ((colIndex, key) in allKeys.withIndex()) {
                row.createCell(colIndex).setCellValue(dataMap[key] ?: "-")
            }
        }

        saveExcelToDownloadFolder(workbook, "DataNasabah.xlsx")
    }


    private fun saveExcelToDownloadFolder(workbook: XSSFWorkbook, fileName: String) {
        val resolver = requireContext().contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(
                MediaStore.Downloads.MIME_TYPE,
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            )
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it)?.use { outputStream ->
                workbook.write(outputStream)
                outputStream.flush()
            }

            Toast.makeText(requireContext(), "File berhasil disimpan di Download", Toast.LENGTH_LONG).show()
        } ?: run {
            Toast.makeText(requireContext(), "Gagal menyimpan file", Toast.LENGTH_SHORT).show()
        }
    }

    private fun searchDataSelengkapnya(keyword: String) {
        val database = FirebaseDatabase.getInstance().getReference("add_data")

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var found = false

                for (nasabah in snapshot.children) {
                    val namaNasabah = nasabah.child("nama_nasabah").getValue(String::class.java) ?: ""

                    if (namaNasabah.contains(keyword, ignoreCase = true)) {
                        found = true

                        //Peringatan

                        val peringatan = nasabah.child("peringatan")
                        val tanggal = peringatan.child("tanggal").getValue(String::class.java) ?: "-"
                        val uploadData = peringatan.child("uploadData").getValue(String::class.java) ?: "-"
                        val keterangan = peringatan.child("keterangan").getValue(String::class.java) ?: "-"
                        val status = peringatan.child("status").getValue(String::class.java) ?: "-"


                        binding.etTanggalPeringatanDetail.text = tanggal
                        binding.etUploadDataPeringatanDetail.text = uploadData
                        binding.etKeteranganPeringatanDetail.text = keterangan
                        binding.spinnerStatusPeringatanDetail.text = status

                        //Surat Peringatan 1

                        val peringatan1 = nasabah.child("surat_peringatan1")
                        val tanggal1 = peringatan1.child("tanggal").getValue(String::class.java) ?: "-"
                        val uploadData1 = peringatan1.child("uploadData").getValue(String::class.java) ?: "-"
                        val keterangan1 = peringatan1.child("keterangan").getValue(String::class.java) ?: "-"
                        val status1 = peringatan1.child("status").getValue(String::class.java) ?: "-"


                        binding.etTanggalSuratPeringatan1Detail.text = tanggal1
                        binding.etUploadDataSuratPeringatan1Detail.text = uploadData1
                        binding.etKeteranganSuratPeringatan1Detail.text = keterangan1
                        binding.spinnerStatusSuratPeringatan1Detail.text = status1


                        //Surat Peringatan 2

                        val peringatan2 = nasabah.child("surat_peringatan2")
                        val tanggal2 = peringatan2.child("tanggal").getValue(String::class.java) ?: "-"
                        val uploadData2 = peringatan2.child("uploadData").getValue(String::class.java) ?: "-"
                        val keterangan2 = peringatan2.child("keterangan").getValue(String::class.java) ?: "-"
                        val status2 = peringatan2.child("status").getValue(String::class.java) ?: "-"


                        binding.etTanggalSuratPeringatan2Detail.text = tanggal2
                        binding.etUploadDataSuratPeringatan2Detail.text = uploadData2
                        binding.etKeteranganSuratPeringatan2Detail.text = keterangan2
                        binding.spinnerStatusSuratPeringatan2Detail.text = status2



                        //Surat Peringatan 3

                        val peringatan3 = nasabah.child("surat_peringatan3")
                        val tanggal3 = peringatan3.child("tanggal").getValue(String::class.java) ?: "-"
                        val uploadData3 = peringatan3.child("uploadData").getValue(String::class.java) ?: "-"
                        val keterangan3 = peringatan3.child("keterangan").getValue(String::class.java) ?: "-"
                        val status3 = peringatan3.child("status").getValue(String::class.java) ?: "-"


                        binding.etTanggalSuratPeringatan3Detail.text = tanggal3
                        binding.etUploadDataSuratPeringatan3Detail.text = uploadData3
                        binding.etKeteranganSuratPeringatan3Detail.text = keterangan3
                        binding.spinnerStatusSuratPeringatan3Detail.text = status3



                        //Surat Panggilan

                        val panggilan = nasabah.child("surat_panggilan")
                        val tanggalPanggilan = panggilan.child("tanggal").getValue(String::class.java) ?: "-"
                        val uploadDataPanggilan = panggilan.child("uploadData").getValue(String::class.java) ?: "-"
                        val keteranganPanggilan = panggilan.child("keterangan").getValue(String::class.java) ?: "-"
                        val statusPanggilan = panggilan.child("status").getValue(String::class.java) ?: "-"


                        binding.etTanggalSuratPanggilanDetail.text = tanggalPanggilan
                        binding.etUploadDataSuratPanggilanDetail.text = uploadDataPanggilan
                        binding.etKeteranganSuratPanggilanDetail.text = keteranganPanggilan
                        binding.spinnerStatusSuratPanggilanDetail.text = statusPanggilan



                        //Penyemprotan/Sticker

                        val penyemprotan = nasabah.child("penyemprotan")
                        val tanggalPenyemprotan = penyemprotan.child("tanggal").getValue(String::class.java) ?: "-"
                        val uploadDataPenyemprotan = penyemprotan.child("uploadData").getValue(String::class.java) ?: "-"
                        val keteranganPenyemprotan = penyemprotan.child("keterangan").getValue(String::class.java) ?: "-"
                        val statusPenyemprotan = penyemprotan.child("status").getValue(String::class.java) ?: "-"


                        binding.etTanggalPenyemprotanDetail.text = tanggalPenyemprotan
                        binding.etUploadDataPenyemprotanDetail.text = uploadDataPenyemprotan
                        binding.etKeteranganPenyemprotanDetail.text = keteranganPenyemprotan
                        binding.spinnerStatusPenyemprotanDetail.text = statusPenyemprotan



                        //Eksekusi

                        val eksekusi = nasabah.child("eksekusi")
                        val tanggalEksekusi = eksekusi.child("tanggal").getValue(String::class.java) ?: "-"
                        val uploadDataEksekusi = eksekusi.child("uploadData").getValue(String::class.java) ?: "-"
                        val keteranganEksekusi = eksekusi.child("keterangan").getValue(String::class.java) ?: "-"
                        val statusEksekusi = eksekusi.child("status").getValue(String::class.java) ?: "-"


                        binding.etTanggalEksekusiDetail.text = tanggalEksekusi
                        binding.etUploadDataEksekusiDetail.text = uploadDataEksekusi
                        binding.etKeteranganEksekusiDetail.text = keteranganEksekusi
                        binding.spinnerStatusEksekusiDetail.text = statusEksekusi



                        //Realisasi

                        val realisasi = nasabah.child("realisasi")
                        val realisasi_1 = realisasi.child("realisasi_1").getValue(String::class.java) ?: "-"
                        val realisasi_2 = realisasi.child("realisasi_2").getValue(String::class.java) ?: "-"
                        val realisasi_3 = realisasi.child("realisasi_3").getValue(String::class.java) ?: "-"
                        val realisasi_4 = realisasi.child("realisasi_4").getValue(String::class.java) ?: "-"
                        val realisasi_5 = realisasi.child("realisasi_5").getValue(String::class.java) ?: "-"
                        val realisasi_6 = realisasi.child("realisasi_6").getValue(String::class.java) ?: "-"
                        val realisasi_7 = realisasi.child("realisasi_7").getValue(String::class.java) ?: "-"
                        val realisasi_8 = realisasi.child("realisasi_8").getValue(String::class.java) ?: "-"
                        val realisasi_9 = realisasi.child("realisasi_9").getValue(String::class.java) ?: "-"
                        val realisasi_10 = realisasi.child("realisasi_10").getValue(String::class.java) ?: "-"


                        binding.realisasi1.text = realisasi_1
                        binding.realisasi2.text = realisasi_2
                        binding.realisasi3.text = realisasi_3
                        binding.realisasi4.text = realisasi_4
                        binding.realisasi5.text = realisasi_5
                        binding.realisasi6.text = realisasi_6
                        binding.realisasi7.text = realisasi_7
                        binding.realisasi8.text = realisasi_8
                        binding.realisasi9.text = realisasi_9
                        binding.realisasi10.text = realisasi_10

                        break
                    }
                }

                if (!found) {
                    Toast.makeText(requireContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Gagal membaca data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchDataFromFirebase(selectedData: List<ClassDataAdd>) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("add_data")

        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    Toast.makeText(requireContext(), "Data kosong", Toast.LENGTH_SHORT).show()
                    return
                }

                val sectionKeys = listOf(
                    "peringatan", "surat_peringatan1", "surat_peringatan2", "surat_peringatan3",
                    "surat_panggilan", "penyemprotan", "eksekusi"
                )

                val exportList = mutableListOf<Map<String, String>>()

                for (dataSnapshot in snapshot.children) {
                    val namaNasabah = dataSnapshot.child("nama_nasabah").getValue(String::class.java) ?: "-"
                    val tanggalUpload = dataSnapshot.child("tanggal_upload").getValue(String::class.java) ?: "-"

                    if (selectedData.isNotEmpty() && selectedData.none { it.nama_nasabah == namaNasabah }) {
                        continue
                    }

                    for (key in sectionKeys) {
                        val section = dataSnapshot.child(key)
                        if (section.exists()) {
                            val dataMap = mutableMapOf<String, String>()
                            dataMap["Nama Nasabah"] = namaNasabah
                            dataMap["Tanggal Upload"] = tanggalUpload
                            dataMap["Bagian"] = key

                            dataMap["Status"] = section.child("status").getValue(String::class.java) ?: "-"
                            dataMap["Tanggal"] = section.child("tanggal").getValue(String::class.java) ?: "-"
                            dataMap["Keterangan"] = section.child("keterangan").getValue(String::class.java) ?: "-"
                            dataMap["Upload Data"] = section.child("uploadData").getValue(String::class.java) ?: "-"

                            exportList.add(dataMap)
                        }
                    }
                }

                exportToExcelFromMap(exportList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseError", "❌ Gagal mengambil data: ${error.message}")
            }
        })
    }

    private fun searchData(keyword: String) {
        val query = if (keyword.isBlank()) {
            // Kembalikan semua data
            database.orderByChild("timestamp")
        } else {
            // Filter berdasarkan nama_nasabah
            database.orderByChild("nama_nasabah")
                .startAt(keyword)
                .endAt(keyword + "\uf8ff")
        }

        val options = FirebaseRecyclerOptions.Builder<ClassDataAdd>()
            .setQuery(query, ClassDataAdd::class.java)
            .build()

        adapter.updateOptions(options)  // Ganti query di adapter
        adapter.startListening()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        if (::adapter.isInitialized) {
            adapter.stopListening()
        }
        _binding = null
    }



}
