package com.godzuche.outdoorexplorer.ui.location

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.godzuche.outdoorexplorer.R
import com.godzuche.outdoorexplorer.data.Activity

class ActivitiesAdapter :
    RecyclerView.Adapter<ActivitiesAdapter.ActivityHolder>() {
    private var allActivities: List<Activity> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivityHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.location_activity_item, parent, false)
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
        holder.bind(allActivities[position])
    }

    inner class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(activity: Activity) {
            with(itemView) {
                itemView.findViewById<TextView>(R.id.title).text = activity.title

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
}