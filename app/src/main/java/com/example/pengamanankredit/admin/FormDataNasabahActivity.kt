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
import com.google.firebase.database.FirebaseDatabase
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.text.SimpleDateFormat
import java.util.Locale

class FormDataNasabahActivity : AppCompatActivity() {

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
        setContentView(R.layout.activity_form_data_nasabah)


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

        backBtn.setOnClickListener{
            finish()
        }

        loadTreeData()
    }

    private fun uploadExcelToFirebase(uri: Uri, context: Context) {
        val inputStream = contentResolver.openInputStream(uri)
        val workbook = XSSFWorkbook(inputStream)
        val sheet = workbook.getSheetAt(0)

        val database = FirebaseDatabase.getInstance().reference.child("data")

        fun getCellString(row: org.apache.poi.ss.usermodel.Row, index: Int): String {
            val cell = row.getCell(index)
            return when (cell?.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> {
                    if (DateUtil.isCellDateFormatted(cell)) {
                        val date = cell.dateCellValue
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(date)
                    } else {
                        cell.numericCellValue.toLong().toString()
                    }
                }

                CellType.BOOLEAN -> cell.booleanCellValue.toString()
                else -> ""
            }
        }

        Log.d("ExcelUpload", "lastRowNum = ${sheet.lastRowNum}")
        Log.d("ExcelUpload", "physicalNumberOfRows = ${sheet.physicalNumberOfRows}")

        for (rowIndex in 1 until sheet.physicalNumberOfRows) { // mulai dari 1, skip header
            val row = sheet.getRow(rowIndex) ?: continue

            val firstCellValue = row.getCell(0)?.stringCellValue ?: ""
            if (firstCellValue.lowercase() == "alamat") continue // skip header ganda kalau ada

            val alamat = getCellString(row, 0)
            val hariTunggakan = getCellString(row, 1)
            val id = getCellString(row, 2)
            val jaminan = getCellString(row, 3)
            val nama = getCellString(row, 4)
            val noHp = getCellString(row, 5)
            val noRekening = getCellString(row, 6)
            val plafon = getCellString(row, 7)
            val tglJatuhTempo = getCellString(row, 8)
            val tglRealisasi = getCellString(row, 9)
            val totalKewajiban = getCellString(row, 10)
            val tunggakanBunga = getCellString(row, 11)
            val tunggakanDenda = getCellString(row, 12)
            val tunggakanPokok = getCellString(row, 13)

            // Skip row jika id kosong agar tidak tersimpan/update di DB
            if (id.isEmpty()) {
                Log.w("ExcelUpload", "Skip row $rowIndex karena id kosong")
                continue
            }

            val dataMap = mapOf(
                "alamat" to alamat,
                "hari_tunggakan" to hariTunggakan,
                "id" to id,
                "jaminan" to jaminan,
                "nama" to nama,
                "no_hp" to noHp,
                "no_rekening" to noRekening,
                "plafon" to plafon,
                "tgl_jatuh_tempo" to tglJatuhTempo,
                "tgl_realisasi" to tglRealisasi,
                "total_kewajiban" to totalKewajiban,
                "tunggakan_bunga" to tunggakanBunga,
                "tunggakan_denda" to tunggakanDenda,
                "tunggakan_pokok" to tunggakanPokok
            )

            val nodeKey = id  // Pakai id sebagai key utama
            val nodeRef = database.child(nodeKey)

            nodeRef.get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val updates = mutableMapOf<String, Any>()
                    for ((key, newValue) in dataMap) {
                        val oldValue = snapshot.child(key).value?.toString()
                        if (oldValue != newValue) {
                            updates[key] = newValue
                        }
                    }
                    if (updates.isNotEmpty()) {
                        nodeRef.updateChildren(updates)
                    }
                } else {
                    nodeRef.setValue(dataMap)
                }
            }
        }
        loadTreeData()

        Toast.makeText(this, "Data berhasil diunggah", Toast.LENGTH_SHORT).show()
    }

    private fun loadTreeData() {
        val database = FirebaseDatabase.getInstance().getReference("data")
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
