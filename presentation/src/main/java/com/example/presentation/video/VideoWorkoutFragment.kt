package com.example.presentation.video

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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackGroup
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.TrackSelectionParameters
import androidx.media3.common.Tracks
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.trackselection.DefaultTrackSelector
import androidx.media3.exoplayer.trackselection.MappingTrackSelector
import androidx.media3.ui.TrackSelectionDialogBuilder
import androidx.navigation.fragment.navArgs
import com.example.presentation.R
import com.example.presentation.databinding.FragmentVideoWorkoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@OptIn(UnstableApi::class)
@AndroidEntryPoint
class VideoWorkoutFragment : Fragment() {

    private var _binding: FragmentVideoWorkoutBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoWorkoutViewModel by viewModels()
    private val args: VideoWorkoutFragmentArgs by navArgs()

    @Inject
    @Named("BASE_URL")
    lateinit var baseUrl: String

    private var player: ExoPlayer? = null
    private lateinit var trackSelector: DefaultTrackSelector
    private var mappedTrackInfo: MappingTrackSelector.MappedTrackInfo? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = args.title
        binding.tvDescription.text = args.description
        binding.tvDuration.text = "Длительность: ${args.duration} мин"

        setupObserver()
        viewModel.loadVideo(args.id)
    }

    private fun setupObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is VideoWorkoutState.Loading -> {

                        }
                        is VideoWorkoutState.Error -> {
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                        is VideoWorkoutState.Success -> {
                            initializePlayer(state.data.link)
                        }
                    }
                }
            }
        }
    }

    private fun initializePlayer(videoUrl: String) {
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

        player?.addListener(object : Player.Listener {
            override fun onTracksChanged(tracks: Tracks) {
                mappedTrackInfo = trackSelector.currentMappedTrackInfo
            }
        })

        val normalizedBase = baseUrl.trimEnd('/')
        val normalizedPath = videoUrl.trimStart('/')
        val fullUri = "$normalizedBase/$normalizedPath"
        val mediaItem = MediaItem.fromUri(fullUri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true

        binding.playerView.findViewById<View>(R.id.exo_quality)
            .setOnClickListener {
                 showQualitySelectionDialog()
             }
    }

    private fun showQualitySelectionDialog() {
        val exoPlayer = player ?: return

        val mappedInfo = trackSelector.currentMappedTrackInfo ?: return
        val videoRendererIndex = (0 until mappedInfo.rendererCount)
            .firstOrNull { mappedInfo.getRendererType(it) == C.TRACK_TYPE_VIDEO }
            ?: return

        TrackSelectionDialogBuilder(
            requireContext(),
            "Выбор качества",
            exoPlayer,
            videoRendererIndex
        ).build().show()
    }



    override fun onStop() {
        super.onStop()
        player?.release()
        player = null
        mappedTrackInfo = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
