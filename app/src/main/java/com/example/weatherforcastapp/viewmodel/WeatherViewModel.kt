package com.example.weatherforcastapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherforcastapp.data.model.ForecastResponse
import com.example.weatherforcastapp.data.model.WeatherResponse
import com.example.weatherforcastapp.data.repository.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.UnknownHostException


sealed class WeatherUiState {
    object Idle : WeatherUiState()
    object Loading : WeatherUiState()

    data class Success(
        val weather: WeatherResponse,
        val forecast: ForecastResponse
    ) : WeatherUiState()

    data class Error(val message: String) : WeatherUiState()
}


class WeatherViewModel : ViewModel() {

    private val repository = WeatherRepository()

    private val _uiState = MutableStateFlow<WeatherUiState>(WeatherUiState.Idle)

    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()


    fun searchWeather(city: String) {

        // Don't search if input is empty
        if (city.isBlank()) {
            _uiState.value = WeatherUiState.Error("Please enter a city name")
            return
        }

        viewModelScope.launch {
            _uiState.value = WeatherUiState.Loading

            try {

                val weather = repository.getCurrentWeather(city)
                val forecast = repository.getForecast(city)

                _uiState.value = WeatherUiState.Success(weather, forecast)

            } catch (e: HttpException) {
                // The server replied with an error status code
                val errorMessage = when (e.code()) {
                    401 -> "❌ Invalid API key.\nCheck your key in Constants.kt"
                    404 -> "🔍 City \"$city\" not found.\nTry a different spelling"
                    429 -> "⏳ Too many requests.\nPlease wait a moment and try again"
                    else -> "Server error (${e.code()}). Please try again."
                }
                _uiState.value = WeatherUiState.Error(errorMessage)

            } catch (e: UnknownHostException) {
                // No internet connection
                _uiState.value = WeatherUiState.Error(
                    "📡 No internet connection.\nPlease check your network settings"
                )

            } catch (e: Exception) {
                // Any other unexpected error
                _uiState.value = WeatherUiState.Error(
                    "Something went wrong:\n${e.localizedMessage}"
                )
            }
        }
    }
}
