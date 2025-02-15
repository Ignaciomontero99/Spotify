package com.nachomontero.examenspotify.profileModule

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.nachomontero.examenspotify.R
import com.nachomontero.examenspotify.databinding.ActivityProfileBinding
import com.nachomontero.examenspotify.retrofit.Common.Constants
import com.nachomontero.examenspotify.retrofit.data.Usuario
import kotlinx.coroutines.launch

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = 1
        lifecycleScope.launch {
            val user = Constants.api.getUser(userId)
            binding.username.text = user.username
            binding.email.text = user.email
            binding.country.text = user.pais
        }

        binding.btnEditProfile.setOnClickListener {
            val updatedUser = Usuario(username = "nuevoUsuario", email = "nuevo@mail.com", pais = "España")
            lifecycleScope.launch { Constants.api.updateUser(userId, updatedUser) }
        }
    }
}
