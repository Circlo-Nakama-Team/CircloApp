package com.nakama.circlo.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.databinding.ItemDonateImageBinding
import com.nakama.circlo.util.glide

class ItemDonateAdapter: RecyclerView.Adapter<ItemDonateAdapter.OnBoardingViewHolder>() {

    var imageItems: MutableList<Uri> = mutableListOf()
    lateinit var onItemClick: ((Uri) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OnBoardingViewHolder {
        return OnBoardingViewHolder(ItemDonateImageBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageItems.size
    }

    override fun onBindViewHolder(holder: OnBoardingViewHolder, position: Int) {
        with(holder) {
            with(imageItems[position]) {
                binding.imageItem.glide(this.toString())
                binding.ivDelete1.setOnClickListener {
                    onItemClick.invoke(this)
                }
            }
        }
    }

    fun getItems(): MutableList<Uri> {
        return imageItems
    }

    fun addItem(newDataSet: Uri) {
        imageItems.add(newDataSet)
        this.notifyDataSetChanged()
    }

    fun removeItem(newDataSet: Uri) {
        imageItems.remove(newDataSet)
        this.notifyDataSetChanged()
    }

    inner class OnBoardingViewHolder(val binding: ItemDonateImageBinding): RecyclerView.ViewHolder(binding.root)

}