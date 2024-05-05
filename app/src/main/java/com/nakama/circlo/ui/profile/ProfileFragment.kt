package com.nakama.circlo.ui.profile

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nakama.circlo.R
import com.nakama.circlo.ui.MainActivity
import com.nakama.circlo.data.Result
import com.nakama.circlo.databinding.FragmentProfileBinding
import com.nakama.circlo.ui.profile.viewmodel.ProfileViewModel
import com.nakama.circlo.utils.confirmDialog
import com.nakama.circlo.utils.glide
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.showAnimationDialog
import com.nakama.circlo.utils.showBottomNavView
import com.nakama.circlo.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()
    private var loadingDialog: Dialog? = null

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

        loadingDialog = showAnimationDialog()
        getUserToken()
    }

    private fun getUserToken() {
        viewModel.getUser().observe(viewLifecycleOwner) { token ->
            if (token != "") {
                setupViewUser(token)
            } else {
                setupViewGuest()
            }
        }
    }

    private fun setupViewUser(token: String) {
        binding.apply {
            viewMustLogin.hide()
            viewAlreadyLogin.show()
            btnLogout.show()
            changeProfileAction.show()
            seeAddressAction.show()
        }
        getDetailProfile("Bearer $token")
        setupActionUser()
    }

    private fun setupViewGuest() {
        binding.apply {
            viewMustLogin.show()
            viewAlreadyLogin.hide()
            btnLogout.hide()
            changeProfileAction.hide()
            seeAddressAction.hide()
        }
        setupActionGuest()
    }

    private fun getDetailProfile(token: String) {
        viewModel.getProfile(token).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    loadingDialog?.show()
                }
                is Result.Success -> {
                    loadingDialog?.cancel()
                    binding.tvName.text = it.data.data?.user?.username
                    binding.ivProfil.glide(it.data.data?.user?.image.toString())
                    binding.tvPoint.text = it.data.data?.user?.point.toString()
                }
                is Result.Error -> {
                    loadingDialog?.cancel()
                    binding.btnLogin.isEnabled = true
                    Log.d("LoginFragment", it.error)
                }
            }
        }
    }

    private fun setupActionGuest() {
        binding.apply {
            btnLogin.setOnClickListener {
                actionToAuth()
            }
            changePanduanAction.setOnClickListener {
                toast(getString(R.string.info_dev_mode))
            }
            changeTermsAction.setOnClickListener {
                toast(getString(R.string.info_dev_mode))
            }
        }
    }

    private fun setupActionUser() {
        binding.apply {
            btnLogout.setOnClickListener {
                confirmDialog(
                        requireContext(),
                    "Confirm Logout",
                    "Are you sure want to logout?",
                    "Yes",
                    "No",
                    )
                {
                    viewModel.logout()
                    refreshPage()
                }
            }
            seeAddressAction.setOnClickListener {
                findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToAddressFragment(false))
            }
            changeProfileAction.setOnClickListener {
                toast(getString(R.string.info_dev_mode))
            }
            changePanduanAction.setOnClickListener {
                toast(getString(R.string.info_dev_mode))
            }
            changeTermsAction.setOnClickListener {
                toast(getString(R.string.info_dev_mode))
            }
        }
    }

    private fun refreshPage() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun actionToAuth() {
        findNavController().navigate(ProfileFragmentDirections.actionProfileFragmentToLoginFragment())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getUserToken()
    }

}