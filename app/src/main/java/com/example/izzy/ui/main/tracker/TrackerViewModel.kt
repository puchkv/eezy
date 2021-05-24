package com.example.izzy.ui.main.tracker

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.example.izzy.models.LocationManager
import kotlinx.coroutines.Job

class TrackerViewModel(application: Application) : AndroidViewModel(application) {

    fun getLocation() = LocationManager.location

}