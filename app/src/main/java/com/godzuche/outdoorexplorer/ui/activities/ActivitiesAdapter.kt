package com.godzuche.outdoorexplorer.ui.activities

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.outdoorexplorer.R
import com.godzuche.outdoorexplorer.data.Activity
import com.google.android.material.card.MaterialCardView

class ActivitiesAdapter(private val onClickListener: OnClickListener) :
    RecyclerView.Adapter<ActivitiesAdapter.ActivityHolder>() {
    private var allActivities: List<Activity> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item, parent, false)
        return ActivityHolder(itemView)
    }

    override fun getItemCount(): Int {
        return allActivities.size
    }

    fun setActivities(activities: List<Activity>) {
        allActivities = activities
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
        holder.bind(allActivities[position], onClickListener)
    }

    inner class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(activity: Activity, clickListener: OnClickListener) {
            with(itemView) {
                itemView.findViewById<TextView>(R.id.title).text = activity.title

                itemView.findViewById<CardView>(R.id.card).setOnClickListener {
                    clickListener.onClick(activity.activityId, activity.title)
                }
                itemView.findViewById<ImageView>(R.id.geofence)
                    .setOnClickListener { clickListener.onGeofenceClick(activity.activityId) }

                var color = R.color.colorGray
                if (activity.geofenceEnabled) {
                    color = R.color.colorAccent
                }

                ImageViewCompat
                    .setImageTintList(
                        itemView.findViewById<ImageView>(R.id.geofence),
                        ColorStateList.valueOf(
                            ContextCompat.getColor(
                                itemView.findViewById<ImageView>(R.id.geofence).context,
                                color
                            )
                        )
                    )

                val iconUri = "drawable/ic_${activity.icon}_black_24dp"
                val imageResource: Int =
                    context.resources.getIdentifier(
                        iconUri, null, context.packageName
                    )
                itemView.findViewById<ImageView>(R.id.icon).setImageResource(imageResource)
                itemView.findViewById<ImageView>(R.id.icon).contentDescription = activity.title
            }
        }
    }

    interface OnClickListener {
        fun onClick(id: Int, title: String)
        fun onGeofenceClick(id: Int)
    }
}