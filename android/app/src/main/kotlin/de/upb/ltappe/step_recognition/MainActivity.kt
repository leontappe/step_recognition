package de.upb.ltappe.step_recognition

import android.os.Bundle
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorManager
import android.hardware.SensorEventListener

import io.flutter.app.FlutterActivity
import io.flutter.plugins.GeneratedPluginRegistrant
import io.flutter.plugin.common.EventChannel

class StepListener(private val sensorManager: SensorManager) :
        EventChannel.StreamHandler,
        SensorEventListener {
    private var eventSink: EventChannel.EventSink? = null

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        println("Accuracy changed to ${accuracy.toString()}")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        eventSink?.success(1)
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        eventSink = events
        registerIfActive()
    }

    override fun onCancel(arguments: Any?) {
        eventSink = null
        unregisterIfActive()
    }

    // Lifecycle methods.
    public fun registerIfActive() {
        if (eventSink == null) return
        val stepDetector: Sensor = sensorManager!!.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
        // we could play around with samplingPeriodUs (3rd param) here for lower latency
        // e.g. SensorManger.SENSOR_DELAY_GAME
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL)
    }

    public fun unregisterIfActive() {
        if (eventSink == null) return
        sensorManager.unregisterListener(this)
    }
}

class MainActivity : FlutterActivity() {
    private val EVENTS_CHANNEL_STEPS = "step_recognition/events-steps"

    private var stepListener: StepListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        GeneratedPluginRegistrant.registerWith(this)

        // Get android's sensor service to access all sensor methods
        val sensorManager: SensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // Init StepListener with newly aquired manager and set it as stream handler for a new
        // event channel on 'step_recognition/events-steps'
        stepListener = StepListener(sensorManager)
        val channel = EventChannel(flutterView, EVENTS_CHANNEL_STEPS)
        channel.setStreamHandler(stepListener)
    }

    override fun onResume() {
        super.onResume()
        stepListener!!.registerIfActive()
    }

    override fun onPause() {
        super.onPause()
        stepListener!!.unregisterIfActive()
    }
}
