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
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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

class DataNamaNasabahActivity : AppCompatActivity() {

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
        val database = FirebaseDatabase.getInstance().reference.child("name_nasabah")

        fun getCellString(row: Row, index: Int): String {
            val cell = row.getCell(index)
            return when (cell?.cellType) {
                CellType.STRING -> cell.stringCellValue.trim()
                CellType.NUMERIC -> cell.numericCellValue.toLong().toString()
                else -> ""
            }
        }

        val dataRow = sheet.getRow(2) // Baris ke-3 (index 2)
        if (dataRow != null) {
            val lastCellIndex = dataRow.lastCellNum.toInt()
            var i = 0
            while (i + 2 < lastCellIndex) {
                val idStr = getCellString(dataRow, i)
                val name = getCellString(dataRow, i + 1)
                val status = getCellString(dataRow, i + 2)

                if (idStr.isEmpty() || name.isEmpty()) {
                    Log.w("ExcelUpload", "Kolom $i dilewati: id atau name kosong")
                    i += 3
                    continue
                }

                val idLong = idStr.toLongOrNull()
                if (idLong == null) {
                    Log.w("ExcelUpload", "Kolom $i dilewati: id bukan angka - '$idStr'")
                    i += 3
                    continue
                }

                val newData = mapOf(
                    "id" to idLong,
                    "name" to name,
                    "status" to status
                )

                database.orderByChild("id").equalTo(idLong.toDouble()).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var found = false
                        for (child in snapshot.children) {
                            val updates = mutableMapOf<String, Any>()

                            val currentName = child.child("name").value?.toString()
                            val currentStatus = child.child("status").value?.toString()

                            if (currentName != name) updates["name"] = name
                            if (currentStatus != status) updates["status"] = status

                            if (updates.isNotEmpty()) {
                                database.child(child.key!!).updateChildren(updates)
                            }

                            found = true
                            break
                        }

                        if (!found) {
                            database.push().setValue(newData)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ExcelUpload", "Firebase error: ${error.message}")
                    }
                })

                i += 3
            }
        }

        Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
    }




    private fun loadTreeData() {
        val database = FirebaseDatabase.getInstance().getReference("name_nasabah")
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