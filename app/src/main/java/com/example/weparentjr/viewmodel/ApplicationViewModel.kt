package com.example.weparentjr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.weparentjr.data.request.addAppsRquest
import com.example.weparentjr.data.response.BaseResponse
import com.example.weparentjr.data.response.addAppsResponse
import com.example.weparentjr.repository.applicationRepositoory
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class ApplicationViewModel(application: Application) : AndroidViewModel(application) {

    val appRepo=applicationRepositoory()
    val addAppsResult:MutableLiveData<BaseResponse<addAppsResponse>> = MutableLiveData()



    fun addApps(name: String, icon: String,packageName:String,id:String) {

        addAppsResult.value = BaseResponse.Loading()
        viewModelScope.launch {
            try {

                val addAppsRquest = addAppsRquest(
                    name = name,
                    icon = icon,
                    packageName=packageName,
                    id=id
                )
                val response = appRepo.addApps(addAppsRquest = addAppsRquest)
                if (response?.code() == 200) {
                    addAppsResult.value = BaseResponse.Success(response.body())
                } else {
                    addAppsResult.value = BaseResponse.Error(response?.message())
                }

            } catch (ex: Exception) {
                addAppsResult.value = BaseResponse.Error(ex.message)
            }
        }
    }
}