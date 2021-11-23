package com.godzuche.outdoorexplorer.ui.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.godzuche.outdoorexplorer.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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

            //setting markers to the available locations from the viewModel
            mapViewModel.allLocations.observe(viewLifecycleOwner, Observer { locations ->
                for (location in locations) {
                    val point = LatLng(location.latitude, location.longitude)
                    map.addMarker(MarkerOptions()
                        .position(point)
                        .title(location.title)
                    )
                }
            })
        }
    }

//    private fun getBitmapFromVector(
//        @DrawableRes vectorResourceId: Int,
//        @ColorRes colorResourceId: Int
//    ): BitmapDescriptor {
//        val vectorDrawable = resources.getDrawable(vectorResourceId, requireContext().theme)
//            ?: return BitmapDescriptorFactory.defaultMarker()
//
//        val bitmap = Bitmap.createBitmap(
//            vectorDrawable.intrinsicWidth,
//            vectorDrawable.intrinsicHeight,
//            Bitmap.Config.ARGB_8888
//        )
//
//        val canvas = Canvas(bitmap)
//        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
//        DrawableCompat.setTint(
//            vectorDrawable,
//            ResourcesCompat.getColor(
//                resources,
//                colorResourceId, requireContext().theme
//            )
//        )
//        vectorDrawable.draw(canvas)
//        return BitmapDescriptorFactory.fromBitmap(bitmap)
//    }
}
