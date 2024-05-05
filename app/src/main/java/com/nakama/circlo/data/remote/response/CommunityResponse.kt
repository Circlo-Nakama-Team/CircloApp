package com.nakama.circlo.data.remote.response

import com.google.gson.annotations.SerializedName

data class CommunityResponse(

	@field:SerializedName("data")
	val data: DataPostCommunity? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DataPostCommunity(

	@field:SerializedName("posts")
	val posts: List<PostsItem>? = null
)

data class UserPost(
	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("username")
	val username: String? = null,

	@field:SerializedName("image")
	val image: String? = null
)

data class PostsItem(

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("user")
	val user: UserPost? = null,

	@field:SerializedName("postTime")
	val postTime: String? = null,

	@field:SerializedName("postLikes")
	val postLikes: Int? = null,

	@field:SerializedName("postBody")
	val postBody: String? = null,

	@field:SerializedName("postImage")
	val postImage: String? = null
)
