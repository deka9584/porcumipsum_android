package org.porcumipsum.clients

import android.content.Context
import android.util.Log
import com.android.volley.NoConnectionError
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONException
import org.json.JSONObject

object PorkApiClient {
    private const val webUrl = "http://salaandrea.altervista.org/porcumipsum"
    private const val apiUrl = "$webUrl/api.php"

    private fun getAPIErrorMessage(error: VolleyError): String {
        if (error is NoConnectionError) {
            return "Unable to reach the server"
        }

        try {
            val responseString = String(error.networkResponse?.data ?: byteArrayOf())
            val jsonResponse = JSONObject(responseString)

            return "${jsonResponse.optString("message", "Error")} (${error.networkResponse?.statusCode})"
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return "Unknown error"
    }

    fun fetchPorks(context: Context, onSuccess: (JSONObject) -> Unit, onError: (String) -> Unit) {
        val reqQueue = Volley.newRequestQueue(context)
        val url = "${apiUrl}?list=all"

        val listener = Response.Listener<String> { response ->
            val responseJSON = JSONObject(response)
            onSuccess(responseJSON)
        }

        val errorListener = Response.ErrorListener { error ->
            val message = getAPIErrorMessage(error)
            onError(message)
            Log.e("serverApi", error.toString())
        }

        val request = object : StringRequest(
            Method.GET, url, listener, errorListener) {

            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Accept"] = "application/json"
                return headers
            }
        }

        reqQueue.add(request)
    }

    fun getWebUrl(): String {
        return webUrl
    }
}