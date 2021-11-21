package com.godzuche.outdoorexplorer.ui.activities

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.godzuche.outdoorexplorer.data.OutdoorRepository
import com.godzuche.outdoorexplorer.data.OutdoorRoomDatabase
import com.godzuche.outdoorexplorer.data.OutdoorRoomRepository

class ActivitiesViewModel(application: Application) : AndroidViewModel(application) {
    private val outdoorRepository: OutdoorRepository

    init {
        val outdoorDao = OutdoorRoomDatabase.getInstance(application).outdoorDao()
        outdoorRepository = OutdoorRoomRepository(outdoorDao)
    }

    val allActivities = outdoorRepository.getAllActivities()

    fun toggleGeofencing(id: Int) = outdoorRepository.toggleActivityGeofence(id)
}