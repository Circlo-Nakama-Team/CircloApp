package com.nakama.circlo.data.remote.response

import com.google.gson.annotations.SerializedName

data class AuthResponse(

	@field:SerializedName("data")
	val data: Data? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class Data(

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("credential")
	val credential: String? = null,

	@field:SerializedName("refreshToken")
	val refreshToken: String? = null,
)
