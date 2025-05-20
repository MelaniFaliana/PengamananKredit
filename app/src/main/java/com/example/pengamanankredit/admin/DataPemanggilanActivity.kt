package com.example.pengamanankredit.admin

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.pengamanankredit.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Locale

class DataPemanggilanActivity : AppCompatActivity() {

    private lateinit var selectFileBtn: Button
    private lateinit var uploadBtn: Button
    private lateinit var backBtn: ImageButton
    private lateinit var fileNameTv: TextView
    private var fileUri: Uri? = null
    private lateinit var tvTreeData: TextView
    private lateinit var progressTree: ProgressBar
    private lateinit var containerDynamicData: LinearLayout
    private lateinit var swipeRefreshLayout: androidx.swiperefreshlayout.widget.SwipeRefreshLayout


    private val launcher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            fileUri = it
            fileNameTv.text = uri.lastPathSegment
            uploadBtn.isEnabled = true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_pemanggilan)


        selectFileBtn = findViewById(R.id.btn_select_file)
        uploadBtn = findViewById(R.id.btn_upload)
        backBtn = findViewById(R.id.btn_back)
        fileNameTv = findViewById(R.id.tv_file_name)
        tvTreeData = findViewById(R.id.tv_tree_data)
        progressTree = findViewById(R.id.progress_tree)
        containerDynamicData = findViewById(R.id.container_dynamic_data)

        swipeRefreshLayout = findViewById(R.id.swipe_refresh)

        swipeRefreshLayout.setOnRefreshListener {
            // Panggil fungsi reload data yang sudah kamu punya
            loadTreeData()
        }


        selectFileBtn.setOnClickListener {
            launcher.launch("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        }

        uploadBtn.setOnClickListener {
            fileUri?.let { uri ->
                uploadExcelToFirebase(uri, this)
            }
        }

        backBtn.setOnClickListener {
            finish()
        }

        loadTreeData()
    }

    private fun uploadExcelToFirebase(uri: Uri, context: Context) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        val database = FirebaseDatabase.getInstance().reference.child("add_data")

        fun getCellString(row: Row, index: Int): String {
            val cell = row.getCell(index)
            return when (cell?.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val date = cell.dateCellValue
                        SimpleDateFormat("dd MMMM yyyy", Locale("id")).format(date)
                    } else {
                        cell.numericCellValue.toLong().toString()
                    }
                }
                CellType.BOOLEAN -> cell.booleanCellValue.toString()
                else -> ""
            }
        }

        // Ambil header dari baris ke-2 dan ke-3 (index 1 dan 2)
        val headerRow1 = sheet.getRow(1)
        val headerRow2 = sheet.getRow(2)
        val headers = mutableListOf<Pair<String, String>>()  // Pair<parent, child>
        var currentSection = ""

        for (i in 0 until headerRow1.lastCellNum) {
            val rawH1 = headerRow1.getCell(i)?.stringCellValue?.trim() ?: ""
            val rawH2 = headerRow2.getCell(i)?.stringCellValue?.trim() ?: ""

            val h1 = if (rawH1.equals("uploadData", ignoreCase = true)) "uploadData" else rawH1.lowercase()
            val h2 = if (rawH2.equals("uploadData", ignoreCase = true)) "uploadData" else rawH2.lowercase()


            if (h1.isNotEmpty() && h1 != "nama" && h1 != "tanggal") {
                currentSection = h1
            }

            if (h1 == "nama" && h2 == "nasabah") {
                headers.add(Pair("nama_nasabah", ""))
            } else if (h1 == "tanggal" && h2 == "upload") {
                headers.add(Pair("tanggal_upload", ""))
            } else if (h2.isNotEmpty()) {
                val parent = if (h1.isNotEmpty()) h1 else currentSection
                headers.add(Pair(parent, h2))
            } else if (h1.isNotEmpty()) {
                headers.add(Pair(h1, ""))
            } else {
                headers.add(Pair("", ""))
            }
        }



        for (rowIndex in 3..sheet.lastRowNum) {
            val row = sheet.getRow(rowIndex) ?: continue
            val flatMap = mutableMapOf<String, String>()
            var namaNasabah = ""
            var tanggalUpload = ""

            for ((cellIndex, header) in headers.withIndex()) {
                val (parent, child) = header
                if (parent.isEmpty() && child.isEmpty()) continue

                val value = getCellString(row, cellIndex)
                if (value.isEmpty()) continue

                if (parent == "nama_nasabah") {
                    namaNasabah = value
                } else if (parent == "tanggal_upload") {
                    tanggalUpload = value
                } else {
                    val mapKey = if (child.isNotEmpty()) "${parent}_${child}" else parent
                    flatMap[mapKey] = value
                }
            }


            if (namaNasabah.isEmpty() || tanggalUpload.isEmpty()) {
                Log.w("ExcelUpload", "Skip row $rowIndex karena nama_nasabah atau tanggal_upload kosong")
                continue
            }

            val structuredFields = listOf(
                "eksekusi", "penyemprotan", "peringatan", "realisasi",
                "surat_panggilan", "surat_peringatan1", "surat_peringatan2", "surat_peringatan3"
            )

            val nestedMap = mutableMapOf<String, Any>()

            for ((key, value) in flatMap) {
                if (key == "nama_nasabah" || key == "tanggal_upload") {
                    nestedMap[key] = value
                    continue
                }

                val matchingParent = structuredFields.firstOrNull { key.startsWith("${it}_") }
                if (matchingParent != null) {
                    val child = key.removePrefix("${matchingParent}_")
                    val parentMap = nestedMap.getOrPut(matchingParent) { mutableMapOf<String, String>() } as MutableMap<String, String>
                    parentMap[child] = value
                    continue
                }


                // Fallback jika bukan format parent_child
                nestedMap[key] = value
            }



            Log.d("ExcelUpload", "NestedMap row $rowIndex: $nestedMap")  // Debug, pastikan nestedMap benar

            database.orderByChild("nama_nasabah").equalTo(namaNasabah)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var found = false
                        for (child in snapshot.children) {
                            val existingTanggalUpload = child.child("tanggal_upload").value?.toString()
                            val existingNamaNasabah = child.child("nama_nasabah").value?.toString()

                            if (existingNamaNasabah == namaNasabah || existingTanggalUpload == tanggalUpload) {
                                val updates = mutableMapOf<String, Any>()

                                // Tambahkan logika update nama_nasabah dan tanggal_upload jika berubah
                                if (existingNamaNasabah != namaNasabah) {
                                    updates["nama_nasabah"] = namaNasabah
                                }
                                if (existingTanggalUpload != tanggalUpload) {
                                    updates["tanggal_upload"] = tanggalUpload
                                }

                                for ((key, value) in nestedMap) {
                                    if (key == "nama_nasabah" || key == "tanggal_upload") continue  // sudah ditangani
                                    if (child.child(key).exists() && child.child(key).hasChildren()) {
                                        val valueMap = value as? Map<*, *> ?: continue
                                        for ((subKey, subVal) in valueMap) {
                                            val oldVal = child.child("$key/$subKey").value?.toString()
                                            if (oldVal != subVal.toString()) {
                                                updates["$key/$subKey"] = subVal.toString()
                                            }
                                        }
                                    } else {
                                        val oldVal = child.child(key).value?.toString()
                                        if (oldVal != value.toString()) {
                                            updates[key] = value.toString()
                                        }
                                    }
                                }

                                if (updates.isNotEmpty()) {
                                    database.child(child.key!!).updateChildren(updates)
                                }

                                found = true
                                break
                            }
                        }
                        if (!found) {
                            database.push().setValue(nestedMap)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ExcelUpload", "Firebase error: ${error.message}")
                    }
                })
        }

        loadTreeData()
        Toast.makeText(context, "Data berhasil diunggah", Toast.LENGTH_SHORT).show()
    }




    private fun loadTreeData() {
        val database = FirebaseDatabase.getInstance().getReference("add_data")
        progressTree.visibility = View.VISIBLE
        containerDynamicData.removeAllViews()

        database.get().addOnSuccessListener { snapshot ->
            progressTree.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false

            for (entry in snapshot.children) {
                val idKey = entry.key ?: continue
                val dataMap = entry.children.associate { it.key!! to it.value.toString() }

                // Detail layout (hidden awalnya)
                val detailLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    visibility = View.GONE
                    setPadding(20, 0, 0, 20)
                }

                // Header (judul id)
                val header = TextView(this).apply {
                    text = "▶ $idKey"  // ▶ = collapsed, ▼ = expanded
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                    setPadding(0, 20, 0, 12)
                    setOnClickListener {
                        val expanded = detailLayout.visibility == View.VISIBLE
                        detailLayout.visibility = if (expanded) View.GONE else View.VISIBLE
                        text = if (expanded) "▶ $idKey" else "▼ $idKey"
                    }
                }

                // Tambah detail data ke dalam detailLayout
                for ((key, value) in dataMap) {
                    val detailText = TextView(this).apply {
                        text = "$key: $value"
                        textSize = 14f
                        setPadding(0, 4, 0, 4)
                    }
                    detailLayout.addView(detailText)
                }

                // Tambahkan ke container utama
                containerDynamicData.addView(header)
                containerDynamicData.addView(detailLayout)
            }
        }.addOnFailureListener {
            progressTree.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = false
            val errorText = TextView(this).apply {
                text = "Gagal memuat data: ${it.message}"
                setTextColor(Color.RED)
            }
            containerDynamicData.addView(errorText)
        }
    }


}