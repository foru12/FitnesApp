package com.example.presentation.video

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.ui.TrackSelectionDialogBuilder
import com.example.presentation.R
import com.example.presentation.databinding.FragmentVideoWorkoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@UnstableApi
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
        setargs()
        setupObserver()
        viewModel.loadVideo(args.id)
    }

    private fun setargs() {
        binding.tvTitle.text = args.title
        binding.tvDescription.text = args.description
        binding.tvDuration.text = "Длительность: ${args.duration} мин"
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state -> renderState(state) }
            }
        }
    }

    private fun renderState(state: VideoWorkoutState) {
        when (state) {
            is VideoWorkoutState.Loading -> {
                // можно добавить лоадер
            }

            is VideoWorkoutState.Error -> {
                Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
            }

            is VideoWorkoutState.Success -> {
                setupExoPlayer(state.data.link)
            }
        }
    }

    private fun setupExoPlayer(videoUrl: String) {
        trackSelector = DefaultTrackSelector(requireContext()).apply {
            setParameters(buildUponParameters())
        }

        player = ExoPlayer.Builder(requireContext())
            .setTrackSelector(trackSelector)
            .build()

        binding.playerView.player = player
        binding.playerView.setUseController(true)
        binding.playerView.setControllerAutoShow(true)
        binding.playerView.setControllerShowTimeoutMs(1000)

        // Кнопка выбора качества
        val qualityButton = binding.playerView.findViewById<View>(R.id.exo_quality)
        qualityButton?.setOnClickListener {
            val tracks = player?.currentTracks ?: return@setOnClickListener
            val videoTrackGroups = tracks.groups.filter { it.type == C.TRACK_TYPE_VIDEO }

            if (videoTrackGroups.isNotEmpty()) {
                TrackSelectionDialogBuilder(
                    requireContext(),
                    "Выбор качества",
                    videoTrackGroups
                ) { isDisabled, overrides ->
                    trackSelector.setParameters(
                        trackSelector.parameters
                            .buildUpon()
                            .setRendererDisabled(0, isDisabled)
                            .build()
                    )

                }.build().show()
            }
        }

        val mediaItem = MediaItem.fromUri(BASE_URL + videoUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
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
