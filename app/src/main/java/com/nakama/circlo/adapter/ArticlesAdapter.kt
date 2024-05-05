package com.nakama.circlo.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.data.remote.response.AddressItem
import com.nakama.circlo.data.remote.response.ArticlesItem
import com.nakama.circlo.databinding.ItemDonateImageBinding
import com.nakama.circlo.databinding.ItemListAddressBinding
import com.nakama.circlo.databinding.ItemListArticleBinding
import com.nakama.circlo.utils.glide
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.show

class ArticlesAdapter: RecyclerView.Adapter<ArticlesAdapter.ViewHolder>() {

    var listArticle: MutableList<ArticlesItem> = mutableListOf()
    private var articleHome = false
    lateinit var onItemClick: ((String) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemListArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        val sizeArticle = if (articleHome) 3 else listArticle.size
        return sizeArticle
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(listArticle[position]) {
                binding.ivArticle.glide(this.image.toString())
                binding.tvTitle.text = this.title
                binding.tvSubhead.text = this.date
                binding.tvDesc.text = this.desc

                itemView.setOnClickListener {
                    onItemClick.invoke(this.link.toString())
                }
            }
        }
    }

    fun setFromHomeSource(isFromHome: Boolean) {
        articleHome = isFromHome
        this.notifyDataSetChanged()
    }

    fun addAllItem(newDataSet: List<ArticlesItem>) {
        listArticle.clear()
        listArticle.addAll(newDataSet)
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemListArticleBinding): RecyclerView.ViewHolder(binding.root)

}