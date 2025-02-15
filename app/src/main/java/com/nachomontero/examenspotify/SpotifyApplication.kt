package com.nachomontero.examenspotify

import android.app.Application
import android.util.Log

class SpotifyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar RetrofitInstance o cualquier configuración global si es necesario
        Log.d("SpotifyApplication", "Aplicación iniciada")
    }

    companion object{
        private lateinit var  instance: SpotifyApplication
    }
}