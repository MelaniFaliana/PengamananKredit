package com.example.pengamanankredit.bottomnav.ui.profil


import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.pengamanankredit.R
import com.example.pengamanankredit.authentication.AuthenticationActivity
import com.example.pengamanankredit.databinding.FragmentProfilBinding
import com.google.firebase.auth.FirebaseAuth

class ProfilFragment : Fragment() {

    private var _binding: FragmentProfilBinding? = null
    private val binding get() = _binding!!
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inisialisasi binding dan ViewModel (jika diperlukan)
        _binding = FragmentProfilBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Setup tombol logout, edit profile, dan change password
        binding.logout.setOnClickListener {
            context?.let { ctx -> // Pastikan context tidak null sebelum membuat dialog
                AlertDialog.Builder(ctx).apply {
                    setTitle("Konfirmasi Logout")
                    setMessage("Apakah Anda yakin ingin keluar?")
                    setPositiveButton("Ya") { _, _ ->
                        firebaseAuth.signOut()
                        val intent = Intent(requireContext(), AuthenticationActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                    setNegativeButton("Batal") { dialog, _ ->
                        dialog.dismiss()
                    }
                    create().show()
                }
            }
        }

        binding.btnEditProfile.setOnClickListener {
            Log.d("ProfileFragment", "btnEditProfile clicked")
            val intent = Intent(requireContext(), EditProfilActivity::class.java)
            startActivity(intent)
        }

        binding.changePassword.setOnClickListener {
            val emailUser = firebaseAuth.currentUser?.email

            if (emailUser.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Email tidak ditemukan. Silakan login ulang.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Tampilkan AlertDialog untuk konfirmasi
            AlertDialog.Builder(requireContext()).apply {
                setTitle("Konfirmasi Ubah Kata Sandi")
                setMessage("Apakah Anda yakin ingin mengubah kata sandi? Email reset akan dikirim ke $emailUser.")
                setPositiveButton("Ya") { _, _ ->
                    if (emailUser.isNullOrEmpty()) {
                        Toast.makeText(requireContext(), "Email tidak ditemukan!", Toast.LENGTH_LONG).show()
                        return@setPositiveButton
                    }
                    // Kirim email reset password
                    firebaseAuth.sendPasswordResetEmail(emailUser)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Email reset password telah dikirim ke $emailUser", Toast.LENGTH_LONG).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(requireContext(), "Gagal mengirim email: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                }
                setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                create().show()
            }
        }

        // Tampilkan nama user di TextView
        val currentUser = firebaseAuth.currentUser
        val userName = currentUser?.displayName ?: "Pengguna"
        binding.tvUserName.text = userName

        // Muat gambar profil sesuai dengan akun yang sedang login
        loadProfileImage()

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.title = "Profil"
    }

    // Fungsi loadProfileImage() mengambil URI gambar dari SharedPreferences dan menampilkannya
    private fun loadProfileImage() {
        val currentEmail = firebaseAuth.currentUser?.email ?: "default"
        val key = "profileImageUri_$currentEmail"
        val prefs = requireContext().getSharedPreferences("profilePrefs", Context.MODE_PRIVATE)
        val uriString = prefs.getString(key, null)
        if (uriString != null) {
            val imageUri = Uri.parse(uriString)
            binding.imgProfile.setImageURI(imageUri)
        } else {
            // Jika belum ada gambar tersimpan, tampilkan gambar default (person_icon)
            binding.imgProfile.setImageResource(R.drawable.person_black)
        }
    }

    // Setiap kali fragment kembali aktif, gambar profil akan diperbarui
    override fun onResume() {
        super.onResume()
        loadProfileImage()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
