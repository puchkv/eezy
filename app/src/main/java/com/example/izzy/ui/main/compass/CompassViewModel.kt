    package com.example.izzy.ui.main.compass

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.izzy.models.Compass

class CompassViewModel(application: Application) : AndroidViewModel(application) {

    private var acc : Sensor? = null
    private var mag : Sensor? = null

    inner class CompassLiveData : MutableLiveData<Compass>(), SensorEventListener {

        private var compass: Compass = Compass()

        private val sensorManager
            get() = getApplication<Application>().getSystemService(Context.SENSOR_SERVICE) as SensorManager

        override fun onSensorChanged(event: SensorEvent) {
            if(event.sensor === acc) {
                lowPass(event.values, compass.lastAcc)
                compass.lastAccSet = true
            } else if (event.sensor === mag) {
                lowPass(event.values, compass.lastMag)
                compass.lastMagSet = true
            }

            if(compass.lastAccSet && compass.lastMagSet) {
                val rotationMatrix = FloatArray(9)

                if(SensorManager.getRotationMatrix(rotationMatrix, null, compass.lastAcc, compass.lastMag)) {
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)

                    val degree = (Math.toDegrees(orientation[0].toDouble()) + 360).toFloat() % 360

                    val rotateAnimation = RotateAnimation(
                        compass.currentDegree,
                        -degree,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f)

                    rotateAnimation.duration = 1000
                    rotateAnimation.fillAfter = true

                    //compass.startAnimation(rotateAnimation)
                    compass.rotateAnimation = rotateAnimation
                    compass.currentDegree = -degree

                    postValue(compass)
                }
            }
        }

        private fun lowPass(input: FloatArray, output: FloatArray) {

            val alpha = 0.05f

            for(i in input.indices) {
                output[i] = output[i] + alpha * (input[i] - output[i])
            }
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) { }

        override fun onActive() {
            sensorManager.let {sm ->
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER).let {
                    acc = it
                    sm.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
                }
            }

            sensorManager.let { sm ->
                sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD).let {
                    mag = it
                    sm.registerListener(this, mag, SensorManager.SENSOR_DELAY_GAME)
                }
            }

            super.onActive()
        }

        override fun onInactive() {
            sensorManager.unregisterListener(this, mag)
            sensorManager.unregisterListener(this, acc)
            super.onInactive()
        }
    }
}