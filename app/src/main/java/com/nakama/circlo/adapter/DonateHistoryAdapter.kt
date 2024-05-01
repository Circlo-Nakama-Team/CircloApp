package com.nakama.circlo.adapter

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.data.remote.response.DonateData
import com.nakama.circlo.databinding.ItemOrderHistoryBinding
import com.nakama.circlo.utils.convertDate
import com.nakama.circlo.utils.convertTime

class DonateHistoryAdapter: RecyclerView.Adapter<DonateHistoryAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemOrderHistoryBinding): RecyclerView.ViewHolder(binding.root)
    lateinit var onItemClick: ((String) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemOrderHistoryBinding.inflate(
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
            val time = "${convertTime(listItem.donateTime?.startTime!!)} — ${convertTime(listItem.donateTime.startTime)}"
            holder.binding.apply {
                tvDonateMethod.text = listItem.donateMethod
                tvAlamatUser.text = listItem.donateAddress
                tvDate.text = convertDate(listItem.donateDate.toString())
                tvOrderStatus.text = listItem.donateStatus
                tvJenisSampah.text = "Trash type:\n${listItem.trashCategories?.categoryName}"
                tvWaktuPengambilan.text = time
            }
            holder.itemView.setOnClickListener {
                onItemClick.invoke(listItem.donateId.toString())
            }
        }
        Log.d("Adapter list History", listItem.toString())
    }

    private val diffUtil: DiffUtil.ItemCallback<DonateData> =
        object : DiffUtil.ItemCallback<DonateData>() {
            override fun areItemsTheSame(oldItem: DonateData, newItem: DonateData): Boolean {
                return oldItem.donateId == newItem.donateId
            }

            override fun areContentsTheSame(oldItem: DonateData, newItem: DonateData): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, diffUtil)
}