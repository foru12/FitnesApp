package com.example.presentation.workouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.domain.workout.WorkoutType
import com.example.presentation.R
import com.example.presentation.databinding.FragmentWorkoutsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkoutsFragment : Fragment() {

    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutsViewModel by viewModels()
    private lateinit var adapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = WorkoutAdapter { workout ->
            val action = WorkoutsFragmentDirections
                .actionWorkoutsFragmentToVideoFragment(
                    id = workout.id,
                    title = workout.title,
                    description = workout.description ?: "Нет описания",
                    duration = workout.duration
                )
            findNavController().navigate(action)
        }
        binding.recyclerView.adapter = adapter

        setupSearchView()
        setupTypeSpinner()
        observeViewModel()

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshWorkouts()
        }
    }

    private fun setupSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setQuery(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setQuery(newText ?: "")
                return true
            }
        })
    }

    private fun setupTypeSpinner() {
        val spinnerOptions: List<WorkoutType?> = listOf(null) + WorkoutType.values().toList()
        val labels = spinnerOptions.map {
            it?.name ?: getString(R.string.all_types)
        }
        val arrayAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            labels
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerType.adapter = it
        }

        binding.spinnerType.onItemSelectedListener = object :
            android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                viewModel.setType(spinnerOptions[position])
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                viewModel.setType(null)
            }
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.isLoading.collect { loading ->
                        binding.swipeRefreshLayout.isRefreshing = loading
                    }
                }

                launch {
                    viewModel.errorMessage.collect { error ->
                        if (error != null) {
                            binding.txtMessage.visibility = View.VISIBLE
                            binding.txtMessage.text = error
                            binding.recyclerView.visibility = View.GONE
                        }
                    }
                }
                launch {
                    viewModel.filteredWorkouts.collect { list ->
                        if (list.isEmpty()) {
                            binding.recyclerView.visibility = View.GONE
                            binding.txtMessage.visibility = View.VISIBLE
                            binding.txtMessage.text = "Тренировок пока нет"
                        } else {
                            binding.txtMessage.visibility = View.GONE
                            binding.recyclerView.visibility = View.VISIBLE
                            adapter.submitList(list)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
