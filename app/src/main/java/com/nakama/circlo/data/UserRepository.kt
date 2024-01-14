package com.nakama.circlo.data

import com.nakama.circlo.data.pref.DataStoreManager
import com.nakama.circlo.data.remote.retrofit.ApiService
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val dataStore: DataStoreManager
) {
        suspend fun saveUser(token: String) = dataStore.saveUser(token)

        fun getUser() = dataStore.getUser()

        suspend fun clear() = dataStore.clear()
}