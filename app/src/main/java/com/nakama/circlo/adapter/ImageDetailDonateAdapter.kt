package com.nakama.circlo.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.databinding.ItemDonateImageBinding
import com.nakama.circlo.utils.glide

class ImageDetailDonateAdapter: RecyclerView.Adapter<ImageDetailDonateAdapter.DetailDonateViewHolder>() {

    var imageItems: MutableList<String> = mutableListOf()
    lateinit var onItemClick: ((Uri) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailDonateViewHolder {
        return DetailDonateViewHolder(ItemDonateImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }

    override fun onBindViewHolder(holder: DetailDonateViewHolder, position: Int) {
        with(holder) {
            with(imageItems[position]) {
                binding.imageItem.glide(this@with)
                Log.d("Image url", this@with)
                binding.cvDelete1.visibility = View.GONE
            }
        }
    }

    fun addItems(newDataSet: List<String>) {
        imageItems.addAll(newDataSet)
        this.notifyDataSetChanged()
    }


    inner class DetailDonateViewHolder(val binding: ItemDonateImageBinding): RecyclerView.ViewHolder(binding.root)

}