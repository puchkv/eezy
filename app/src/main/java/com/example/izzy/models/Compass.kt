package com.example.izzy.models

import android.view.animation.RotateAnimation

class Compass {

    var lastAcc = FloatArray(3)
    var lastMag = FloatArray(3)
    var lastAccSet = false
    var lastMagSet = false

    var currentDegree: Float = 0.0f
    lateinit var rotateAnimation: RotateAnimation
}