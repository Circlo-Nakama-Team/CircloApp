package com.nakama.circlo.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.data.remote.response.TrashIdeasItem
import com.nakama.circlo.databinding.ItemScanResultBinding
import com.nakama.circlo.util.glide

class TrashResultAdapter: RecyclerView.Adapter<TrashResultAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemScanResultBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemScanResultBinding.inflate(
                LayoutInflater.from(parent.context), parent,false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val listItem = differ.currentList[position]
        if (listItem != null) {
            holder.binding.apply {
                tvTrashTitle.text = listItem.trashType
                tvCategory.text = listItem.category
                tvPoint.text = listItem.rewardPoint.toString()
                ivTrash.glide(listItem.image.toString())
            }
        }
        Log.d("Adapter list Trash", listItem.toString())
    }

    private val diffUtil: DiffUtil.ItemCallback<TrashIdeasItem> =
        object : DiffUtil.ItemCallback<TrashIdeasItem>() {
            override fun areItemsTheSame(oldItem: TrashIdeasItem, newItem: TrashIdeasItem): Boolean {
                return oldItem.trashId == newItem.trashId
            }

            override fun areContentsTheSame(oldItem: TrashIdeasItem, newItem: TrashIdeasItem): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)
}