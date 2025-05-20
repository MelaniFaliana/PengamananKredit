package com.example.pengamanankredit.tambahdata

data class ClassDataAdd(
    val nama_nasabah: String = "-",
    val tanggal_upload: String = "-",
    val tanggal_update: String? = "-",
    val peringatan: ClassSubData = ClassSubData(),
    val surat_peringatan1: ClassSubData = ClassSubData(),
    val surat_peringatan2: ClassSubData = ClassSubData(),
    val surat_peringatan3: ClassSubData = ClassSubData(),
    val surat_panggilan: ClassSubData = ClassSubData(),
    val penyemprotan: ClassSubData = ClassSubData(),
    val eksekusi: ClassSubData = ClassSubData(),
    var id: String? = null
)
data class ClassSubData(
    val status: String = "-",
    val tanggal: String = "-",
    val uploadData: String = "-",
    val keterangan: String = "-"
)
