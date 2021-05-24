package com.example.izzy.models

data class LocationData(
        var timestamp: String? = null,
        var longitude: Double? = null,
        var latitude: Double? = null,
        var speed: Float? = null,
        var accuracy: Float? = null,
        var altitude: Double? = null,
        var bearing: Float? = null
)