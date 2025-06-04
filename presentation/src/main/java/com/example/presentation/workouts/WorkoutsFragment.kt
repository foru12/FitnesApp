package com.example.presentation.workouts

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.presentation.databinding.FragmentWorkoutsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController


@AndroidEntryPoint
class WorkoutsFragment : Fragment() {

    private var _binding: FragmentWorkoutsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: WorkoutsViewModel by viewModels()
    private lateinit var adapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
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
                    description = workout.description ?: "Error",
                    duration = workout.duration
                )
            findNavController().navigate(action)

        }

        binding.recyclerView.adapter = adapter

        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state.collect { state ->
                    when (state) {
                        is WorkoutsState.Loading -> {
                            binding.swipeRefreshLayout.isRefreshing = true
                        }
                        is WorkoutsState.Success -> {
                            binding.swipeRefreshLayout.isRefreshing = false
                            Log.e("MFPWE","dasdad")
                            adapter.submitList(state.workouts)
                        }
                        is WorkoutsState.Error -> {
                            binding.swipeRefreshLayout.isRefreshing = false
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshWorkouts()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
