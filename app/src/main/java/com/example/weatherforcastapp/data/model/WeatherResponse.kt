package com.example.weatherforcastapp.data.model

import com.google.gson.annotations.SerializedName


//  CURRENT WEATHER RESPONSE
data class WeatherResponse(
    val name: String,
    val main: MainWeatherData,
    val weather: List<WeatherCondition>,
    val wind: Wind,
    val sys: Sys,
    val visibility: Int
)

data class MainWeatherData(
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val humidity: Int,
    @SerializedName("temp_min")
    val tempMin: Double,
    @SerializedName("temp_max")
    val tempMax: Double,
    val pressure: Int
)

data class WeatherCondition(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Wind(
    val speed: Double,
    val deg: Int = 0
)

data class Sys(
    val country: String = "",
    val sunrise: Long = 0L,
    val sunset: Long = 0L
)

//  5-DAY FORECAST RESPONSE

data class ForecastResponse(
    val list: List<ForecastItem>,   // 40 items (every 3 hours for 5 days)
    val city: ForecastCity
)

data class ForecastItem(
    val dt: Long,
    val main: MainWeatherData,
    val weather: List<WeatherCondition>,
    @SerializedName("dt_txt")
    val dtTxt: String
)

data class ForecastCity(
    val name: String,
    val country: String
)

