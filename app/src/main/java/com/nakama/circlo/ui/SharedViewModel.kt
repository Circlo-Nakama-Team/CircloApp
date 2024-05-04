package com.nakama.circlo.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nakama.circlo.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()  {

    fun getUser(): LiveData<String> {
        return repository.getUser().asLiveData()
    }

    fun getProfile(token: String) = repository.getProfile(token)
    fun getDetailAddress(token: String, addressId: String) = repository.getDetailAddress(token, addressId)
}