package com.nakama.circlo.data.remote.response

import com.google.gson.annotations.SerializedName

data class AddressResponse(

	@field:SerializedName("data")
	val dataAddress: DataAddress? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class AddressDataItem(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("detailAddress")
	val detailAddress: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("addressId")
	val addressId: String? = null
)

data class DataAddress(

	@field:SerializedName("addressData")
	val addressData: List<AddressDataItem?>? = null
)
