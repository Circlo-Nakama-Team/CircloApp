package com.nakama.circlo.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.data.remote.response.IdeasItem
import com.nakama.circlo.data.remote.response.TrashIdeasItem
import com.nakama.circlo.databinding.ItemListProductIdeaBinding
import com.nakama.circlo.databinding.ItemScanResultBinding
import com.nakama.circlo.util.glide

class IdeasItemAdapter: RecyclerView.Adapter<IdeasItemAdapter.ViewHolder>() {

    lateinit var onItemClick: ((IdeasItem) -> Unit)
    inner class ViewHolder(val binding: ItemListProductIdeaBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListProductIdeaBinding.inflate(
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
                tvTitle.text = listItem.ideasName
                tvDesc.text = listItem.ideasDescription
                ivIdeas.glide(listItem.ideasImage.toString())
            }
            holder.itemView.setOnClickListener {
                onItemClick.invoke(listItem)
            }
        }
        Log.d("Adapter list Trash", listItem.toString())
    }

    private val diffUtil: DiffUtil.ItemCallback<IdeasItem> =
        object : DiffUtil.ItemCallback<IdeasItem>() {
            override fun areItemsTheSame(oldItem: IdeasItem, newItem: IdeasItem): Boolean {
                return oldItem.ideasId == newItem.ideasId
            }

            override fun areContentsTheSame(oldItem: IdeasItem, newItem: IdeasItem): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)
}