package com.example.izzy.ui.main

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.Chronometer
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.izzy.R
import com.example.izzy.models.JSON
import com.example.izzy.models.LocationManager
import com.example.izzy.models.NetworkService
import com.example.izzy.ui.permissions.PermissionFragment
import com.example.izzy.models.PermissionManager
import com.example.izzy.ui.services.ServiceFragment
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var chronometer : Chronometer

    companion object {
        fun newInstance() = MainFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NetworkService.setInstance(requireContext())
        NetworkService.startNetworkCallback()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        checkPermissions()
        checkLocationServices()



        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView(view)

        startLocationUpdates(requireContext())

        NetworkService.checkUserExist()

    }

    override fun onDestroy() {
        super.onDestroy()
        if(LocationManager.checkLocationEnabled(requireContext())) {
            LocationManager.stopLocationUpdates()
        }
        NetworkService.stopNetworkCallback()
        chronometer.stop()
    }

    private fun checkPermissions() {
        if(!PermissionManager.checkPermissions(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION))
        {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(this.id, PermissionFragment.newInstance())
                .commit()
        }
    }

    private fun checkLocationServices() {
        if(!LocationManager.checkLocationEnabled(requireContext())) {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(this.id, ServiceFragment.newInstance())
                .commit()
        }
    }

    private fun startLocationUpdates(context: Context) {
        if(PermissionManager.checkPermissions(
                        context, Manifest.permission.ACCESS_FINE_LOCATION)) {
            if(LocationManager.checkLocationEnabled(context)) {
                LocationManager.startLocationUpdates(context)
                chronometer.base = SystemClock.elapsedRealtime()
                chronometer.start()
            }
        }
    }

    private fun setupView(view: View) {

        chronometer = view.findViewById(R.id.chronometer)
        var currentTime : Long = chronometer.base
        val internet = view.findViewById<TextView>(R.id.internet_tv)
        internet.visibility = View.GONE

        chronometer.setOnChronometerTickListener {
            val elapsedTime = (SystemClock.elapsedRealtime() - currentTime)
            if(elapsedTime >= 5000) {
                checkLocationServices()
                if(!NetworkService.isConnectionAvailable) {
                    internet.visibility = View.VISIBLE
                } else {
                    internet.visibility = View.GONE
                }
                currentTime = SystemClock.elapsedRealtime()
            }
        }

    }

}