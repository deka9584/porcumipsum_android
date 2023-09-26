package org.porcumipsum.models

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.porcumipsum.utils.FavouritesUtils

class ListViewModel: ViewModel() {

    private val _favourites = MutableLiveData<MutableList<String>>()
    val favourites: LiveData<MutableList<String>> get() = _favourites

    fun loadFavourites() {
        FavouritesUtils.getFavourites()?.let { jsonArray ->
            _favourites.value = (0 until jsonArray.length()).map { i ->
                jsonArray.optString(i)
            }.toMutableList()
        }
    }

    fun removeElement(context: Context, index: Int) {
        val currentList = _favourites.value

        if (currentList != null && index >= 0 && index < currentList.size) {
            FavouritesUtils.removeFavourite(context, index)
            currentList.removeAt(index)
            _favourites.postValue(currentList)
        }
    }
}