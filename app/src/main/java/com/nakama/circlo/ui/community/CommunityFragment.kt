package com.nakama.circlo.ui.community

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nakama.circlo.adapter.PostItemAdapter
import com.nakama.circlo.data.Result
import com.nakama.circlo.data.remote.response.PostsItem
import com.nakama.circlo.databinding.FragmentCommunityBinding
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.showBottomNavView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PostItemAdapter
    private val viewModel by viewModels<CommunityViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        showBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentCommunityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = PostItemAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        validateUserToken()
    }

    private fun validateUserToken() {
        viewModel.getToken().observe(viewLifecycleOwner) { token ->
            setupView(token)
        }
    }

    private fun setupRv() {
        binding.rvPost.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observe() {
        viewModel.getCommunity().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    setUpRvPost(result.data.data?.posts!!)
                    Log.d("Get Community", result.data.data.posts.toString())
                }
                is Result.Error -> {
                    binding.progressBar.hide()
                    if (result.error.trimEnd() == "HTTP 404") {
                        binding.ivEmpty.show()
                    }
                }
            }
        }
    }

    private fun setUpRvPost(data: List<PostsItem>) {
        binding.rvPost.adapter = adapter
        adapter.differ.submitList(data)
    }

    private fun setupView(token: String) {
        if (token != "") {
            binding.fabAddPost.show()
            binding.fabAddPost.setOnClickListener {
                val action = CommunityFragmentDirections.actionCommunityFragmentToAddPostFragment()
                findNavController().navigate(action)
            }
        } else {
            binding.fabAddPost.hide()
        }
        observe()
    }



    override fun onResume() {
        super.onResume()
        validateUserToken()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}