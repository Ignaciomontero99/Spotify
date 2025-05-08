package com.nachomontero.spotify.sharedPreferences

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.nachomontero.spotify.api.Cancion
import com.nachomontero.spotify.api.Playlist

object SessionManager {
    private const val PREFS_NAME = "user_session"
    private const val USER_ID_KEY = "user_id"
    private const val USER_TYPE_KEY = "user_type"

    private const val PREF_NAME = "playlists"
    private const val KEY_PLAYLISTS = "playlists"

    fun saveSession(context: Context, userId: String, userType: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(USER_ID_KEY, userId)
            putString(USER_TYPE_KEY, userType)
            apply()
        }
    }

    fun getUser(context: Context): String? {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(USER_ID_KEY, null)
    }

    fun getUserId(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val idString = prefs.getString(USER_ID_KEY, null)
        return idString?.toIntOrNull() ?: -1
    }

    fun getUserType(context: Context): String? = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(USER_TYPE_KEY, null)

    fun clearSession(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit() {
            clear()
        }
    }

    private fun guardarCancionesLocalesEnPreferences(context: Context, canciones: List<Cancion>) {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val gson = Gson()
        val json = gson.toJson(canciones)

        editor.putString("cancionesLocales", json)
        editor.apply()
    }

    fun obtenerCancionesLocalesDesdePreferences(context: Context): List<Cancion> {
        val sharedPreferences = context.getSharedPreferences("app_preferences", Context.MODE_PRIVATE)
        val json = sharedPreferences.getString("cancionesLocales", "[]")

        val gson = Gson()
        val type = object : TypeToken<List<Cancion>>() {}.type
        return gson.fromJson(json, type)
    }

    fun agregarCancionLocal(context: Context, cancion: Cancion) {
        val cancionesLocales = obtenerCancionesLocalesDesdePreferences(context).toMutableList()
        cancionesLocales.add(cancion)

        guardarCancionesLocalesEnPreferences(context, cancionesLocales)
    }

    fun eliminarCancionLocal(context: Context, cancion: Cancion) {
        val sharedPreferences = context.getSharedPreferences("songs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Convertir la canción a JSON y eliminarla del conjunto de canciones
        val cancionesJson = sharedPreferences.getString("cancionesLocales", "[]")
        val cancionesList = Gson().fromJson(cancionesJson, Array<Cancion>::class.java).toMutableList()

        // Eliminar la canción de la lista
        cancionesList.removeAll { it.id == cancion.id }

        // Guardar la lista actualizada en SharedPreferences
        val updatedCancionesJson = Gson().toJson(cancionesList)
        editor.putString("cancionesLocales", updatedCancionesJson)
        editor.apply()
    }

    fun agregarPlaylistLocal(context: Context, playlist: Playlist) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Obtener las playlists existentes
        val playlistsJson = sharedPreferences.getString(KEY_PLAYLISTS, "[]")
        val playlistsList = Gson().fromJson(playlistsJson, Array<Playlist>::class.java).toMutableList()

        // Añadir la nueva playlist
        playlistsList.add(playlist)

        // Guardar la lista actualizada
        val updatedPlaylistsJson = Gson().toJson(playlistsList)
        editor.putString(KEY_PLAYLISTS, updatedPlaylistsJson)
        editor.apply()
    }

    fun obtenerPlaylistsDesdePreferences(context: Context): List<Playlist> {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val playlistsJson = sharedPreferences.getString(KEY_PLAYLISTS, "[]")
        return Gson().fromJson(playlistsJson, Array<Playlist>::class.java).toList()
    }

    fun eliminarPlaylistLocal(context: Context, playlist: Playlist) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        // Obtener las playlists existentes
        val playlistsJson = sharedPreferences.getString(KEY_PLAYLISTS, "[]")
        val playlistsList = Gson().fromJson(playlistsJson, Array<Playlist>::class.java).toMutableList()

        // Eliminar la playlist
        playlistsList.removeAll { it.id == playlist.id }

        // Guardar la lista actualizada
        val updatedPlaylistsJson = Gson().toJson(playlistsList)
        editor.putString(KEY_PLAYLISTS, updatedPlaylistsJson)
        editor.apply()
    }

}

