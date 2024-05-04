package com.nakama.circlo.ui.profile.address

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nakama.circlo.R
import com.nakama.circlo.data.Result
import com.nakama.circlo.databinding.FragmentAddressBinding
import com.nakama.circlo.databinding.FragmentDetailAddressBinding
import com.nakama.circlo.ui.SharedViewModel
import com.nakama.circlo.ui.donate.pickup.PickUpFragmentDirections
import com.nakama.circlo.ui.profile.viewmodel.ProfileViewModel
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.hideBottomNavView
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.singleton.DataSingleton
import com.nakama.circlo.utils.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailAddressFragment : Fragment() {

    private var _binding: FragmentDetailAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<ProfileViewModel>()
    private val sharedViewModel by viewModels<SharedViewModel>()
    private var isNewAddress: Boolean = false
    lateinit var addressId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentDetailAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addressId = DetailAddressFragmentArgs.fromBundle(arguments as Bundle).addressId
        validateBtnSave()
        getDetailProfile("Bearer ${DataSingleton.getInstance().userToken}")
    }

    private fun getDetailProfile(token: String) {
        sharedViewModel.getProfile(token).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val responseData = it.data.data
                    isNewAddress = responseData?.address.isNullOrEmpty()
                    setupUI(token, responseData?.user?.mainAddressId.toString())
                }
                is Result.Error -> {
                    Log.d("Get Profile", it.error)
                }
            }
        }
    }

    private fun setupUI(token: String, mainAddressId: String) {
        if (addressId != "") {
            binding.tvTitleAppbar.text = "Edit Address"
            getAddressDetail(token, addressId, mainAddressId)
            setupAction(token, false)
        } else {
            binding.tvTitleAppbar.text = "Add Address"
            binding.swMainAddress.isChecked = isNewAddress
            setupAction(token, true)
        }
    }

    private fun validateBtnSave() {
        val detailAddressWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val addresDetailText = s.toString()

                binding.btnSave.isEnabled = addresDetailText.isNotEmpty()
            }
        }

        binding.edAddressDetail.addTextChangedListener(detailAddressWatcher)
    }


    private fun setupAction(token: String, isAddAddress: Boolean) {

        binding.btnSave.setOnClickListener {
            val titleAddress = binding.edAddressTitle.text.toString()
            val fullAddress = binding.edFullAddress.text.toString()
            val addressDetail = binding.edAddressDetail.text.toString()

            if (isAddAddress) {
                addAddress(token, fullAddress, addressDetail, titleAddress)
            } else {
                editAddress(token, fullAddress, addressDetail, titleAddress, addressId)
            }
        }

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    private fun updateMainAddress(token: String, addressId: String) {
        viewModel.updateMainAddress(token, addressId).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.progressBar.show()
                }

                is Result.Success -> {
                    findNavController().navigateUp()
                }

                is Result.Error -> {
                    binding.progressBar.hide()
                    binding.btnSave.isEnabled = true
                    Log.d("Update Main Address", it.error)
                    toast(it.error)
                }
            }
        }
    }
    private fun addAddress(
        token: String,
        address: String,
        addressDetail: String,
        addressTitle: String
    ) {
        viewModel.addAddress(token, address, addressDetail, addressTitle).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.progressBar.show()
                }

                is Result.Success -> {
                    if (binding.swMainAddress.isChecked) {
                        updateMainAddress(token, it.data.data?.addressId.toString())
                    } else findNavController().navigateUp()
                }

                is Result.Error -> {
                    binding.progressBar.hide()
                    binding.btnSave.isEnabled = true
                    toast(it.error)
                }
            }
        }
    }

    private fun editAddress(
        token: String,
        address: String,
        addressDetail: String,
        addressTitle: String,
        addressId: String
    ) {
        viewModel.editAddress(token, address, addressDetail, addressTitle, addressId).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.btnSave.isEnabled = false
                    binding.progressBar.show()
                }

                is Result.Success -> {
                    if (binding.swMainAddress.isChecked) {
                        updateMainAddress(token, addressId)
                    } else findNavController().navigateUp()
                }

                is Result.Error -> {
                    binding.progressBar.hide()
                    binding.btnSave.isEnabled = true
                    toast(it.error)
                }
            }
        }
    }


    private fun getAddressDetail(token: String, addressId: String, mainAddressId: String) {
        sharedViewModel.getDetailAddress(token, addressId).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {

                }
                is Result.Success -> {
                    val responseData = it.data.dataAddress?.addressData?.first()
                    binding.apply {
                        edAddressTitle.setText(responseData?.title)
                        edFullAddress.setText(responseData?.address)
                        edAddressDetail.setText(responseData?.detailAddress)
                    }
                    if (addressId == mainAddressId) binding.swMainAddress.isChecked = true
                }
                is Result.Error -> {
                    Log.d("Get Address", it.error)
                }
            }
        }
    }

}