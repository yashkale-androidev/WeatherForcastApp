package com.example.weatherforcastapp.data.repository

import com.example.weatherforcastapp.data.api.RetrofitInstance
import com.example.weatherforcastapp.data.model.ForecastResponse
import com.example.weatherforcastapp.data.model.WeatherResponse
import com.example.weatherforcastapp.viewmodel.Constants

class WeatherRepository {

    private val api = RetrofitInstance.api

    suspend fun getCurrentWeather(city: String): WeatherResponse {
        return api.getCurrentWeather(
            city = city,
            apiKey = Constants.API_KEY
        )
    }

    suspend fun getForecast(city: String): ForecastResponse {
        return api.getForecast(
            city = city,
            apiKey = Constants.API_KEY
        )
    }
}
