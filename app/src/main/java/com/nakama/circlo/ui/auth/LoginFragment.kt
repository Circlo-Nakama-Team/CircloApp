package com.nakama.circlo.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewmodel by viewModels<AuthViewModel>()

    var auth = FirebaseAuth.getInstance()
     lateinit var gsc: GoogleSignInClient

    companion object {
        const val RC_SIGN_IN = 120
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

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(SERVER_CLIENT_ID)
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(requireContext(), gso)
        setupAction()

    }

    private fun setupAction() {
        binding.apply {
            tvRegister.setOnClickListener {
                findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
            btnGoogle.setOnClickListener {
                googleSignIn()
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
            observeLogin(email, password)
        }
    }

    private fun observeLogin(email: String, password: String) {
        viewmodel.login(email, password).observe(viewLifecycleOwner) {
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

    private fun observeGoogleLogin(userId: String, firstname: String, username: String, email: String) {
        viewmodel.registerGoogle(userId, firstname, username, email).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressIndicator.show()
                    binding.btnLogin.isEnabled = false
                }
                is Result.Success -> {
                    binding.progressIndicator.hide()
                    toast("Login Google Success")
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

    private fun googleSignIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthGoogle(account.idToken!!)
            } catch (e: ApiException) {
                e.printStackTrace()
                toast(e.localizedMessage)
            }
        }
    }

    private fun firebaseAuthGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener {
                toast("Login Success")
                val isNewUser = it.additionalUserInfo?.isNewUser ?: false

                // Continue with other operations
                navigateToHome(idToken)
                // Save user data in ViewModel
                viewmodel.saveUser(idToken)
                Log.d("Account Google", it.additionalUserInfo.toString())
                Log.d("User Google", it.user?.email.toString())
                Log.d("Credential Google", it.credential?.signInMethod.toString())
                Log.d("User Token Google", idToken)
            }
            .addOnFailureListener {
                toast(it.localizedMessage)
            }
    }
}