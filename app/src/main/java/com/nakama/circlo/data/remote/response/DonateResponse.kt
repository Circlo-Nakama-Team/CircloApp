package com.nakama.circlo.data.remote.response

import com.google.gson.annotations.SerializedName

data class DonateResponse(
	@field:SerializedName("data")
	val data: DonateData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class CertainDonateResponse(
	@field:SerializedName("data")
	val data: DonateItem? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DonateItem(
	@field:SerializedName("donateData")
	val donateItem: DonateData? = null,
)

data class ListDonateResponse(
	@field:SerializedName("data")
	val data: DonateDatas? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DonateDatas(
	@field:SerializedName("donateData")
	val donateDatas: List<DonateData>? = null
)

data class NewDonateResponse(
	@field:SerializedName("data")
	val data: DonateId? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DonateScheduleResponse(
	@field:SerializedName("data")
	val data: List<DonateSchedule>? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class DonateSchedule(

	@field:SerializedName("scheduleId")
	val scheduleId: String? = null,

	@field:SerializedName("scheduleStart")
	val scheduleStart: String? = null,

	@field:SerializedName("scheduleEnd")
	val scheduleEnd: String? = null,
)

data class DonateId(

	@field:SerializedName("donateId")
	val donateId: String? = null
)

data class DonateData(

	@field:SerializedName("donateId")
	val donateId: String? = null,

	@field:SerializedName("image")
	val image: List<String>? = null,

	@field:SerializedName("donateAddressDetail")
	val donateAddressDetail: String? = null,

	@field:SerializedName("donateMethod")
	val donateMethod: String? = null,

	@field:SerializedName("donateDate")
	val donateDate: String? = null,

	@field:SerializedName("donatePoint")
	val donatePoint: Int? = null,

	@field:SerializedName("trashCategories")
	val trashCategories: TrashCategories? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("donateAddress")
	val donateAddress: String? = null,

	@field:SerializedName("donateDescription")
	val donateDescription: String? = null,

	@field:SerializedName("donateWeight")
	val donateWeight: Any? = null,

	@field:SerializedName("donateStatus")
	val donateStatus: String? = null,

	@field:SerializedName("user")
	val user: UserDonate? = null,

	@field:SerializedName("donateTime")
	val donateTime: DonateTime? = null
)

data class UserDonate(

	@field:SerializedName("donorName")
	val donorName: String? = null,

	@field:SerializedName("userId")
	val userId: String? = null,

	@field:SerializedName("donorEmail")
	val donorEmail: String? = null
)

data class TrashCategories(

	@field:SerializedName("rewardPoint")
	val rewardPoint: Int? = null,

	@field:SerializedName("categoryName")
	val categoryName: String? = null,

	@field:SerializedName("categoryId")
	val categoryId: String? = null
)

data class DonateTime(

	@field:SerializedName("startTime")
	val startTime: String? = null,

	@field:SerializedName("endTime")
	val endTime: String? = null
)
