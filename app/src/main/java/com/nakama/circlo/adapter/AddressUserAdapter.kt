package com.nakama.circlo.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nakama.circlo.data.remote.response.AddressItem
import com.nakama.circlo.databinding.ItemDonateImageBinding
import com.nakama.circlo.databinding.ItemListAddressBinding
import com.nakama.circlo.utils.glide
import com.nakama.circlo.utils.hide
import com.nakama.circlo.utils.show

class AddressUserAdapter: RecyclerView.Adapter<AddressUserAdapter.ViewHolder>() {

    var listAddress: MutableList<AddressItem> = mutableListOf()
    private var mainAddressId: String = ""
    private var isPickup = false
    lateinit var onItemClick: ((String) -> Unit)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemListAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount(): Int {
        return listAddress.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        with(holder) {
            with(listAddress[position]) {
                binding.tvTitleAddress.text = this.titleAddress
                binding.tvAlamatUser.text = this.address

                if (mainAddressId == this.addressId) {
                    binding.cardMainAddress.show()
                }

                if (isPickup) {
                    binding.radioBtn.show()
                    binding.radioBtn.setOnClickListener {
                        onItemClick.invoke(this.addressId.toString())
                    }
                } else {
                    itemView.setOnClickListener {
                        onItemClick.invoke(this.addressId.toString())
                    }
                }
            }
        }
    }

    fun setMainAddressId(mainAddress: String) {
        mainAddressId = mainAddress
        this.notifyDataSetChanged()
    }

    fun setIsPickup(isPickup: Boolean) {
        this.isPickup = isPickup
    }

    fun addAllItem(newDataSet: List<AddressItem>) {
        listAddress.clear()
        listAddress.addAll(newDataSet)
        this.notifyDataSetChanged()
    }

    fun removeItem(newDataSet: AddressItem) {
        listAddress.remove(newDataSet)
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemListAddressBinding): RecyclerView.ViewHolder(binding.root)

}