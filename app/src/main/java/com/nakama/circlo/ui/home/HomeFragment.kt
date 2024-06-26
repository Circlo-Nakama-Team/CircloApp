package com.nakama.circlo.ui.home

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.nakama.circlo.R
import com.nakama.circlo.adapter.ArticlesAdapter
import com.nakama.circlo.data.Result
import com.nakama.circlo.data.remote.response.ArticlesItem
import com.nakama.circlo.databinding.FragmentHomeBinding
import com.nakama.circlo.ui.SharedViewModel
import com.nakama.circlo.utils.confirmDialog
import com.nakama.circlo.utils.glide
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.setupConfirmDonateDialog
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.showAnimationDialog
import com.nakama.circlo.utils.showBottomNavView
import com.nakama.circlo.utils.toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<HomeViewModel>()
    private val sharedViewModel by viewModels<SharedViewModel>()
    private lateinit var articlesAdapter: ArticlesAdapter
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        articlesAdapter = ArticlesAdapter()
    }

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

        loadingDialog = showAnimationDialog()
        getUserToken()
        askNotificationPermission()
        viewLifecycleOwner.lifecycleScope.launch {
            getFcmToken()
        }
        setupRv()
        getArticles()
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
                val param = icPoint.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(36, 120, 0,0)
                icPoint.layoutParams = param
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
            val param = icPoint.layoutParams as ViewGroup.MarginLayoutParams
            param.setMargins(36, 200, 0,0)
            icPoint.layoutParams = param
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
            btnTopUp.setOnClickListener {
                setupConfirmDonateDialog(
                    onDonateClick = {
                        val action = HomeFragmentDirections.actionHomeFragmentToDropOffFragment()
                        findNavController().navigate(action)
                    },
                    onPickupClick = {
                        val action = HomeFragmentDirections.actionHomeFragmentToPickUpFragment()
                        findNavController().navigate(action)
                    }
                )
            }
            btnRedeem.setOnClickListener {
                toast(getString(R.string.info_dev_mode))
            }
            btnNotif.setOnClickListener {
                toast(getString(R.string.info_dev_mode))
            }
            cvExplore.setOnClickListener {
                toast(getString(R.string.info_dev_mode))
            }
        }
    }

    private fun setupRv() {
        binding.rvListArticle.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupRvArticles(listArticle: List<ArticlesItem>) {
        binding.rvListArticle.adapter = articlesAdapter
        articlesAdapter.addAllItem(listArticle)
        articlesAdapter.setFromHomeSource(true)
        articlesAdapter.onItemClick = {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToArticleDetailFragment(it))
        }
    }

    private fun getArticles() {
        sharedViewModel.getArticles().observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    loadingDialog?.show()
                }
                is Result.Success -> {
                    loadingDialog?.cancel()
                    val responseData = it.data.data
                    setupRvArticles(responseData?.articles!!)
                }
                is Result.Error -> {
                    Log.d("Get Articles", it.error)
                }
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

    private suspend fun getFcmToken() {
        val token = Firebase.messaging.token.await()
        Log.d("FCM Token", token)
    }

    // Declare the launcher at the top of your Activity/Fragment:
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
            toast("Notification is enabled")
            viewLifecycleOwner.lifecycleScope.launch {
                getFcmToken()
            }
        } else {
            toast("Notification is disabled")
        }
    }

    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        loadingDialog?.cancel()
    }
}