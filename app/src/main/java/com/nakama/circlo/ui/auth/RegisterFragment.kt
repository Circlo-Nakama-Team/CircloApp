package com.nakama.circlo.ui.auth

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nakama.circlo.data.Result
import com.nakama.circlo.databinding.FragmentRegisterBinding
import com.nakama.circlo.utils.confirmDialog
import com.nakama.circlo.utils.hideBottomNavView
import com.nakama.circlo.utils.showAnimationDialog
import com.nakama.circlo.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewmodel by viewModels<AuthViewModel>()
    private var loadingDialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
            _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = showAnimationDialog()
        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
            btnRegister.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    validateRegister()
                }
            }
        }
    }

    private fun validateRegister() {
        val firstname = binding.edRegisterFirstName.text.toString()
        val lastname = binding.edRegisterLastName.text.toString()
        val username = binding.edRegisterUsername.text.toString()
        val email = binding.edRegisterEmail.text.toString()
        val password = binding.edRegisterPassword.text.toString()
        if (email.isEmpty()) {
            binding.edRegisterEmail.error = "Email cannot be empty"
            binding.edRegisterEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.edRegisterEmail.error = "Password cannot be empty"
            binding.edRegisterEmail.requestFocus()
        } else if (firstname.isEmpty()) {
            binding.edRegisterEmail.error = "First Name cannot be empty"
            binding.edRegisterEmail.requestFocus()
        } else if (lastname.isEmpty()) {
            binding.edRegisterEmail.error = "Last Name cannot be empty"
            binding.edRegisterEmail.requestFocus()
        } else if (username.isEmpty()) {
            binding.edRegisterEmail.error = "Username cannot be empty"
            binding.edRegisterEmail.requestFocus()
        }
        else {
            observeRegister(firstname, lastname, username, email, password)
        }
    }

    private fun observeRegister(firstname: String, lastname: String, username: String, email: String, password: String) {
        viewmodel.register(firstname, lastname, username, email, password).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    loadingDialog?.show()
                    binding.btnRegister.isEnabled = false
                }
                is Result.Success -> {
                    loadingDialog?.cancel()
                    confirmDialog(
                        requireContext(),
                        "Register Success",
                        "Please check your email for verification",
                        "Continue",
                        ""
                    ) {
                        navigateToLogin()
                    }
                }
                is Result.Error -> {
                    loadingDialog?.cancel()
                    binding.btnBack.isEnabled = true
                    toast(it.error)
                }
            }
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToLoginFragment())
    }

}