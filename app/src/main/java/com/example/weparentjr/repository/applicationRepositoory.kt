package com.example.weparentjr.repository

import com.example.mealmate.data.api.methods.ApplicationApi
import com.example.weparentjr.data.request.addAppsRquest
import com.example.weparentjr.data.response.addAppsResponse
import retrofit2.Response

class applicationRepositoory {

suspend fun addApps(addAppsRquest: addAppsRquest):Response<addAppsResponse>?{
return ApplicationApi.getApi()?.addApplications(addAppsRquest=addAppsRquest)
}
}