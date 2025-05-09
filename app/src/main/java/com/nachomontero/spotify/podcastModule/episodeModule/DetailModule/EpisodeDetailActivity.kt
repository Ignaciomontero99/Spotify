package com.nachomontero.spotify.podcastModule.episodeModule.DetailModule

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.nachomontero.spotify.R
import com.nachomontero.spotify.api.Episodio
import com.nachomontero.spotify.api.common.Constants
import com.nachomontero.spotify.api.service.EpisodioService
import com.nachomontero.spotify.databinding.ActivityEpisodeDetailBinding
import com.nachomontero.spotify.songModule.DetailModule.SongDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class EpisodeDetailActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityEpisodeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mBinding = ActivityEpisodeDetailBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val episodeId = intent.getIntExtra("episodeId", -1)
        val podcastId = intent.getIntExtra("podcastId", -1)

        val titulo = intent.getStringExtra("titulo")
        val descripcion = intent.getStringExtra("descripcion")

        if (episodeId != -1) {
            getEpisodeById(episodeId, podcastId)
        } else {
            Toast.makeText(this, "No se encontró el episodio", Toast.LENGTH_SHORT).show()
            finish()
        }

        mBinding.episodeDetailTitle.text = titulo
        mBinding.episodeDetailDescript.text = descripcion

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun getEpisodeById(episodeId: Int, podcastId: Int) {
        val retrofit = provideRetrofit()
        val service = retrofit.create(EpisodioService::class.java)

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val episodio = service.getEpisodioInfo(episodeId, podcastId)
                withContext(Dispatchers.Main) {

                    mBinding.episodeDetailTitle.text = episodio.titulo
                    mBinding.episodeDetailDescript.text = episodio.descripcion
                }
            } catch (e: Exception) {
                Log.e("EpisodeDetail", "Error cargando Episodio: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@EpisodeDetailActivity, "Error al cargar el episodio", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun provideRetrofit(): Retrofit {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit
    }
}