package com.nakama.circlo.ui.scan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.nakama.circlo.R
import com.nakama.circlo.adapter.IdeasItemAdapter
import com.nakama.circlo.adapter.TrashDetailPager
import com.nakama.circlo.data.remote.response.DataTrash
import com.nakama.circlo.data.remote.response.TrashIdeasItem
import com.nakama.circlo.databinding.FragmentTrashDetailBinding
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.invisible
import com.nakama.circlo.utils.show

class TrashDetailFragment : Fragment() {

    private var _binding: FragmentTrashDetailBinding? = null
    private val binding get() = _binding!!
    private lateinit var trashDetailAdapter: TrashDetailPager
    private lateinit var trashViewPager: ViewPager2
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var layoutOnBoardingIndicators: LinearLayout
    private lateinit var ideasItemAdapter: IdeasItemAdapter
    private lateinit var dataTrash: DataTrash

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentTrashDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ideasItemAdapter = IdeasItemAdapter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        trashViewPager = binding.trashViewPager
        btnNext = binding.btnNext
        btnPrev = binding.btnPrev
        layoutOnBoardingIndicators = binding.dots

        setupTrashResultItem()
        trashViewPager.adapter = trashDetailAdapter
        setupTrashResultItem()
        setUpOnBoardingIndicators()
        setCurrentPage(0)

        trashViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentPage(position)
            }
        })

        setupRv()
        setupAction()
    }

    private fun setupAction() {
        binding.btnNext.setOnClickListener {
            trashViewPager.currentItem = trashViewPager.currentItem + 1
        }
        binding.btnPrev.setOnClickListener {
            trashViewPager.currentItem = trashViewPager.currentItem - 1
        }
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setupTrashResultItem() {
        dataTrash = TrashDetailFragmentArgs.fromBundle(arguments as Bundle).trashResult
        trashDetailAdapter = TrashDetailPager()
        trashDetailAdapter.setNewItem(dataTrash.trashIdeas!!)
    }

    private fun setUpOnBoardingIndicators() {
        val indicators = arrayOfNulls<ImageView>(trashDetailAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        layoutParams.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(requireContext())
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParams
            }
            layoutOnBoardingIndicators.addView(indicators[i])
        }
    }

    private fun setCurrentPage(position: Int) {
        // Check trash data is not null
        val childCount = layoutOnBoardingIndicators.childCount
        for (i in 0 until childCount) {
            val imageView = layoutOnBoardingIndicators[i] as ImageView

            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
        if (trashDetailAdapter.itemCount == 1) {
            btnNext.invisible()
            btnPrev.invisible()
        } else {
            when (position) {
                0 -> {
                    btnPrev.invisible()
                    btnNext.show()
                }
                trashDetailAdapter.itemCount - 1 -> {
                    btnPrev.show()
                    btnNext.invisible()
                }
                else -> {
                    btnPrev.show()
                    btnNext.show()
                }
            }
        }
        setupRvProductIdeas(dataTrash.trashIdeas!![position])
    }

    private fun setupRv() {
        binding.rvProductIdeas.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupRvProductIdeas(trashIdea: TrashIdeasItem) {
        binding.rvProductIdeas.adapter = ideasItemAdapter
        ideasItemAdapter.differ.submitList(trashIdea.ideas)
        ideasItemAdapter.onItemClick = { ideasItem ->
            findNavController().navigate(TrashDetailFragmentDirections.actionTrashDetailFragmentToDetailProductIdeaFragment(ideasItem))
        }
    }
}