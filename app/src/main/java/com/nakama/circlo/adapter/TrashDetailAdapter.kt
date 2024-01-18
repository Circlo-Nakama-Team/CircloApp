package com.nakama.circlo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.data.remote.response.TrashIdeasItem
import com.nakama.circlo.databinding.ItemTrashPagerBinding
import com.nakama.circlo.util.glide

class TrashDetailPager: RecyclerView.Adapter<TrashDetailPager.ViewHolder>() {

    private var trashItem: MutableList<TrashIdeasItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemTrashPagerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return trashItem.size
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(trashItem[position]) {
                binding.ivTrash.glide(this.image.toString())
                binding.tvTrashTitle.text = this.trashType
            }
        }
    }

    fun setNewItem(newDataSet: List<TrashIdeasItem>) {
        trashItem.clear()
        trashItem.addAll(newDataSet)
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemTrashPagerBinding): RecyclerView.ViewHolder(binding.root)

}