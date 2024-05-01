package com.nakama.circlo.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.data.remote.response.PostsItem
import com.nakama.circlo.databinding.ItemPostBinding
import com.nakama.circlo.utils.convertDate
import com.nakama.circlo.utils.glide
import com.nakama.circlo.utils.hide

class PostItemAdapter: RecyclerView.Adapter<PostItemAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemPostBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPostBinding.inflate(
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
                ivProfile.glide(listItem.postImage!!)
                tvName.text = listItem.userId
                tvDate.text = convertDate(listItem.postTime!!)
                tvDesc.text = listItem.postBody
                tvLikeCount.text = listItem.postLikes.toString()
                if (listItem.postImage != "") {
                    ivPost.glide(listItem.postImage)
                } else {
                    ivPost.hide()
                }
            }
        }
    }

    private val diffUtil: DiffUtil.ItemCallback<PostsItem> =
        object : DiffUtil.ItemCallback<PostsItem>() {
            override fun areItemsTheSame(oldItem: PostsItem, newItem: PostsItem): Boolean {
                return oldItem.postImage == newItem.postImage
            }

            override fun areContentsTheSame(oldItem: PostsItem, newItem: PostsItem): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)
}