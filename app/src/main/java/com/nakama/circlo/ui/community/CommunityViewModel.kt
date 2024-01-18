package com.nakama.circlo.ui.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nakama.circlo.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()  {

    fun getToken(): LiveData<String> {
        return repository.getUser().asLiveData()
    }
    fun getProfile(token: String) = repository.getProfile(token)

    fun getCommunity() = repository.getCommunity()

    fun addPost(token: String, caption: RequestBody, image: MultipartBody.Part) = repository.addPost(token, caption, image)

    fun addPostNoImage(token: String, caption: String) = repository.addPostNoImage(token, caption)

}