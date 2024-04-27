package com.nakama.circlo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.google.gson.Gson
import com.nakama.circlo.data.pref.DataStoreManager
import com.nakama.circlo.data.remote.response.AuthResponse
import com.nakama.circlo.data.remote.response.CommunityResponse
import com.nakama.circlo.data.remote.response.DonateResponse
import com.nakama.circlo.data.remote.response.ScanResponse
import com.nakama.circlo.data.remote.response.UserResponse
import com.nakama.circlo.data.remote.retrofit.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStoreManager
) {
    fun login(email: String, password: String, fcmToken: String): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.login(email, password, fcmToken)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun authGoogle(authToken: String) : LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.oauthGoogle(authToken)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun register(
        firstname: String,
        lastname: String,
        username: String,
        email: String,
        password: String,
        fcmToken: String
    ): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(firstname, lastname, username, email, password, fcmToken)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun registerGoogle(
        firstname: String,
        username: String,
        email: String,
        fcmToken: String
    ): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.registerGoogle(firstname, username, email, fcmToken)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun logout(token: String): LiveData<Result<AuthResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.logout(token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getProfile(token: String): LiveData<Result<UserResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getAuthUser(token)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Trash Scan
    fun scanTrash(token: String, image: MultipartBody.Part): LiveData<Result<ScanResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.scanTrash(token, image)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Community
    fun getCommunity(): LiveData<Result<CommunityResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.getCommunityPost()
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun addPost(token: String, postBody: RequestBody, image: MultipartBody.Part): LiveData<Result<CommunityResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addPost(token, postBody, image)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun addPostNoImage(token: String, postBody: String): LiveData<Result<CommunityResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.addPostNoImage(token, postBody)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun likePost(token: String, postId: String): LiveData<Result<CommunityResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.likePost(token, postId)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    fun unlikePost(token: String, postId: String): LiveData<Result<CommunityResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.unlikePost(token, postId)
            emit(Result.Success(response))
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        }
    }

    // Donate
    fun donateTrash(
        token: String,
        trashId: RequestBody,
        address: RequestBody,
        detailAddress: RequestBody,
        donateMethod: RequestBody,
        donateDescription: RequestBody,
        donateDate: RequestBody,
        donateSchedule: RequestBody,
        image: List<MultipartBody.Part>
    ): LiveData<Result<DonateResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.donateTrash(
                token,
                trashId,
                address,
                detailAddress,
                donateMethod,
                donateDescription,
                donateDate,
                donateSchedule,
                image
            )
            if (response.status == "Success") {
                emit(Result.Success(response))
            } else {
                emit(Result.Error(response.message.toString()))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.message.toString()))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, DonateResponse::class.java)
            val errorMessage = errorBody.status
            emit(Result.Error(errorMessage.toString()))
        }
    }

    suspend fun saveUser(token: String) = dataStore.saveUser(token)

    fun getUser() = dataStore.getUser()

    suspend fun clear() = dataStore.clear()
}