package com.nakama.circlo.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.nakama.circlo.data.pref.DataStoreManager
import com.nakama.circlo.data.remote.response.AuthResponse
import com.nakama.circlo.data.remote.retrofit.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStoreManager
) {
        fun login(email: String, password: String): LiveData<Result<AuthResponse>> = liveData {
            emit(Result.Loading)
            try {
                val response = apiService.login(email, password)
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
            password: String
        ): LiveData<Result<AuthResponse>> = liveData {
            emit(Result.Loading)
            try {
                val response = apiService.register(firstname, lastname, username, email, password)
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
        suspend fun saveUser(token: String) = dataStore.saveUser(token)

        fun getUser() = dataStore.getUser()

        suspend fun clear() = dataStore.clear()
}