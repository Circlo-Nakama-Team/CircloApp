package com.nakama.circlo.data.remote.response

import com.google.gson.annotations.SerializedName

data class UserResponse(

	@field:SerializedName("data")
	val data: DataUser? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class User(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("firstname")
	val firstname: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("email")
	val email: String? = null,

	@field:SerializedName("point")
	val point: Int? = null,

	@field:SerializedName("main_addressId")
	val mainAddressId: String? = null,

	@field:SerializedName("lastname")
	val lastname: String? = null,

	@field:SerializedName("username")
	val username: String? = null
)

data class DataUser(

	@field:SerializedName("postHistory")
	val postHistory: List<PostHistoryItem>? = null,

	@field:SerializedName("address")
	val address: List<AddressItem>? = null,

	@field:SerializedName("user")
	val user: User? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("addressId")
	val addressId: String? = null
)

data class AddressItem(

	@field:SerializedName("address")
	val address: String? = null,

	@field:SerializedName("detailAddress")
	val detailAddress: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("addressId")
	val addressId: String? = null,

	@field:SerializedName("title")
	val titleAddress: String? = null
)

data class PostHistoryItem(

	@field:SerializedName("postTime")
	val postTime: String? = null,

	@field:SerializedName("postImage")
	val postImage: String? = null,

	@field:SerializedName("postBody")
	val postBody: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("postLikes")
	val postLikes: Int? = null
)
