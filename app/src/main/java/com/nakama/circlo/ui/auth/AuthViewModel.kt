package com.nakama.circlo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nakama.circlo.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    fun login(email: String, password: String) = repository.login(email, password)

    fun register(
        firstname: String,
        lastname: String,
        username: String,
        email: String,
        password: String
    ) = repository.register(firstname, lastname, username, email, password)

    fun registerGoogle(
        userId: String,
        firstname: String,
        username: String,
        email: String
    ) = repository.registerGoogle(userId, firstname, username, email)

    fun logout(token: String) = repository.logout(token)

    suspend fun saveUser(token: String) = repository.saveUser(token)

}