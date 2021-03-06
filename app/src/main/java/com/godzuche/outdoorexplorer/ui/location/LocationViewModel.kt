package com.godzuche.outdoorexplorer.ui.location

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.godzuche.outdoorexplorer.data.OutdoorRepository
import com.godzuche.outdoorexplorer.data.OutdoorRoomDatabase
import com.godzuche.outdoorexplorer.data.OutdoorRoomRepository

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val outdoorRepository: OutdoorRepository

    init {
        val outdoorDao = OutdoorRoomDatabase.getInstance(application).outdoorDao()
        outdoorRepository = OutdoorRoomRepository(outdoorDao)
    }

    fun getLocation(locationId: Int) =
        outdoorRepository.getLocationWithActivities(locationId)
}