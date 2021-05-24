package com.example.izzy.models

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import androidx.lifecycle.Observer
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import java.lang.Exception
import kotlin.properties.Delegates

object NetworkService {

    interface SendDataCallback {
        fun onSuccess(response: JSONObject)
        fun onFailure(error: String)
    }

    private lateinit var context : Context

    fun setInstance(context: Context) {
        this.context = context
    }

    private val requestQueue : RequestQueue by lazy {
        Volley.newRequestQueue(context)
    }

    private fun <T> addToRequestQueue(request: Request<T>) {
        requestQueue.add(request)
    }

    var isConnectionAvailable: Boolean by Delegates.observable(false) { _, _, _ ->  }

    fun startNetworkCallback() {
        val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val builder = NetworkRequest.Builder()

        connectionManager.registerNetworkCallback(
            builder.build(),
            networkCallback)
    }

    private val networkCallback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                isConnectionAvailable = true
            }
            override fun onLost(network: Network) {
                isConnectionAvailable = false
            }
    }

    fun stopNetworkCallback() {
        val connectionManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectionManager.unregisterNetworkCallback(networkCallback)
    }

    fun sendData(url : String, data: JSONObject, callback: SendDataCallback?) {
        try {
            val jsonRequest = JsonObjectRequest(Request.Method.POST, url, data,
                { response ->
                    println(response.toString())
                    callback?.onSuccess(response)
                }, { error ->
                    println("SEND DATA ERROR: ${error.message}")
                    error.message?.let { callback?.onFailure(it) }
                })

            addToRequestQueue(jsonRequest)

        } catch (ex: Exception) {
            println(ex.printStackTrace())
        }
    }

    fun checkUserExist() {
        val json = JSON()
        json.addParam("unique_id", UniqueID.get(context))
        println(json.generate())
        sendData(
            Config.API_URL + "/api/getaccount",
            json.generate(),
            object: SendDataCallback {
                override fun onSuccess(response: JSONObject) {
                    println("CHECK USER: SUCCESS! JSON: $response")
                }

                override fun onFailure(error: String) {
                    sendData(Config.API_URL + "/api/register", json.generate(),
                    object: SendDataCallback {
                        override fun onSuccess(response: JSONObject) {
                            println("Register success! JSON: {$response}")
                        }

                        override fun onFailure(error: String) {
                            println("Register ERROR! Error: $error")
                        }

                    })
                }
            })

    }

}