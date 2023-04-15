package com.example.weparentjr.data.response

import com.google.gson.annotations.SerializedName

data class addAppsResponse(
    @SerializedName("success")
    var success: Boolean,
    @SerializedName("message")
    var message: String
)