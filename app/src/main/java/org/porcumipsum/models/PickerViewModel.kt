package org.porcumipsum.models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.porcumipsum.clients.PorkApiClient

class PickerViewModel: ViewModel() {

    private val _preImp = MutableLiveData<List<String>>()
    val preImp: LiveData<List<String>> get() = _preImp

    private val _saints = MutableLiveData<List<String>>()
    val saints: LiveData<List<String>> get() = _saints

    private val _postImp = MutableLiveData<List<String>>()
    val postImp: LiveData<List<String>> get() = _postImp

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun load(context: Context) {
        if (_saints.value == null || _saints.value?.size == 0) {
            _loading.value = true

            PorkApiClient.fetchPorks(context,
                onSuccess = { json ->
                    json.optJSONArray("preImprecazioni")?.let { arr ->
                        _preImp.value = (0 until arr.length()).map { i ->
                            arr.optString(i)
                        }
                    }

                    json.optJSONArray("santi")?.let { arr ->
                        _saints.value = (0 until arr.length()).map { i ->
                            arr.optString(i)
                        }
                    }

                    json.optJSONArray("postImprecazioni")?.let { arr ->
                        _postImp.value = (0 until arr.length()).map { i ->
                            arr.optString(i)
                        }
                    }

                    _loading.value = false
                },
                onError = { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    _loading.value = false
                }
            )
        }
    }
}