package com.nakama.circlo.ui.donate.dropoff

import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nakama.circlo.adapter.ItemDonateAdapter
import com.nakama.circlo.data.Result
import com.nakama.circlo.databinding.FragmentDropOffBinding
import com.nakama.circlo.ui.donate.DonateViewModel
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.hideBottomNavView
import com.nakama.circlo.utils.reduceFileImage
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.showAnimationDialog
import com.nakama.circlo.utils.timeSpinner
import com.nakama.circlo.utils.toast
import com.nakama.circlo.utils.transformIntoDatePicker
import com.nakama.circlo.utils.uriToFile
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class DropOffFragment : Fragment() {

    private var _binding: FragmentDropOffBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ItemDonateAdapter
    private val viewModel by viewModels<DonateViewModel>()
    private var loadingDialog: Dialog? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentDropOffBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ItemDonateAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = showAnimationDialog()
        setupRv()

        // Get bundle from previous fragment
        val imageUri = arguments?.getString("imageUri")
        val category = arguments?.getString("category")
        val trashTitle = arguments?.getStringArrayList("trashTitle")
        val trashTitleString = trashTitle?.joinToString(", ")

        if (imageUri != null || category != null) {
            val uri = Uri.parse(imageUri)
            setupRvTDonateImage()
            adapter.addItem(uri)

            binding.edDropoffDescription.setText(trashTitleString)
            // If Category is "Organic" then radiobutton will be checked at rbPlastic radioButton
            if (category == "Organik") {
                binding.trashcat1.isChecked = true
            } else {
                binding.trashcat2.isChecked = true
            }
        }

        binding.edPickupDate.transformIntoDatePicker(requireContext(), "yyyy-MM-dd")
        binding.tvPickupTime.timeSpinner(requireContext())

        binding.cvAddPhoto.setOnClickListener {
            PickImageDialog.build(PickSetup())
                .setOnPickResult {
                    if (it.error == null) {
                        setupRvTDonateImage()
                        adapter.addItem(it.uri)
                    } else {
                        toast(it.error.message.toString())
                    }
                }

                .setOnPickCancel {
                    toast("Canceled")
                }.show(childFragmentManager)
        }
        validateUserToken()
    }

    private fun validateUserToken() {
        viewModel.getToken().observe(viewLifecycleOwner) { token ->
            if (token != "") {
                setupAction(token)
                Log.d("Token", token)
            } else {
                toast("Please Login First")
            }
        }
    }

    private fun setupRv() {
        binding.rvDonateImage.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }
    private fun setupRvTDonateImage() {
        binding.rvDonateImage.adapter = adapter
        adapter.onItemClick = {
            adapter.removeItem(it)
        }
    }

    private fun getValueOfRadioButton() : String {
        val radioGroup = binding.chooseTrashType
        val selectedId = radioGroup.checkedRadioButtonId
        val radioButton = requireView().findViewById<RadioButton>(selectedId)
        return radioButton.id.toString()
    }

    private fun donateNow(token: String) {
        val trashCategoriesId = "trashcat-1"
        val address = binding.tvTitleDropPoint.text.toString()
        val detailAddress = binding.tvMainAddress.text.toString()
        val donateMethod = "self-service"
        val donateDescription = binding.edDropoffDescription.text.toString()
        val donateDate = binding.edPickupDate.text.toString()
        val donateSchedule = when (binding.tvPickupTime.text.toString()) {
            "08:00 — 12:00" -> "sch_1"
            "12:00 — 16:00" -> "sch_2"
            "16:00 — 18:00" -> "sch_3"
            else -> ""
        }

        val requestCategoriesId = trashCategoriesId.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestAddress = address.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestDetailAddress = detailAddress.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestDonateMethod = donateMethod.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestDonateDescription = donateDescription.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestDonateDate = donateDate.toRequestBody("text/plain".toMediaTypeOrNull())
        val requestDonateSchedule = donateSchedule.toRequestBody("text/plain".toMediaTypeOrNull())

        val listImage = adapter.getItems()

        val imageParts = listImage.mapIndexed { index, image ->
            val imageItem = uriToFile(image, requireContext()).reduceFileImage()
            val requestBody = imageItem.asRequestBody("image/jpeg".toMediaType())
            MultipartBody.Part.createFormData("image", "image_$index.jpg", requestBody)
        }

        observeDonateTrash(
            "Bearer $token",
            requestCategoriesId,
            requestAddress,
            requestDetailAddress,
            requestDonateMethod,
            requestDonateDescription,
            requestDonateDate,
            requestDonateSchedule,
            imageParts
        )
    }

    private fun observeDonateTrash(
        token: String,
        trashCategoriesId: RequestBody,
        address: RequestBody,
        detailAddress: RequestBody,
        donateMethod: RequestBody,
        donateDescription: RequestBody,
        donateDate: RequestBody,
        donateSchedule: RequestBody,
        image: List<MultipartBody.Part>
    ) {
        viewModel.trashDonate(
            token,
            trashCategoriesId,
            address,
            detailAddress,
            donateMethod,
            donateDescription,
            donateDate,
            donateSchedule,
            image
        ).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.btnDonateNow.isEnabled = false
                    loadingDialog?.show()
                }

                is Result.Success -> {
                    loadingDialog?.cancel()
                    Log.d("Donate Success", it.data.toString())
                    findNavController().navigate(DropOffFragmentDirections.actionDropOffFragmentToSuccessFragment())
                }

                is Result.Error -> {
                    loadingDialog?.cancel()
                    loadingDialog?.cancel()
                    toast(it.error)
                    Log.d("Donate", it.error)
                }
            }
        }
    }

    private fun setupAction(token: String) {
        binding.btnDonateNow.setOnClickListener {
            donateNow(token)
        }
        binding.btnChangeLocation.setOnClickListener {
            toast("Maaf untuk saat ini drop point terbatas hanya 1 lokasi")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}