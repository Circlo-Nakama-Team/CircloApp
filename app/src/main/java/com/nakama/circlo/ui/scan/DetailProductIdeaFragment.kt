package com.nakama.circlo.ui.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.nakama.circlo.R
import com.nakama.circlo.data.remote.response.IdeasItem
import com.nakama.circlo.databinding.FragmentDetailProductIdeaBinding
import com.nakama.circlo.util.glide
import com.nakama.circlo.util.hideBottomNavView

class DetailProductIdeaFragment : Fragment() {

    private var _binding: FragmentDetailProductIdeaBinding? = null
    private val binding get() = _binding!!
    private lateinit var ideasItem: IdeasItem
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentDetailProductIdeaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ideasItem = DetailProductIdeaFragmentArgs.fromBundle(arguments as Bundle).ideasItem

        setupUI()
        setupAction()
    }

    private fun setupUI() {
        binding.apply {
            tvTitle.text = ideasItem.ideasName
            tvDescription.text = ideasItem.ideasDescription
            for (i in 0 until ideasItem.benefits?.size!!) {
                "• ${ideasItem.benefits?.get(i)}\n".also { tvBenefit.append(it) }
            }
            ivIdea.glide(ideasItem.ideasImage!!)
        }
    }

    private fun setupAction() {
        binding.apply {
            btnBack.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}