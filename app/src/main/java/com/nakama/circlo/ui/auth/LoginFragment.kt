package com.nakama.circlo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.nakama.circlo.ui.MainActivity
import com.nakama.circlo.R
import com.nakama.circlo.data.Result
import com.nakama.circlo.databinding.FragmentLoginBinding
import com.nakama.circlo.util.Constants.SERVER_CLIENT_ID
import com.nakama.circlo.util.hideBottomNavView
import com.nakama.circlo.util.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewmodel by viewModels<AuthViewModel>()
    private lateinit var fcmToken: String
    private lateinit var userToken: String

    var auth = FirebaseAuth.getInstance()

    companion object {
        const val TAG = "Login Fragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAction()
    }

    private fun setupAction() {
        binding.apply {
            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            btnGoogle.setOnClickListener {
                viewLifecycleOwner.lifecycleScope.launch {
                    fcmToken = viewmodel.getFcmToken()
                    googleSignIn()
                }

            }
            btnLogin.setOnClickListener {
                validateLogin()
            }
        }
    }

    private fun validateLogin() {
        val email = binding.edLoginEmail.text.toString()
        val password = binding.edLoginPassword.text.toString()
        if (email.isEmpty()) {
            binding.edLoginEmail.error = "Email cannot be empty"
            binding.edLoginEmail.requestFocus()
        } else if (password.isEmpty()) {
            binding.edLoginPassword.error = "Password cannot be empty"
            binding.edLoginPassword.requestFocus()
        } else {
            observeLogin(email, password, fcmToken)
        }
    }

    private fun observeLogin(email: String, password: String, fcmToken: String) {
        viewmodel.login(email, password, fcmToken).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressIndicator.show()
                    binding.btnLogin.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressIndicator.hide()
                    toast("Login Success")
                    Log.d("User Token Login", it.data.data?.credential.toString())
                    navigateToHome(it.data.data?.credential.toString())
                }
                is Result.Error -> {
                    binding.progressIndicator.hide()
                    binding.btnLogin.isEnabled = true
                    toast(it.error)
                }
            }
        }
    }

    private fun observeRegisterGoogleAccount(
        firstname: String,
        username: String,
        email: String,
        fcmToken: String
    ) {
        viewmodel.registerGoogle(firstname, username, email, fcmToken).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressIndicator.show()
                    binding.btnLogin.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressIndicator.hide()
                    Log.d("Response Register", it.data.toString())
                    navigateToHome(userToken)
                    Log.d("User Token new", fcmToken)
                }
                is Result.Error -> {
                    binding.progressIndicator.hide()
                    binding.btnLogin.isEnabled = true
                    if (it.error.equals("HTTP 400 ")) {
                        navigateToHome(userToken)
                        Log.d("User Token already", fcmToken)
                    }
                    toast(it.error)
                }
            }
        }
    }

    private fun navigateToHome(token: String) {
        runBlocking {
            launch {
                viewmodel.saveUser(token)
                delay(1000)
            }
        }
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private suspend fun googleSignIn() {
        val credentialManager = CredentialManager.create(requireContext())
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(SERVER_CLIENT_ID)
            .setNonce("")
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

        coroutineScope {
            launch {
                try {
                    val result = credentialManager.getCredential(
                        request = request,
                        context = requireContext(),
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "Get credential error", e)
                }
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)

                        userToken = googleIdTokenCredential.idToken
                        firebaseAuthGoogle(googleIdTokenCredential.idToken)
                        Log.d(TAG, googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        toast("Received an invalid google id token response")
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type
                    toast("Unexpected type of credential")
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                toast("Login Success")
                observeRegisterGoogleAccount(
                    it.user?.displayName.toString(),
                    it.user?.displayName.toString(),
                    it.user?.email.toString(),
                    fcmToken
                )
            }
            .addOnFailureListener {
                toast(it.localizedMessage)
            }
    }
}