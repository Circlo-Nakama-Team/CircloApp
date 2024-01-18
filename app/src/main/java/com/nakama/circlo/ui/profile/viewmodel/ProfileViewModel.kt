package com.nakama.circlo.ui.profile.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nakama.circlo.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()  {

    fun getUser(): LiveData<String> {
        return repository.getUser().asLiveData()
    }

    fun logout() = viewModelScope.launch {
        repository.clear()
    }

    fun getProfile(token: String) = repository.getProfile(token)
}