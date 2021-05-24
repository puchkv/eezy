package com.example.izzy.models

import android.content.SharedPreferences
import com.google.android.gms.location.LocationRequest
import java.util.concurrent.TimeUnit

object Config {

    const val API_URL = "http://5.105.226.28"

    var NIGHT_MODE : Boolean = false

    const val REQUEST_CHECK_SETTINGS_CODE = 10000

    var UPDATE_INTERVAL : Long = TimeUnit.SECONDS.toMillis(5)
    var FASTEST_INTERVAL : Long = TimeUnit.SECONDS.toMillis(1)

    const val LOCATION_PRIORITY = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY

    const val PREF_UNIQUE_ID = "PREF_UNIQUE_ID"

}