package com.godzuche.outdoorexplorer.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.godzuche.outdoorexplorer.R
import com.godzuche.outdoorexplorer.databinding.FragmentLocationBinding

class LocationFragment : Fragment() {
    private var _binding: FragmentLocationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val locationViewModel = ViewModelProvider(this)[LocationViewModel::class.java]

        arguments?.let { bundle ->
            val passedArguments = LocationFragmentArgs.fromBundle(bundle)
            locationViewModel.getLocation(passedArguments.locationId)
                .observe(viewLifecycleOwner, Observer { wrapper ->
                    val location = wrapper.location
                    binding.title.text = location.title
                    binding.hours.text = location.hours
                    binding.description.text = location.description
                    val adapter = ActivitiesAdapter()
                    binding.listActivities.adapter = adapter
                    adapter.setActivities(wrapper.activities.sortedBy { a -> a.title })
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
