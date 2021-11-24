package com.godzuche.outdoorexplorer.ui.locations

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.godzuche.outdoorexplorer.MainActivity
import com.godzuche.outdoorexplorer.R
import com.godzuche.outdoorexplorer.databinding.FragmentLocationsBinding
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import pub.devrel.easypermissions.EasyPermissions

class LocationsFragment : Fragment(), LocationsAdapter.OnClickListener {
    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: LocationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLocationsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val locationsViewModel = ViewModelProvider(this)[LocationsViewModel::class.java]

        adapter = LocationsAdapter(this)
        binding.listLocations.adapter = adapter

        arguments?.let { bundle ->
            val passedArguments = LocationsFragmentArgs.fromBundle(bundle)
            if (passedArguments.activityId == 0) {
                locationsViewModel.allLocations.observe(viewLifecycleOwner, Observer {
                    adapter.setLocations(it)
                })
            } else {
                locationsViewModel.locationsWithActivity(passedArguments.activityId)
                    .observe(viewLifecycleOwner, Observer {
                        adapter.setLocations(it.locations)
                    })
            }
        }

//        getCurrentLocation()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())

            //rather than driving directions, the last location will do for our use case
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    adapter.setCurrentLocation(location)
                }
            }
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
            Snackbar.make(requireView(),
                getString(R.string.locations_snackbar),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok) {
                    /*EasyPermissions.requestPermissions(this, getString(R.string.locations_rationale),
                        RC_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)*/

                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(getString(R.string.locations_rationale))
                        .setPositiveButton("Ok") {_,_ ->
                            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        .setNegativeButton("No Thanks", null)
                        .show()
                }.show()
        } else {
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

   /* @SuppressLint("MissingPermission")
    @AfterPermissionGranted(RC_LOCATION)
    private fun getCurrentLocation() {
        //checking if the user already has the fine location permission granted already
        if (EasyPermissions.hasPermissions(requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
        ) {
            //the instance of fusedLocationProviderClient will give us an object to get location data from
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireActivity())

            //rather than driving directions, the last location will do for our use case
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    adapter.setCurrentLocation(location)
                }
            }
        } else {
            Snackbar.make(requireView(),
                getString(R.string.locations_snackbar),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok) {
                    EasyPermissions.requestPermissions(this,
                        getString(R.string.locations_rationale),
                        RC_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                }.show()
        }
    }*/


@SuppressLint("MissingPermission")
val locationPermissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            when {
                isGranted -> {
                    //the instance of fusedLocationProviderClient will give us an object to get location data from
                    val fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(requireActivity())

                    //rather than driving directions, the last location will do for our use case
                    fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            adapter.setCurrentLocation(location)
                        }
                    }
                }
                /*!shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                    // permission denied, the user has checked the Don't ask again.
                }*/
                else -> {
                    //access to location is prohibited
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(getString(R.string.locations_rationale))
                        .setPositiveButton("Ok", null) /*{_,_ ->
                            locationPermissionRequest.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
                        }*/
                        .setNegativeButton("No Thanks", null)
                        .show()
                }
            }
        }

/*    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }*/

    override fun onClick(id: Int) {
        val action = LocationsFragmentDirections
            .actionNavigationLocationsToNavigationLocation()
        action.locationId = id
        val navController = Navigation.findNavController(requireView())
        navController.navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val RC_LOCATION = 10
    }
}
