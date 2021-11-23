package com.godzuche.outdoorexplorer.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.godzuche.outdoorexplorer.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? =
        inflater.inflate(R.layout.fragment_map, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val mapViewModel = ViewModelProvider(this)[MapViewModel::class.java]


        //get reference to the google maps fragment in the layout using the fragment manager
        val maps = childFragmentManager.findFragmentById(R.id.maps) as SupportMapFragment
        maps.getMapAsync { map ->
            //lat and lng of San Francisco
            //we center the map to San Francisco since all of our locations are within San Fransisco
            val bay = LatLng(37.747063, -122.329030)
            //zoom level
            map.moveCamera(CameraUpdateFactory.zoomTo(10f))
            map.moveCamera(CameraUpdateFactory.newLatLng(bay))

            //update the map's ui settings
            map.uiSettings.apply {
                //zoom control buttons
                isZoomControlsEnabled = true
                isTiltGesturesEnabled = false
            }

            //setting markers to the available locations from the viewModel
            mapViewModel.allLocations.observe(viewLifecycleOwner, Observer { locations ->
                for (location in locations) {
                    val point = LatLng(location.latitude, location.longitude)
                    val marker = map.addMarker(MarkerOptions()
                        .position(point)
                        .title(location.title)
                        //updating the snippet
                        .snippet("Hours: ${location.hours}")
                        //change marker icon. It requires a bitmap resource
                        .icon(getBitmapFromVector(R.drawable.ic_star_black_24dp,
                            R.color.colorAccent)
                        )
                        //opacity of the icon
                        .alpha(0.75F)
                    )
                    //set marker tag for id since markers don't have a unique id
                    marker?.tag = location.locationId
                }
            })

            //set click listener on the infoWindow
            map.setOnInfoWindowClickListener { marker ->
                val action = MapFragmentDirections.actionNavigationMapToNavigationLocation()
                action.locationId = marker.tag as Int
                val navController = Navigation.findNavController(requireView())
                navController.navigate(action)
            }
        }
    }

    //method for converting vector to bitmap
    private fun getBitmapFromVector(
        @DrawableRes vectorResourceId: Int,
        @ColorRes colorResourceId: Int,
    ): BitmapDescriptor {
        val vectorDrawable = resources.getDrawable(vectorResourceId, requireContext().theme)
            ?: return BitmapDescriptorFactory.defaultMarker()

        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(
            vectorDrawable,
            ResourcesCompat.getColor(
                resources,
                colorResourceId, requireContext().theme
            )
        )
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
