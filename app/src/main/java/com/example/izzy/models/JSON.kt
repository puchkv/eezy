package com.example.izzy.models

import org.json.JSONObject
import java.util.HashMap

class JSON {

    private val params  = HashMap<String, String>()

    fun addParam(key: String, param: String) {
        params[key] = param
    }

    fun generate(): JSONObject {
        return JSONObject(params as Map<String, String>)
    }
}