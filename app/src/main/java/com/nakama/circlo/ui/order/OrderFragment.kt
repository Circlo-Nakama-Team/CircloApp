package com.nakama.circlo.ui.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.nakama.circlo.adapter.DonateHistoryAdapter
import com.nakama.circlo.data.Result
import com.nakama.circlo.databinding.FragmentOrderBinding
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.showBottomNavView
import com.nakama.circlo.utils.singleton.DataSingleton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OrderFragment : Fragment() {

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<OrderViewModel>()
    private lateinit var donateHistoryAdapter: DonateHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        donateHistoryAdapter = DonateHistoryAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        showBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentOrderBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        getUserToken()
    }

    private fun getUserToken() {
        DataSingleton.getInstance().userToken.apply {
            if (!this.isNullOrEmpty()) {
                observeGetDonate("Bearer $this")
            }
        }
    }

    private fun observeGetDonate(token: String) {
        viewModel.getDonateHistory(token).observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    if (it.data.data?.donateDatas.isNullOrEmpty()) {
                        binding.ivEmpty.show()
                        return@observe
                    }
                    donateHistoryAdapter.differ.submitList(it.data.data?.donateDatas)
                    setupRvOrderHistory()
                }
                is Result.Error -> {
                    binding.progressBar.hide()
                    Log.d("Error History DOnate", it.error)
                }
            }
        }
    }

    private fun setupRvOrderHistory() {
        binding.rvOderHistory.adapter = donateHistoryAdapter
        donateHistoryAdapter.onItemClick = {
            findNavController().navigate(OrderFragmentDirections.actionOrderFragmentToDetailDonateFragment2(it))
        }
    }

    private fun setupRv() {
        binding.rvOderHistory.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

}