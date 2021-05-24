package com.example.izzy.models

import android.content.Context
import android.content.SharedPreferences
import java.util.*

object UniqueID {

    fun get(context: Context) : String {
        val pref = context.getSharedPreferences(
            Config.PREF_UNIQUE_ID, Context.MODE_PRIVATE)

        var uniqueId = pref.getString(Config.PREF_UNIQUE_ID, null)

        if(uniqueId == null) {
            uniqueId = UUID.randomUUID().toString()
            val editor = pref.edit().apply {
                putString(Config.PREF_UNIQUE_ID, uniqueId)
                apply()
            }
        }

        return uniqueId

    }

}