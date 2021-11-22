package com.godzuche.outdoorexplorer.ui.locations

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.outdoorexplorer.R
import com.godzuche.outdoorexplorer.data.Location
import com.google.android.material.card.MaterialCardView

class LocationsAdapter(private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<LocationsAdapter.LocationHolder>() {
    private var allLocations: List<Location> = ArrayList()
    private var currentLocation: android.location.Location? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_item, parent, false)
        return LocationHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allLocations.size
    }

    fun setLocations(locations: List<Location>) {
        allLocations = locations
        notifyDataSetChanged()
    }

    fun setCurrentLocation(location: android.location.Location) {
        currentLocation = location
        allLocations = allLocations.sortedBy { it.getDistanceInMiles(location) }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: LocationHolder, position: Int) {
        holder.bind(allLocations[position], onClickListener)
    }

    inner class LocationHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(location: Location, clickListener: OnClickListener) {
            with(itemView) {
                itemView.findViewById<TextView>(R.id.title).text = location.title
                itemView.findViewById<CardView>(R.id.card).setOnClickListener { clickListener.onClick(location.locationId) }

                if (currentLocation != null) {
                    itemView.findViewById<ImageView>(R.id.distanceIcon).visibility = View.VISIBLE

                    itemView.findViewById<TextView>(R.id.distance).visibility = View.VISIBLE
                    itemView.findViewById<TextView>(R.id.distance).text = context.getString(
                        R.string.distance_value,
                        location.getDistanceInMiles(currentLocation!!)
                    )
                }
            }
        }
    }

    interface OnClickListener {
        fun onClick(id: Int)
    }
}