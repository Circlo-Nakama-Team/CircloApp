package com.nakama.circlo.ui.order

import androidx.lifecycle.ViewModel
import com.nakama.circlo.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()  {

    fun getProfile(token: String) = repository.getProfile(token)

    fun getDonateHistory(token: String) = repository.getListDonate(token)

    fun getDetailDonate(token: String, donateId: String) = repository.getDetailDonate(token, donateId)
}