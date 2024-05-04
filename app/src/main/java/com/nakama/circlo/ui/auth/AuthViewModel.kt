package com.nakama.circlo.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import com.nakama.circlo.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    fun login(email: String, password: String, fcmToken: String) = repository.login(email, password, fcmToken)

    fun register(
        firstname: String,
        lastname: String,
        username: String,
        email: String,
        password: String
    ) = repository.register(firstname, lastname, username, email, password)

    fun registerGoogle(
        firstname: String,
        username: String,
        email: String,
        fcmToken: String
    ) = repository.registerGoogle(firstname, username, email, fcmToken)

    fun saveUser(token: String) = viewModelScope.launch {
        try {
            repository.saveUser(token)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Error Save Token", e.toString())
        }
    }

    suspend fun getFcmToken(): String {
        return Firebase.messaging.token.await()
    }
}