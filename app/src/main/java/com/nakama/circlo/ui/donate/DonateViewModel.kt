package com.nakama.circlo.ui.donate

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.nakama.circlo.data.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class DonateViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel()  {

    fun getToken(): LiveData<String> {
        return repository.getUser().asLiveData()
    }

    fun trashDonate(
        token: String,
        trashCategoriesId: RequestBody,
        address: RequestBody,
        detailAddress: RequestBody,
        donateMethod: RequestBody,
        donateDescription: RequestBody,
        donateDate: RequestBody,
        donateSchedule: RequestBody,
        image: List<MultipartBody.Part>
    ) =
        repository.donateTrash(
        token,
        trashCategoriesId,
        address,
        detailAddress,
        donateMethod,
        donateDescription,
        donateDate,
        donateSchedule,
        image
    )

}