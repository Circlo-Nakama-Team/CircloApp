package com.nakama.circlo.data.remote.retrofit

import com.nakama.circlo.data.remote.response.AuthResponse
import com.nakama.circlo.data.remote.response.UserResponse
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiService {

    @FormUrlEncoded
    @POST("auth/register")
    suspend fun register(
        @Path("firstname") firstname: String,
        @Path("lastname") lastname: String,
        @Path("username") username: String,
        @Path("email") email: String,
        @Path("password") password: String,
    ): AuthResponse

    @FormUrlEncoded
    @POST("auth/login")
    suspend fun login(
        @Path("email") email: String,
        @Path("password") password: String,
    ): AuthResponse

    @POST("auth/logout")
    suspend fun logout(
        @Header("Authorization") token: String
    ): AuthResponse

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
}