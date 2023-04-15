package com.example.weparentjr.data.request

import com.google.gson.annotations.SerializedName

data class addAppsRquest(
    @SerializedName("name")
    var name: String,
    @SerializedName("icon")
    var icon: String,
    @SerializedName("packageName")
    var packageName: String,
    @SerializedName("id")
    var id: String
)