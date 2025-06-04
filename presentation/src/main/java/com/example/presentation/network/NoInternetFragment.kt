package com.example.presentation.network

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.presentation.databinding.FragmentNoInternetBinding

class NoInternetFragment : Fragment() {

    private var _binding: FragmentNoInternetBinding? = null
    private val binding get() = _binding!!

    private lateinit var networkLiveData: NetworkConnectionLiveData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoInternetBinding.inflate(inflater, container, false)
        networkLiveData = NetworkConnectionLiveData(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRetry.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRetry.isEnabled = false

            val isConnectedNow = networkLiveData.value == true

            if (isConnectedNow) {
                parentFragmentManager.beginTransaction()
                    .remove(this@NoInternetFragment)
                    .commitAllowingStateLoss()
            } else {
                Toast.makeText(requireContext(), "Интернет всё ещё недоступен", Toast.LENGTH_SHORT).show()
                binding.progressBar.visibility = View.GONE
                binding.btnRetry.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
