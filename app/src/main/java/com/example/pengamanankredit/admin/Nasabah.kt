package com.example.pengamanankredit.admin

data class Nasabah(
    val id: String = "",
    val nama: String = "",
    val alamat: String = "",
    val no_hp: String = "",
    val no_rekening: String = "",
    val plafon: String = "",
    val tgl_realisasi: String = "",
    val tgl_jatuh_tempo: String = "",
    val total_kewajiban: String = "",
    val tunggakan_pokok: String = "",
    val tunggakan_bunga: String = "",
    val tunggakan_denda: String = "",
    val hari_tunggakan: Int = 0,
    val jaminan: String = ""
)
