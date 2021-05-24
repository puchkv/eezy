package com.example.izzy.ui.main.tracker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.izzy.BuildConfig
import com.example.izzy.R
import com.example.izzy.models.*
import org.json.JSONObject
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class TrackerFragment : Fragment() {

    companion object {
        fun newInstance() = TrackerFragment()
    }

    private lateinit var viewModel: TrackerViewModel

    private lateinit var longitude : TextView
    private lateinit var latitude : TextView
    private lateinit var accuracy : TextView
    private lateinit var speed: TextView
    private lateinit var altitude: TextView
    private lateinit var bearing: TextView

    private lateinit var map : MapView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.tracker_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        longitude = view.findViewById(R.id.longitude_tv)
        latitude = view.findViewById(R.id.latitude_tv)
        accuracy = view.findViewById(R.id.accuracy_tv)
        speed = view.findViewById(R.id.speed_tv)
        altitude = view.findViewById(R.id.altitude_tv)
        bearing = view.findViewById(R.id.bearing_tv)

        Configuration.getInstance().userAgentValue = BuildConfig.APPLICATION_ID

        map = view.findViewById(R.id.map)
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller?.setZoom(17.0)
        map.controller.setCenter(GeoPoint(47.0951, 37.5413 ))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(TrackerViewModel::class.java)

        initLocationUpdates()

    }

    private fun initLocationUpdates() {
        viewModel.getLocation().observe(viewLifecycleOwner, Observer {
            latitude.text = it.latitude.toString()
            longitude.text = it.longitude.toString()
            accuracy.text = String.format("%.1f м", it.accuracy)
            speed.text = String.format("%.2f м/c", it.speed)
            altitude.text = String.format("%.2f м", it.altitude)
            bearing.text = String.format("%d°", it.bearing?.toInt())

            initSendUpdates(it)

            refreshMap(it.longitude, it.latitude, it.accuracy)

        })
    }

    private fun initSendUpdates(it : LocationData) {
            val json = JSON()
            json.addParam("unique_id", UniqueID.get(requireContext()))
            json.addParam("longitude", it.longitude.toString())
            json.addParam("latitude", it.latitude.toString())
            json.addParam("speed", it.speed.toString())
            json.addParam("accuracy", it.accuracy.toString())
            json.addParam("altitude", it.altitude.toString())
            json.addParam("bearing", it.bearing.toString())
            it.timestamp?.let { timestamp -> json.addParam("timestamp", timestamp) }

            if(NetworkService.isConnectionAvailable) {
                NetworkService.sendData(Config.API_URL + "/store", json.generate(),
                object: NetworkService.SendDataCallback {
                    override fun onSuccess(response: JSONObject) {
                        println("Location data updates successfully!\n{$response}")
                    }
                    override fun onFailure(error: String) {
                        println("Location data updates error: $error")
                    }

                })
            }
    }

    private fun refreshMap(longitude: Double?, latitude: Double?, accuracy: Float?) {
        map.overlayManager.clear()

        val point = latitude?.let { lat ->
            longitude?.let { lon ->
                GeoPoint(lat, lon)
            }
        }

        val marker = Marker(map)
        marker.position = point
        marker.setAnchor(Marker.ANCHOR_CENTER,  Marker.ANCHOR_BOTTOM)

        map.overlays?.add(marker)
    }

}