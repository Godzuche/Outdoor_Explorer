package com.godzuche.outdoorexplorer.ui.activities

import android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.godzuche.outdoorexplorer.R
import com.godzuche.outdoorexplorer.data.GeofencingChanges
import com.godzuche.outdoorexplorer.databinding.FragmentActivitiesBinding
import com.godzuche.outdoorexplorer.ui.GeofenceBroadcastReceiver
import com.godzuche.outdoorexplorer.ui.locations.LocationsFragment.Companion.RC_LOCATION
import com.godzuche.outdoorexplorer.ui.map.MapFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.jar.Manifest
import javax.crypto.spec.RC2ParameterSpec

class ActivitiesFragment : Fragment(), ActivitiesAdapter.OnClickListener {
    private lateinit var activitiesViewModel: ActivitiesViewModel
    private lateinit var geofencingClient: GeofencingClient
    private var geofencingChanges: GeofencingChanges? = null
    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(requireContext(),
            GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            requireContext(),
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
            )
    }

    private var _binding: FragmentActivitiesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentActivitiesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        activitiesViewModel = ViewModelProvider(this)[ActivitiesViewModel::class.java]

        geofencingClient = LocationServices.getGeofencingClient(requireActivity())

        val adapter = ActivitiesAdapter(this)
        binding.listActivities.adapter = adapter

        activitiesViewModel.allActivities.observe(viewLifecycleOwner, Observer {
            adapter.setActivities(it)
            if (it.any { a -> a.geofenceEnabled } && checkPermissions().isEmpty()) {
                Snackbar.make(requireView(),
                    getString(R.string.activities_background_reminder),
                    Snackbar.LENGTH_SHORT).show()
            }
        })
    }

    override fun onClick(id: Int, title: String) {
        val action = ActivitiesFragmentDirections
            .actionNavigationActivitiesToNavigationLocations()
        action.activityId = id
        action.title = "Locations with $title"
        val navController = Navigation.findNavController(requireView())
        navController.navigate(action)
    }

    override fun onGeofenceClick(id: Int) {
        geofencingChanges = activitiesViewModel.toggleGeofencing(id)
        handleGeofencing()
    }

    @SuppressLint("InlinedApi", "MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private fun handleGeofencing() {
        val neededPermissions = checkPermissions()
        if (neededPermissions.contains(ACCESS_FINE_LOCATION)) {
            requestPermission(
                R.string.activities_location_snackbar,
                R.string.activities_location_rationale,
                ACCESS_FINE_LOCATION
            )
        } else if (neededPermissions.contains(ACCESS_BACKGROUND_LOCATION)) {
            requestPermission(
                R.string.activities_background_snackbar,
                R.string.activities_background_rationale,
                ACCESS_BACKGROUND_LOCATION
            )
        } else if (geofencingChanges != null) {
            if (geofencingChanges!!.idsToRemove.isNotEmpty()) {
                geofencingClient.removeGeofences(geofencingChanges!!.idsToRemove)
            }

            if (geofencingChanges!!.locationsToAdd.isNotEmpty()) {
                val geofencingRequest = GeofencingRequest.Builder().apply {
                    addGeofences(geofencingChanges!!.locationsToAdd)
                    setInitialTrigger(Geofence.GEOFENCE_TRANSITION_ENTER)
                }.build()
                geofencingClient.addGeofences(
                    geofencingRequest,
                    pendingIntent
                )
            }
        }
    }

    private fun requestPermission(
        @StringRes snackbarMessage: Int,
        @StringRes rationaleMessage: Int,
        permission: String,
    ) {
        Snackbar.make(
            requireView(),
            getString(snackbarMessage),
            Snackbar.LENGTH_INDEFINITE
        )
            .setAction(R.string.ok) {
                EasyPermissions.requestPermissions(
                    this,
                    getString(rationaleMessage),
                    RC_LOCATION,
                    permission
                )
            }
            .show()
    }

    private fun checkPermissions(): List<String> {
        val permissionsNeeded = ArrayList<String>()
        if (!EasyPermissions.hasPermissions(requireContext(), ACCESS_FINE_LOCATION)) {
            permissionsNeeded.add(ACCESS_FINE_LOCATION)
        }
        //access to background permission for android Q and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !EasyPermissions.hasPermissions(
                requireContext(), ACCESS_BACKGROUND_LOCATION
            )
        ) {
            permissionsNeeded.add(ACCESS_BACKGROUND_LOCATION)
        }
        return permissionsNeeded
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults,
            this
        )
    }
}
