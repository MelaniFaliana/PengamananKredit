package com.example.pengamanankredit.bottomnav.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pengamanankredit.R
import com.example.pengamanankredit.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private lateinit var database: DatabaseReference
    private lateinit var scrollView: ScrollView
    private val list = mutableListOf<NasabahData>()
    private lateinit var adapter: NasabahAdapter


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NasabahAdapter(list) { selected ->
            showDataResult(
                selected.id, selected.noHp, selected.noRekening, selected.nama, selected.alamat,
                selected.tglRealisasi, selected.tglJatuhTempo, selected.plafon, selected.jaminan,
                selected.tunggakanPokok, selected.tunggakanBunga, selected.tunggakanDenda,
                selected.totalKewajiban, selected.hariTunggakan
            )
            searchDataSelengkapnya(selected.nama)
        }

        binding.recyclerViewSearchResult.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewSearchResult.adapter = adapter


        // Data bagian peringatan
        val contentPeringatanDetail = binding.contentPeringatanDetail
        val iconToggle = binding.iconTogglePeringatanDetail

        // Data bagian surat peringatan1
        val contentSuratPeringatan1Detail = binding.contentSuratPeringatan1Detail
        val iconToggleSuratPeringatan1Detail = binding.iconToggleSuratPeringatan1Detail

        // Data bagian surat peringatan2
        val contentSuratPeringatan2Detail = binding.contentSuratPeringatan2Detail
        val iconToggleSuratPeringatan2Detail = binding.iconToggleSuratPeringatan2Detail

        // Data bagian surat peringatan3
        val contentSuratPeringatan3Detail = binding.contentSuratPeringatan3Detail
        val iconToggleSuratPeringatan3Detail = binding.iconToggleSuratPeringatan3Detail

        // Data bagian surat panggilan
        val contentSuratPanggilanDetail = binding.contentSuratPanggilanDetail
        val iconToggleSuratPanggilanDetail = binding.iconToggleSuratPanggilanDetail

        // Data bagian penyemprotan/sticker
        val contentPenyemprotanDetail = binding.contentPenyemprotanDetail
        val iconTogglePenyemprotanDetail = binding.iconTogglePenyemprotanDetail

        // Data bagian eksekusi
        val contentEksekusiDetail = binding.contentEksekusiDetail
        val iconToggleEksekusiDetail = binding.iconToggleEksekusiDetail

        // Data bagian realisasi
        val contentRealisasiDetail = binding.contentRealisasiDetail
        val iconToggleRealisasiDetail = binding.iconToggleRealisasiDetail

        scrollView = view.findViewById(R.id.scrollView)
        database = FirebaseDatabase.getInstance().getReference("data")

        // >>> Tambahkan ini untuk mengubah nama Toolbar
        (activity as AppCompatActivity).supportActionBar?.title = "Beranda"
        (activity as AppCompatActivity).supportActionBar?.setBackgroundDrawable(
            resources.getDrawable(R.color.blue) // Ganti dengan warna yang diinginkan
        )

        binding.searchViewHome.setIconifiedByDefault(false)
        binding.searchViewHome.isIconified = false
        binding.searchViewHome.isFocusable = false
        binding.searchViewHome.isFocusableInTouchMode = false
        binding.searchViewHome.clearFocus()
        binding.searchViewHome.queryHint = "Cari data nasabah..."


        handleBackPress()

        binding.searchViewHome.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchData(it)
                    searchDataSelengkapnya(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })


        binding.iconSelengkapnya.setOnClickListener {
            if (binding.layoutDataSelengkapnya.visibility == View.GONE) {
                binding.layoutDataSelengkapnya.visibility = View.VISIBLE
                binding.iconSelengkapnya.setImageResource(R.drawable.ic_expand_less)
            } else {
                binding.layoutDataSelengkapnya.visibility = View.GONE
                binding.iconSelengkapnya.setImageResource(R.drawable.ic_expand_more)
            }
        }

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

    private fun handleBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d("HomeFragment", "Tombol Back ditekan - Memanggil showExitDialog()")
                    showExitDialog()
                }
            }
        )
    }

    private fun searchData(keyword: String) {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                    // Sembunyikan tampilan sebelumnya
                    binding.resultLayout.visibility = View.GONE
                    binding.layoutSelengkapnya.visibility = View.GONE
                    binding.FormDebitur.visibility = View.GONE
                    binding.layoutDataSelengkapnya.visibility = View.GONE
                    binding.iconSelengkapnya.setImageResource(R.drawable.ic_expand_more)

                    list.clear()
                    adapter.notifyDataSetChanged()


                    for (dataSnapshot in snapshot.children) {
                    val nama = dataSnapshot.child("nama").getValue(String::class.java)
                    val noRek = dataSnapshot.child("no_rekening").getValue(String::class.java)

                    if (nama?.contains(keyword, ignoreCase = true) == true ||
                        noRek?.contains(keyword, ignoreCase = true) == true) {

                        val item = NasabahData(
                            id = dataSnapshot.child("id").getValue(String::class.java) ?: "-",
                            nama = nama ?: "-",
                            noRekening = noRek ?: "-",
                            noHp = dataSnapshot.child("no_hp").getValue(String::class.java) ?: "-",
                            alamat = dataSnapshot.child("alamat").getValue(String::class.java) ?: "-",
                            tglRealisasi = dataSnapshot.child("tgl_realisasi").getValue(String::class.java) ?: "-",
                            tglJatuhTempo = dataSnapshot.child("tgl_jatuh_tempo").getValue(String::class.java) ?: "-",
                            plafon = dataSnapshot.child("plafon").getValue(String::class.java) ?: "-",
                            jaminan = dataSnapshot.child("jaminan").getValue(String::class.java) ?: "-",
                            tunggakanPokok = dataSnapshot.child("tunggakan_pokok").getValue(String::class.java) ?: "-",
                            tunggakanBunga = dataSnapshot.child("tunggakan_bunga").getValue(String::class.java) ?: "-",
                            tunggakanDenda = dataSnapshot.child("tunggakan_denda").getValue(String::class.java) ?: "-",
                            totalKewajiban = dataSnapshot.child("total_kewajiban").getValue(String::class.java) ?: "-",
                            hariTunggakan = dataSnapshot.child("hari_tunggakan").getValue(String::class.java) ?: ""
                        )

                        list.add(item)
                    }
                }

                if (list.isNotEmpty()) {
                    binding.recyclerViewSearchResult.visibility = View.VISIBLE
                    binding.imageView.visibility = View.GONE
                    binding.resultLayout.visibility = View.GONE
                    adapter.notifyDataSetChanged() // <--- Ini penting
                } else {
                    binding.recyclerViewSearchResult.visibility = View.GONE
                    binding.imageView.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



    private fun showDataResult(
        id: String,
        noHp: String,
        noRekening: String,
        nama: String,
        alamat: String,
        tglRealisasi: String,
        tglJatuhTempo: String,
        plafon: String,
        jaminan: String,
        tunggakanPokok: String,
        tunggakanBunga: String,
        tunggakanDenda: String,
        totalKewajiban: String,
        hariTunggakan: String
    ) {
        // Sembunyikan RecyclerView saat data detail ditampilkan
        binding.recyclerViewSearchResult.visibility = View.GONE

        binding.resultLayout.visibility = View.VISIBLE
        binding.imageView.visibility = View.GONE

        binding.tvId.text = id
        binding.tvNoHp.text = noHp
        binding.tvNoRek.text = noRekening
        binding.tvNama.text = nama
        binding.tvAlamat.text = alamat
        binding.tvTglRealisasi.text = tglRealisasi
        binding.tvTglJatuhTempo.text = tglJatuhTempo
        binding.tvPlafon.text = plafon
        binding.tvJaminan.text = jaminan
        binding.tvTunggakanPokok.text = tunggakanPokok
        binding.tvTunggakanBunga.text = tunggakanBunga
        binding.tvTunggakanDenda.text = tunggakanDenda
        binding.tvTotalKewajiban.text = totalKewajiban
        binding.tvHariTunggakan.text = "$hariTunggakan hari"

        binding.layoutSelengkapnya.visibility = View.VISIBLE
        binding.FormDebitur.visibility = View.VISIBLE

    }

    private fun showExitDialog() {
        Log.d("HomeFragment", "Menampilkan AlertDialog")
        requireActivity().runOnUiThread {
            AlertDialog.Builder(requireContext())
                .setTitle("Konfirmasi")
                .setMessage("Yakin ingin keluar dari aplikasi?")
                .setPositiveButton("Yakin") { _, _ ->
                    requireActivity().finishAffinity()
                }
                .setNegativeButton("Tidak") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
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


    override fun onResume() {
        super.onResume()

        // Pastikan SearchView tidak terfokus saat halaman dibuka
        binding.searchViewHome.clearFocus()
        binding.searchViewHome.isFocusable = false
        binding.searchViewHome.isFocusableInTouchMode = false

        // Akses SearchView
        val searchView = binding.searchViewHome

        // Mengatur LayoutParams secara manual
        val layoutParams = searchView.layoutParams as ViewGroup.MarginLayoutParams

        // Mengatur margin top, width, height sesuai dengan nilai di XML
        layoutParams.width = resources.getDimensionPixelSize(R.dimen.search_view_width) // Set width to 350dp
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.topMargin = resources.getDimensionPixelSize(R.dimen.search_view_margin_top) // Set marginTop to 20dp

        // Set margin dan tata letak lainnya jika perlu
        searchView.layoutParams = layoutParams

        // Menjaga agar fokus tidak terpengaruh
        searchView.clearFocus()
        searchView.isIconified = false
        searchView.requestFocus()

        // Anda juga bisa mengatur atribut lainnya seperti background, elevation, dsb jika diperlukan
        searchView.setBackgroundColor(resources.getColor(android.R.color.white))
        searchView.elevation = 4f

        // Pastikan SearchView tetap dalam posisi yang diinginkan ketika halaman dibuka kembali
        searchView.requestLayout()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
