package com.godzuche.outdoorexplorer.ui.locations

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.godzuche.outdoorexplorer.R
import com.godzuche.outdoorexplorer.databinding.FragmentLocationBinding
import com.godzuche.outdoorexplorer.databinding.FragmentLocationsBinding

class LocationsFragment : Fragment(), LocationsAdapter.OnClickListener {
    private var _binding: FragmentLocationsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: LocationsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
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
    }

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
}
