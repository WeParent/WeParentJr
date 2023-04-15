package com.example.mealmate.data.api.methods

import com.example.mealmate.data.api.ApiClient
import com.example.weparentjr.data.request.addAppsRquest
import com.example.weparentjr.data.response.addAppsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApplicationApi {

    @POST("/application/")
    suspend fun addApplications(@Body addAppsRquest: addAppsRquest): Response<addAppsResponse>


    companion object {
        fun getApi(): ApplicationApi? {
            return ApiClient.client?.create(ApplicationApi::class.java)
        }
    }
}