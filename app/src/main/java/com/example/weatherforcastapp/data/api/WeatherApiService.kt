package com.example.weatherforcastapp.data.api

import com.example.weatherforcastapp.data.model.ForecastResponse
import com.example.weatherforcastapp.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    // Fetch current weather for a city.
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    // Fetch 5-day forecast
    @GET("forecast")
    suspend fun getForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): ForecastResponse
}