package com.nachomontero.spotify.songModule.DetailModule

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.PlaylistService
import com.nachomontero.spotify.databinding.ActivitySongDetailBinding
import com.nachomontero.spotify.sharedPreferences.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SongDetailActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySongDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mBinding = ActivitySongDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val titulo = intent.getStringExtra("titulo")
        val duracion = intent.getStringExtra("duracion")
        val cancionId = intent.getIntExtra("id", -1)

        mBinding.songTitle.text = titulo
        mBinding.songDuration.text = "Duración: $duracion"

        mBinding.btnAddToPlaylist.setOnClickListener {
            // En el siguiente paso agregaremos esta parte
            Toast.makeText(this, "Mostrar opciones de playlist para ID $cancionId", Toast.LENGTH_SHORT).show()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}