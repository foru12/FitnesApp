package com.example.presentation.video

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import com.example.presentation.databinding.FragmentVideoWorkoutBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoWorkoutFragment : Fragment() {

    private var _binding: FragmentVideoWorkoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoWorkoutViewModel by viewModels()
    private val args: VideoWorkoutFragmentArgs by navArgs()

    private var player: ExoPlayer? = null

    private val BASE_URL = "https://ref.test.kolsa.ru"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObserver()
        viewModel.loadVideo(args.id)
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
    }

    private fun renderState(state: VideoWorkoutState) {
        when (state) {
            is VideoWorkoutState.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
            }

            is VideoWorkoutState.Error -> {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }

            is VideoWorkoutState.Success -> {
                binding.progressBar.visibility = View.GONE

                val workout = state.data
                binding.tvTitle.text = "Тренировка #${workout.id}"
                binding.tvDuration.text = "Длительность: ${workout.duration} минут"
                binding.tvDescription.text = "Описание недоступно"

                setupExoPlayer(workout.link)
            }
        }
    }


    private fun setupExoPlayer(videoUrl: String) {
        player = ExoPlayer.Builder(requireContext()).build().also { exoPlayer ->
            binding.playerView.player = exoPlayer
            val fullUrl = "$BASE_URL$videoUrl"
            val mediaItem = MediaItem.fromUri(fullUrl)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }

    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
