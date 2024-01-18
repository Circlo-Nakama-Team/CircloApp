package com.nakama.circlo.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ScanResponse(

	@field:SerializedName("data")
	val data: DataTrash? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

@Parcelize
data class IdeasItem(

	@field:SerializedName("benefits")
	val benefits: List<String?>? = null,

	@field:SerializedName("ideasId")
	val ideasId: String? = null,

	@field:SerializedName("link")
	val link: List<LinkItem?>? = null,

	@field:SerializedName("ideasName")
	val ideasName: String? = null,

	@field:SerializedName("ideasDescription")
	val ideasDescription: String? = null,

	@field:SerializedName("ideasImage")
	val ideasImage: String? = null
) : Parcelable

@Parcelize
data class TrashIdeasItem(

	@field:SerializedName("trashType")
	val trashType: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("rewardPoint")
	val rewardPoint: Int? = null,

	@field:SerializedName("ideas")
	val ideas: List<IdeasItem?>? = null,

	@field:SerializedName("trashId")
	val trashId: String? = null,

	@field:SerializedName("category")
	val category: String? = null
) : Parcelable

@Parcelize
data class LinkItem(

	@field:SerializedName("tutorialLink")
	val tutorialLink: String? = null,

	@field:SerializedName("creator")
	val creator: String? = null,

	@field:SerializedName("linkSource")
	val linkSource: String? = null,

	@field:SerializedName("title")
	val title: String? = null
): Parcelable

@Parcelize
data class DataTrash(

	@field:SerializedName("trashIdeas")
	val trashIdeas: List<TrashIdeasItem>? = null
) : Parcelable
