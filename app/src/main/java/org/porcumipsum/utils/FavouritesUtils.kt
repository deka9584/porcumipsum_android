package org.porcumipsum.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray
import org.json.JSONException

object FavouritesUtils {
    private var preferences: SharedPreferences? = null
    private var favourites: JSONArray? = null

    fun addFavourite(context: Context, favourite: String) {
        favourites?.put(favourite)
        saveFavourites(context)
    }

    fun getFavourites(): JSONArray? {
        return favourites
    }

    fun isFavorite(text: String?): Boolean {
        val len = favourites?.length() ?: 0

        for (i in 0 until len) {
            if (favourites?.optString(i).equals(text, true)) {
                return true
            }
        }
        return false
    }

    fun loadPreferences(context: Context) {
        preferences = context.getSharedPreferences("pork_prefs", Context.MODE_PRIVATE)
        loadFavourites()
    }

    fun removeFavourite(context: Context, index: Int) {
        favourites?.remove(index)
        saveFavourites(context)
    }

    private fun loadFavourites() {
        val rawList = preferences?.getString("favourites", "[]")

        try {
            favourites = JSONArray(rawList)
        } catch (ex: JSONException) {
            ex.printStackTrace()
            Log.e("rawList", "$rawList")
        }
    }

    private fun saveFavourites(context: Context) {
        val editor = preferences?.edit()
        editor?.putString("favourites", favourites.toString())
        editor?.apply()
    }
}