package com.example.weparentjr.network
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path



interface apiService {


    @POST("/user/addGuardian/{id}")
    fun sendApps(@Path("id") id: String, @Body requestBody: JsonObject): Call<JsonElement>



}