package com.nakama.circlo.ui.article

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import androidx.navigation.fragment.findNavController
import com.nakama.circlo.R
import com.nakama.circlo.databinding.FragmentArticleDetailBinding
import com.nakama.circlo.databinding.FragmentHomeBinding
import com.nakama.circlo.utils.hideBottomNavView

class ArticleDetailFragment : Fragment() {

    private var _binding: FragmentArticleDetailBinding? = null
    private val binding get() = _binding!!
    lateinit var url: String
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        hideBottomNavView()
        // Inflate the layout for this fragment
        _binding = FragmentArticleDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        url = ArticleDetailFragmentArgs.fromBundle(arguments as Bundle).url
        setupAction()
        setupWebView(url)
    }

    private fun setupAction() {
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView(link: String) {
        val webView = binding.webView
        webView.loadUrl(link)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()
    }
}