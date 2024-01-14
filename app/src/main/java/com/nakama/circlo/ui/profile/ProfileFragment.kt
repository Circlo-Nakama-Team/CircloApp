package com.nakama.circlo.ui.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nakama.circlo.databinding.FragmentProfileBinding
import com.nakama.circlo.ui.profile.viewmodel.ProfileViewModel
import com.nakama.circlo.util.hide
import com.nakama.circlo.util.show
import com.nakama.circlo.util.showBottomNavView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()
    var userToken: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        showBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserToken()
        setupView()
    }

    private fun getUserToken() {
        viewModel.getUser().observe(viewLifecycleOwner) { token ->
            if (token != null) {
                userToken = token
            }
        }
    }

    private fun setupView() {
        if (userToken != "") {
            binding.apply {
                viewMustLogin.hide()
                viewAlreadyLogin.show()
                btnLogout.show()
                changeProfileAction.show()
            }

        } else {
            binding.apply {
                viewMustLogin.show()
                viewAlreadyLogin.hide()
                btnLogout.hide()
                changeProfileAction.hide()
            }
            setupActionGuest()
        }
    }

    private fun setupActionGuest() {
        binding.apply {
            btnLogin.setOnClickListener {
                actionToAuth()
            }
        }
    }

    private fun setupActionUser() {
        binding.apply {

        }
    }



    private fun actionToAuth() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}