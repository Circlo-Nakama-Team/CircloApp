package com.nakama.circlo.data.remote.retrofit

import com.nakama.circlo.data.remote.response.AuthResponse
import com.nakama.circlo.data.remote.response.CommunityResponse
import com.nakama.circlo.data.remote.response.DonateResponse
import com.nakama.circlo.data.remote.response.ScanResponse
import com.nakama.circlo.data.remote.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    // Auth
    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Field("firstname") firstname: String,
        @Field("lastname") lastname: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fcmToken") fcmToken: String
    ): AuthResponse

    @FormUrlEncoded
    @POST("auth/register-google")
    suspend fun registerGoogle(
        @Field("firstname") firstname: String,
        @Field("username") username: String,
        @Field("email") email: String,
        @Field("fcmToken") fcmToken: String
    ): AuthResponse

    @GET("auth/oauth/token?authtoken={authToken}")
    suspend fun oauthGoogle(
        @Path("authToken") authToken: String
    ) : AuthResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("fcmToken") fcmToken: String
    ): AuthResponse

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): AuthResponse

    // User
    @GET("user/profile")
    suspend fun getAuthUser(
        @Header("Authorization") token: String
    ): UserResponse

    @GET("user/{userId}/profile")
    suspend fun getCertainUser(
        @Path("userId") userId: String
    ): UserResponse

    @FormUrlEncoded
    @POST("user/profile/address")
    suspend fun addAddress(
        @Header("Authorization") token: String,
        @Path("address") address: String,
        @Path("detail_address") detailAddress: String,
    ): UserResponse

    @FormUrlEncoded
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Path("username") username: String,
        @Path("addressId") addressId: String
    ): UserResponse

    @Multipart
    @PUT("user/profile")
    suspend fun updateImageProfile(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): UserResponse

    @DELETE("user/profile/address/{addressId}")
    suspend fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("addressId") addressId: String
    ): UserResponse

    @GET("user/profile/address/{addressId}")
    suspend fun getAddress(
        @Header("Authorization") token: String,
        @Path("addressId") addressId: String
    ): UserResponse

    // Community
    @GET("community")
    suspend fun getCommunityPost(): CommunityResponse

    @Multipart
    @POST("community/post")
    suspend fun addPost(
        @Header("Authorization") token: String,
        @Part("postBody") postBody: RequestBody,
        @Part image: MultipartBody.Part,
    ): CommunityResponse

    @POST("community/post")
    suspend fun addPostNoImage(
        @Header("Authorization") token: String,
        @Field("postBody") postBody: String
    ): CommunityResponse

    @PUT("community/post/{postId}/likes/add")
    suspend fun likePost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): CommunityResponse

    @PUT("community/post/{postId}/likes/add")
    suspend fun unlikePost(
        @Header("Authorization") token: String,
        @Path("postId") postId: String
    ): CommunityResponse

    // Scan
    @Multipart
    @POST("trash/ideas")
    suspend fun scanTrash(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): ScanResponse

    // Donate Trash
    @POST("donate")
    @Multipart
    suspend fun donateTrash(
        @Header("Authorization") token: String,
        @Part("trashCategoriesId") trashCategoriesId: RequestBody,
        @Part("address") address: RequestBody,
        @Part("address_detail") detailAddress: RequestBody,
        @Part("donate_method") donateMethod: RequestBody,
        @Part("donate_description") donateDescription: RequestBody,
        @Part("donate_date") donateDate: RequestBody,
        @Part("donate_schedule") donateSchedule: RequestBody,
        @Part image: List<MultipartBody.Part>,

        ): DonateResponse

}
