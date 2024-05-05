package com.nakama.circlo.ui.profile.address

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nakama.circlo.R
import com.nakama.circlo.adapter.AddressUserAdapter
import com.nakama.circlo.data.Result
import com.nakama.circlo.data.remote.response.AddressItem
import com.nakama.circlo.databinding.FragmentAddressBinding
import com.nakama.circlo.ui.SharedViewModel
import com.nakama.circlo.ui.profile.viewmodel.ProfileViewModel
import com.nakama.circlo.utils.hideBottomNavView
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.showAnimationDialog
import com.nakama.circlo.utils.singleton.DataSingleton
import com.nakama.circlo.utils.toast
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class AddressFragment : Fragment() {

    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()
    private val sharedViewModel by viewModels<SharedViewModel>()
    private lateinit var addressUserAdapter: AddressUserAdapter
    private var isPickup = false
    private var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addressUserAdapter = AddressUserAdapter()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = showAnimationDialog()
        isPickup = AddressFragmentArgs.fromBundle(arguments as Bundle).isPickup
        if (isPickup) binding.tvTitleAppbar.text = "Ubah Alamat"
        getDetailProfile("Bearer ${DataSingleton.getInstance().userToken}")
        setupRv()
        setupAction()
    }

    private fun getDetailProfile(token: String) {
        sharedViewModel.getProfile(token).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    loadingDialog?.show()
                }
                is Result.Success -> {
                    loadingDialog?.cancel()
                    val responseData = it.data.data
                    if (responseData?.address?.isEmpty() == true) {
                        binding.ivEmpty.show()
                        return@observe
                    }
                    setupRvAddress(responseData?.address, responseData?.user?.mainAddressId.toString())
                }
                is Result.Error -> {
                    loadingDialog?.cancel()
                    toast(it.error)
                }
            }
        }
    }

    private fun setupRvAddress(listAddress: List<AddressItem>?, mainAddress: String) {
        addressUserAdapter.addAllItem(listAddress!!)
        binding.rvListAddress.adapter = addressUserAdapter
        addressUserAdapter.setMainAddressId(mainAddress)
        addressUserAdapter.setIsPickup(isPickup)
        if (isPickup) {
            addressUserAdapter.onItemClick = {
                val bundle = Bundle()
                bundle.putString("addressId", it)
                findNavController().navigate(R.id.action_addressFragment_to_pickUpFragment, bundle)
            }
        } else {
            addressUserAdapter.onItemClick = {
                findNavController().navigate(AddressFragmentDirections.actionAddressFragmentToDetailAddressFragment(it))
            }
        }

    }

    private fun setupAction() {
        binding.btnAdd.setOnClickListener {
            findNavController().navigate(AddressFragmentDirections.actionAddressFragmentToDetailAddressFragment(""))
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupRv() {
        binding.rvListAddress.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onResume() {
        super.onResume()
    }
}