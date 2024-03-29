package com.nakama.circlo.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nakama.circlo.data.Result
import com.nakama.circlo.databinding.FragmentHomeBinding
import com.nakama.circlo.util.confirmDialog
import com.nakama.circlo.util.glide
import com.nakama.circlo.util.hide
import com.nakama.circlo.util.show
import com.nakama.circlo.util.showBottomNavView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        showBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserToken()
    }

    private fun getUserToken() {
        viewModel.getUser().observe(viewLifecycleOwner) { token ->
            setupViewUser(token)
        }
    }

    private fun setupViewUser(token: String) {
        if (token != "") {
            binding.apply {
                viewMustLogin.hide()
                tvName.show()
                tvWelcome.show()
                ivProfile.show()
                btnNotif.show()
            }
            getDetailProfile("Bearer $token")
            setupActionUser()
        } else {
            setupViewGuest()
        }
    }

    private fun setupViewGuest() {
        binding.apply {
            viewMustLogin.show()
            tvName.hide()
            tvWelcome.hide()
            ivProfile.hide()
            btnNotif.hide()
        }
        setupActionGuest()
    }

    private fun getDetailProfile(token: String) {
        viewModel.getProfile(token).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    binding.tvName.text = "Halo, ${it.data.data?.user?.username}"
                    binding.ivProfile.glide(it.data.data?.user?.image!!)
                    binding.tvTotalPoint.text = it.data.data.user.point.toString()
                }
                is Result.Error -> {
                    Log.d("LoginFragment", it.error)
                }
            }
        }
    }

    private fun setupActionGuest() {
        binding.apply {
            btnLogin.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToLoginFragment()
                findNavController().navigate(action)
            }
            cvPickup.setOnClickListener {
                confirmLogin()
            }
            cvDropoff.setOnClickListener {
                confirmLogin()
            }
            cvExplore.setOnClickListener {
                confirmLogin()
            }
        }
    }

    private fun setupActionUser() {
        binding.apply {
            cvPickup.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToPickUpFragment()
                findNavController().navigate(action)
            }
            cvDropoff.setOnClickListener {
                val action = HomeFragmentDirections.actionHomeFragmentToDropOffFragment()
                findNavController().navigate(action)
            }
        }
    }

    private fun confirmLogin() {
            confirmDialog(
                requireContext(),
                "Anda belum login",
                "Login sekarang?",
                "Ya",
                "Tidak"
            ) {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToLoginFragment())
            }
    }
}