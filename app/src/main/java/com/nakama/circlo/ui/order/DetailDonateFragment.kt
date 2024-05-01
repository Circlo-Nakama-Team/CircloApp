package com.nakama.circlo.ui.order

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.nakama.circlo.R
import com.nakama.circlo.adapter.ImageDetailDonateAdapter
import com.nakama.circlo.data.Result
import com.nakama.circlo.data.remote.response.DonateData
import com.nakama.circlo.databinding.FragmentDetailDonateBinding
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.hideBottomNavView
import com.nakama.circlo.utils.show
import com.nakama.circlo.utils.singleton.DataSingleton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailDonateFragment : Fragment() {

    private var _binding: FragmentDetailDonateBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModels<OrderViewModel>()
    private lateinit var itemDonateAdapter: ImageDetailDonateAdapter
    private lateinit var donateId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        itemDonateAdapter = ImageDetailDonateAdapter()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentDetailDonateBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRv()
        donateId = DetailDonateFragmentArgs.fromBundle(arguments as Bundle).donateId
        getUserToken()
    }

    private fun getUserToken() {
        DataSingleton.getInstance().userToken.apply {
            if (!this.isNullOrEmpty()) {
                observeGetDonateDetail("Bearer $this", donateId)
            }
        }
    }

    private fun observeGetDonateDetail(token: String, donateId: String) {
        viewModel.getDetailDonate(token, donateId).observe(viewLifecycleOwner) {
            when(it) {
                is Result.Loading -> {
                    binding.progressBar.show()
                }
                is Result.Success -> {
                    binding.progressBar.hide()
                    val response = it.data.data?.donateItem ?: return@observe
                    setUpAction(response)
                    setupRvOrderHistory()
                    itemDonateAdapter.addItems(response.image!!)
                }
                is Result.Error -> {
                    binding.progressBar.hide()
                    Log.d("Error History DOnate", it.error)
                }
            }
        }
    }

    private fun setUpAction(item: DonateData) {
        binding.apply {
            tvStatusLarge.text = item.donateStatus
            tvTypeDonation.text = item.donateMethod
            tvIdDonasi.text = item.donateId
            tvStatus.text = item.donateStatus
            tvDesc.text = item.donateDescription
            tvPotentialPoint.text = "+ ${item.donatePoint}"

            expandButton.setOnClickListener {
                if (detailDonation.visibility == View.VISIBLE) {
                    detailDonation.hide()
                    expandButton.setImageResource(R.drawable.expand_more)
                    TransitionManager.beginDelayedTransition(containerDetail, AutoTransition())
                } else {
                    detailDonation.show()
                    expandButton.setImageResource(R.drawable.expand_less)
                    TransitionManager.beginDelayedTransition(containerDetail, AutoTransition())
                }
            }
        }
    }

    private fun setupRvOrderHistory() {
        binding.rvDonateImage.adapter = itemDonateAdapter
    }

    private fun setupRv() {
        binding.rvDonateImage.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

}