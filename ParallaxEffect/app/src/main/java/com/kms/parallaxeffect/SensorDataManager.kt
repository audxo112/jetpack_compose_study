package com.kms.parallaxeffect

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.channels.Channel

class SensorDataManager(context: Context) : SensorEventListener {

    private val sensorManager by lazy{
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    init{
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        val magnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        sensorManager.registerListener(this, accel, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(this, magnet, SensorManager.SENSOR_DELAY_UI)
    }

    private var gravity:FloatArray? = null
    private var geomagnetic:FloatArray? = null
//    private val r = FloatArray(9)
//    private val i = FloatArray(9)
//    private val orientation = FloatArray(3)

    val data:Channel<SensorData> = Channel(Channel.UNLIMITED)

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GRAVITY){
            gravity = event.values
        }

        if (event?.sensor?.type == Sensor.TYPE_MAGNETIC_FIELD){
            geomagnetic = event.values
        }

        if (gravity != null && geomagnetic != null){
            val r = FloatArray(9)
            val i = FloatArray(9)
            if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)){
                val orientation = FloatArray(3)
                SensorManager.getOrientation(r, orientation)
                data.trySend(SensorData(roll = orientation[2], pitch = orientation[1]))
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun cancel(){
        sensorManager.unregisterListener(this)
    }
}