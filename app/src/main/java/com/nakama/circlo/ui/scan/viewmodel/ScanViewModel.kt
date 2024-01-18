package com.nakama.circlo.ui.scan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.nakama.circlo.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()  {

    fun getUser(): LiveData<String> {
        return repository.getUser().asLiveData()
    }

    fun scanTrash(token: String, image: MultipartBody.Part) = repository.scanTrash(token, image)
}