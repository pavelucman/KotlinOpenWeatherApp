package com.pavel.openweather.apiObjects

import com.google.gson.annotations.SerializedName

data class Main(
   @SerializedName("temp")
   val temp: Double = 0.0,

   @SerializedName("humidity")
   val humidity: Int = 0
)