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
import com.google.android.exoplayer2.Tracks
import com.google.android.exoplayer2.source.TrackGroup
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.trackselection.TrackSelectionOverride
import com.google.android.exoplayer2.ui.TrackSelectionDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class VideoWorkoutFragment : Fragment() {

    private var _binding: FragmentVideoWorkoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoWorkoutViewModel by viewModels()
    private val args: VideoWorkoutFragmentArgs by navArgs()

    private var player: ExoPlayer? = null

    private lateinit var trackSelector: DefaultTrackSelector


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
        setargs()
        viewModel.loadVideo(args.id)
    }

    private fun setargs() {

        val args: VideoWorkoutFragmentArgs by navArgs()

        binding.tvTitle.text = args.title
        binding.tvDescription.text = args.description
        binding.tvDuration.text = "Длительность: ${args.duration} мин"

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
                setupExoPlayer(state.data.link)
            }
        }
    }


    private fun setupExoPlayer(videoUrl: String) {
        trackSelector = DefaultTrackSelector(requireContext()).apply {
            setParameters(buildUponParameters().setMaxVideoSizeSd())
        }

        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()

        binding.playerView.player = player

        val mediaItem = MediaItem.fromUri(BASE_URL + videoUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true

        binding.btnQuality.setOnClickListener {
            val tracks = player?.currentTracks
            val trackGroups = mutableListOf<Tracks.Group>()
            if (tracks != null){
                for (i in 0 until tracks.groups.size) {
                    trackGroups.add(tracks.groups[i])
                }

                val dialog = TrackSelectionDialogBuilder(
                    requireContext(),
                    "Выбор качества",
                    trackGroups
                ) { isDisabled, overrides -> }.build()

                dialog.show()
            }

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
