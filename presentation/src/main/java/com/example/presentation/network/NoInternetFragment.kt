package com.example.presentation.network

import android.content.Context
import android.net.ConnectivityManager
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.presentation.R
import com.example.presentation.databinding.FragmentNoInternetBinding


class NoInternetFragment : Fragment() {

    private var _binding: FragmentNoInternetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoInternetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnRetry.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            binding.btnRetry.isEnabled = false

            Handler(Looper.getMainLooper()).postDelayed({
                if (isInternetAvailable(requireContext())) {
                    parentFragmentManager.beginTransaction()
                        .remove(this@NoInternetFragment)
                        .commitAllowingStateLoss()
                } else {
                    Toast.makeText(requireContext(), "Интернет всё ещё недоступен", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                    binding.btnRetry.isEnabled = true
                }
            }, 1500)
        }
    }

    private fun isInternetAvailable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
