package com.nakama.circlo.ui.scan

import android.net.Uri
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
import com.nakama.circlo.adapter.TrashResultAdapter
import com.nakama.circlo.data.Result
import com.nakama.circlo.data.remote.response.DataTrash
import com.nakama.circlo.databinding.FragmentResultBinding
import com.nakama.circlo.ui.scan.viewmodel.ScanViewModel
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.hideBottomNavView
import com.nakama.circlo.utils.reduceFileImage
import com.nakama.circlo.utils.setupConfirmDonateDialog
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.toast
import com.nakama.circlo.utils.uriToFile
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody

@AndroidEntryPoint
class ResultFragment : Fragment() {

    private var _binding: FragmentResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUri: Uri
    private lateinit var trashAdapter: TrashResultAdapter

    private val viewModel by viewModels<ScanViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        trashAdapter = TrashResultAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get data Bundle from previous fragment
        val imageUriString = arguments?.getString("imageUri")
        imageUri = Uri.parse(imageUriString)

        setupRv()
        getUserToken()
    }

    private fun getUserToken() {
        viewModel.getUser().observe(viewLifecycleOwner) { token ->
            if (token != "") {
                scanTrash(token)
            }
        }
    }

    private fun setupRv() {
        binding.rvTrashResult.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }
    private fun setupRvTrash() {
        binding.rvTrashResult.adapter = trashAdapter
    }

    private fun scanTrash(token: String) {
        Log.d("Image Uri", imageUri.toString())
        imageUri.let { uri ->  
            val image = uriToFile(uri, requireContext()).reduceFileImage()
            val requestImageFile = image.asRequestBody("image/jpg".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData(
                "image",
                image.name,
                requestImageFile
            )
            observeScanResult("Bearer $token", multipartBody)
        }
    }
    private fun observeScanResult(token: String, image: MultipartBody.Part) {
        viewModel.scanTrash(token, image).observe(viewLifecycleOwner) {
            when (it) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    trashAdapter.differ.submitList(it.data.data?.trashIdeas)
                    setupRvTrash()
                    setupAction(it.data.data!!)
                }
                is Result.Error -> {
                    binding.progressBar.hide()
                    Log.e("Scan Trash", it.error)
                    toast(it.error)
                }
            }
        }
    }

    private fun setupAction(data: DataTrash) {
        binding.apply {
            btnDonation.isEnabled = true
            btnRecycle.isEnabled = true
            btnDonation.setOnClickListener {
                val listOfCategory = data.trashIdeas?.map { it.category }
                val listOfTitle = data.trashIdeas?.map { it.trashType }
                val allCategoriesItem = listOfCategory?.all { it == listOfCategory[0] } ?: false
                if (allCategoriesItem) {
                    val bundle = Bundle()
                    bundle.putString("imageUri", imageUri.toString())
                    bundle.putString("category", listOfCategory!![0].toString())
                    bundle.putStringArrayList("trashTitle", ArrayList(listOfTitle!!))
                    setupConfirmDonateDialog(
                        onDonateClick = {
                            findNavController().navigate(R.id.action_resultFragment_to_dropOffFragment, bundle)
                        },
                        onPickupClick = {
                            findNavController().navigate(R.id.action_resultFragment_to_pickUpFragment, bundle)
                        }
                    )
                } else {
                    toast("Sorry, your trash must be in the same category")
                }
            }
            btnRecycle.setOnClickListener {
                findNavController().navigate(ResultFragmentDirections.actionResultFragmentToTrashDetailFragment(data))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}