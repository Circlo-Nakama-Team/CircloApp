package com.nakama.circlo.ui.community

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nakama.circlo.adapter.PostItemAdapter
import com.nakama.circlo.data.Result
import com.nakama.circlo.databinding.FragmentAddPostBinding
import com.nakama.circlo.utils.confirmDialog
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.hideBottomNavView
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.showAnimationDialog
import com.nakama.circlo.utils.toast
import com.nakama.circlo.utils.uriToFile
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import dagger.hilt.android.AndroidEntryPoint
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody

@AndroidEntryPoint
class AddPostFragment : Fragment() {

    private var _binding: FragmentAddPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PostItemAdapter
    private val viewModel by viewModels<CommunityViewModel>()
    private var imageUri: Uri? = null
    private var loadingDialog: Dialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentAddPostBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = PostItemAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = showAnimationDialog()
        validateUserToken()
    }

    private fun validateUserToken() {
        viewModel.getToken().observe(viewLifecycleOwner) { token ->
            setupAction(token)
        }
    }

    private fun addPost(token: String) {
        val caption = binding.edPostDescription.text.toString()

        val requestCaption = caption.toRequestBody("text/plain".toMediaTypeOrNull())
        val image = uriToFile(imageUri!!, requireContext())
        val requestImage = image.asRequestBody("image/jpeg".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", image.name, requestImage)

        observe("Bearer $token", requestCaption, imagePart)
    }
    private fun setupAction(token: String) {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.cvAddPhoto.setOnClickListener {
            PickImageDialog.build(PickSetup())
                .setOnPickResult {
                    if (it.error == null) {
                        imageUri = it.uri
                        binding.ivPreview.hide()
                        binding.ivAddPhoto.setImageURI(it.uri)
                        binding.ivAddPhoto.show()
                    } else {
                        toast(it.error.message.toString())
                    }
                }

                .setOnPickCancel {
                    toast("Canceled")
                }.show(childFragmentManager)
        }
        binding.btnPostNow.setOnClickListener {
            addPost(token)
        }
    }

    private fun observe(
        token: String,
        caption: RequestBody,
        image: MultipartBody.Part,
    ) {
        viewModel.addPost(token, caption, image).observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    loadingDialog?.show()
                }
                is Result.Success -> {
                    loadingDialog?.cancel()
                    confirmDialog(
                        requireContext(),
                        "Post added successfully",
                        "Back to community",
                        "OK",
                        ""
                    ) {
                        findNavController().navigateUp()
                    }
                }
                is Result.Error -> {
                    loadingDialog?.cancel()
                    binding.progressBar.hide()
                    toast(result.error)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}