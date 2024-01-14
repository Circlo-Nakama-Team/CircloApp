package com.nakama.circlo.ui.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.nakama.circlo.R
import com.nakama.circlo.databinding.FragmentTrashDetailBinding

class TrashDetailFragment : Fragment() {

    private var _binding: FragmentTrashDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTrashDetailBinding.inflate(inflater, container, false)
        return binding.root
    }



}