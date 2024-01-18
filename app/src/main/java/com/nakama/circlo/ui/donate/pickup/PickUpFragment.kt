package com.nakama.circlo.ui.donate.pickup

import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.nakama.circlo.databinding.FragmentPickUpBinding
import com.nakama.circlo.ui.donate.DonateViewModel
import com.nakama.circlo.util.hide
import com.nakama.circlo.util.hideBottomNavView
import com.nakama.circlo.util.reduceFileImage
import com.nakama.circlo.util.show
import com.nakama.circlo.util.timeSpinner
import com.nakama.circlo.util.toast
import com.nakama.circlo.util.transformIntoDatePicker
import com.nakama.circlo.util.uriToFile
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
class PickUpFragment : Fragment() {

    private var _binding: FragmentPickUpBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ItemDonateAdapter
    private val viewModel by viewModels<DonateViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentPickUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = ItemDonateAdapter()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                binding.rbOrganik.isChecked = true
            } else {
                binding.rbAnOrganik.isChecked = true
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
        val address = binding.tvMainAddress.text.toString()
        val detailAddress = binding.tvDetailAddress.text.toString()
        val donateMethod = "pickup"
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
                    binding.progressBar.show()
                }

                is Result.Success -> {
                    findNavController().navigate(PickUpFragmentDirections.actionPickUpFragmentToSuccessFragment())
                }

                is Result.Error -> {
                    binding.progressBar.hide()
                    toast(it.error)
                }
            }
        }
    }

    private fun setupAction(token: String) {
        binding.btnDonateNow.setOnClickListener {
            donateNow(token)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}