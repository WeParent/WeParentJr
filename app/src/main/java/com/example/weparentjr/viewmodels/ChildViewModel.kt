package com.example.weparentjr.viewmodels

import androidx.lifecycle.ViewModel
import com.example.weparentjr.network.RetrofitInstance
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class childViewModel() : ViewModel(){

    // Create a Retrofit instance
    private val retrofit = RetrofitInstance.retrofit

    // Create an instance of your API service interface
    private val apiService = retrofit.create(com.example.weparentjr.network.apiService::class.java)



    fun sendApps(id : String ,jsonObject: JsonObject, callback: (String?, Int) -> Unit) {
        apiService.sendApps(id,jsonObject).enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        callback(responseBody.toString(),response.code())
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    callback(errorBody,response.code())
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                callback(t.message,501)
            }
        })
    }









}