package org.porcumipsum.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.porcumipsum.clients.PorkApiClient

class GeneratorViewModel: ViewModel() {
    private val start = ArrayList<String>()
    private val preImp = ArrayList<String>()
    private val saints = ArrayList<String>()
    private val postImp = ArrayList<String>()

    private val _textOut = MutableLiveData<String>()
    val textOut: LiveData<String> get() = _textOut

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    fun generatePork(context: Context, nPar: Int, minLen: Int, commas: Boolean) {
        if (saints.isEmpty()) {
            _loading.value = true

            PorkApiClient.fetchPorks(context,
                onSuccess = { json ->
                    json.optJSONArray("start")?.let {
                        for (i in 0 until it.length()) {
                            start.add(it.optString(i))
                        }
                    }

                    json.optJSONArray("preImprecazioni")?.let {
                        for (i in 0 until it.length()) {
                            preImp.add(it.optString(i))
                        }
                    }

                    json.optJSONArray("santi")?.let {
                        for (i in 0 until it.length()) {
                            saints.add(it.optString(i))
                        }
                    }

                    json.optJSONArray("postImprecazioni")?.let {
                        for (i in 0 until it.length()) {
                            postImp.add(it.optString(i))
                        }
                    }

                    write(nPar, minLen, commas)
                    _loading.value = false
                },
                onError = { message ->
                    _textOut.value = message
                    _loading.value = false
                }
            )
        } else {
            write(nPar, minLen, commas)
        }
    }

    fun clearData() {
        start.clear()
        preImp.clear()
        saints.clear()
        postImp.clear()
    }

    private fun write(nPar: Int, minLen: Int, commas: Boolean) {
        var output = ""

        for (i in 0..nPar) {
            var par = ""

            if (i == 0) {
                val startPhrase = start.firstOrNull()
                startPhrase?.let { par += it }
            }

            while (par.length < minLen) {
                if (par.isEmpty()) {
                    par += "${pickRandom(preImp)} ${pickRandom(saints)} ${pickRandom(postImp)}"
                }

                if (commas) {
                    par += ","
                }

                par += " ${pickRandom(preImp).lowercase()} ${pickRandom(saints)} ${pickRandom(postImp)}"
            }

            par += "."
            output += "$par\n\n"
        }

        _textOut.value = output
    }

    private fun pickRandom(array: List<String>?): String {
        if (array != null && array.isNotEmpty()) {
            val max = array.size - 1
            val index = (0..max).random()
            return array[index]
        }

        return ""
    }
}