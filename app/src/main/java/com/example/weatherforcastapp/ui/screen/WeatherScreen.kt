package com.example.weatherforcastapp.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.weatherforcastapp.R
import com.example.weatherforcastapp.data.model.ForecastItem
import com.example.weatherforcastapp.data.model.ForecastResponse
import com.example.weatherforcastapp.data.model.WeatherResponse
import com.example.weatherforcastapp.viewmodel.Constants
import com.example.weatherforcastapp.viewmodel.WeatherUiState
import com.example.weatherforcastapp.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

//  MAIN SCREEN
@Composable
fun WeatherScreen(
    weatherViewModel: WeatherViewModel = viewModel()
) {
    val uiState by weatherViewModel.uiState.collectAsStateWithLifecycle()

    var cityInput by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    // Full-screen dark blue gradient background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,   // Top — medium navy
                        MaterialTheme.colorScheme.secondaryContainer    // Bottom — very dark navy
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Title
            Text(
                text = "🌤  Weather Forecast",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Search Bar
            SearchBar(
                query = cityInput,
                onQueryChange = { cityInput = it },
                onSearch = {
                    keyboardController?.hide()
                    weatherViewModel.searchWeather(cityInput)
                }
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Content (changes based on state)
            when (val state = uiState) {
                is WeatherUiState.Idle -> IdlePlaceholder()
                is WeatherUiState.Loading -> LoadingContent()
                is WeatherUiState.Success -> WeatherContent(
                    weather = state.weather,
                    forecast = state.forecast
                )

                is WeatherUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = {
                        weatherViewModel.searchWeather(cityInput)
                    }
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

//  SEARCH BAR
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = "Enter city name...",
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch() }
        ),
        // Search icon button on the right side of the text field
        trailingIcon = {
            IconButton(onClick = { onSearch() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        },

        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.primary,
            unfocusedTextColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            cursorColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(14.dp)
    )
}


//  STATE SCREENS
@Composable
fun IdlePlaceholder() {
    Column(
        modifier = Modifier.padding(top = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🌍", fontSize = 72.sp)
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Search for a city\nto see the weather",
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            lineHeight = 26.sp
        )
    }
}

/** Shown while network request is in progress */
@Composable
fun LoadingContent() {
    Column(
        modifier = Modifier
            .padding(top = 60.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Fetching weather data...",
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
            fontSize = 15.sp
        )
    }
}

/** Shown when something goes wrong */
@Composable
fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(top = 50.dp)
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "⚠️", fontSize = 52.sp)
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = message,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            lineHeight = 24.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Try Again", color = MaterialTheme.colorScheme.primary, fontSize = 15.sp)
        }
    }
}


//  SUCCESS — WEATHER CONTENT
@Composable
fun WeatherContent(
    weather: WeatherResponse,
    forecast: ForecastResponse
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // City name + country code (e.g. "London, GB")
        Text(
            text = "${weather.name}, ${weather.sys.country}",
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )

        // Today's date
        Text(
            text = getTodayDate(),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Weather icon loaded from OpenWeatherMap's CDN using Coil
        val iconCode = weather.weather.firstOrNull()?.icon ?: "01d"
        AsyncImage(
            model = "${Constants.ICON_BASE_URL}${iconCode}@4x.png",
            contentDescription = "Weather icon",
            modifier = Modifier.size(110.dp)
        )

        Text(
            text = "${weather.main.temp.roundToInt()}°C",
            fontSize = 76.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = weather.weather
                .firstOrNull()
                ?.description
                ?.replaceFirstChar { it.uppercaseChar() }
                ?: "",
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Calculate today's max/min from the forecast list
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayItems = forecast.list.filter {
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date(it.dt * 1000L)) == todayDate
        }

        // Aggregate current weather and all today's forecast slots for a better range
        val allTodayTemps =
            mutableListOf(weather.main.temp, weather.main.tempMax, weather.main.tempMin)
        todayItems.forEach {
            allTodayTemps.add(it.main.temp)
            allTodayTemps.add(it.main.tempMax)
            allTodayTemps.add(it.main.tempMin)
        }

        val maxTemp = allTodayTemps.maxOrNull() ?: weather.main.tempMax
        val minTemp = allTodayTemps.minOrNull() ?: weather.main.tempMin

        Text(
            text = "↑ ${maxTemp.roundToInt()}°  |  ↓ ${minTemp.roundToInt()}°",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.65f)
        )

        Spacer(modifier = Modifier.height(28.dp))

        WeatherDetailsCard(weather = weather)

        Spacer(modifier = Modifier.height(28.dp))

        ForecastSection(forecastItems = forecast.list)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

//  DETAILS CARD (humidity, wind, feels like, etc.)

@Composable
fun WeatherDetailsCard(weather: WeatherResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Gray.copy(alpha = 0.12f)
        )
    ) {
        Column(modifier = Modifier.padding(vertical = 16.dp, horizontal = 8.dp)) {

            // Row 1: Humidity | Wind | Feels Like
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherDetailItem(
                    emoji = "💧",
                    label = "Humidity",
                    value = "${weather.main.humidity}%"
                )
                VerticalDivider()
                WeatherDetailItem(
                    emoji = "💨",
                    label = "Wind",
                    value = "${weather.wind.speed} m/s"
                )
                VerticalDivider()
                WeatherDetailItem(
                    emoji = "🌡️",
                    label = "Feels Like",
                    value = "${weather.main.feelsLike.roundToInt()}°C"
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f)
            )

            // Row 2: Visibility | Pressure | Sunrise
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                WeatherDetailItem(
                    emoji = "👁️",
                    label = "Visibility",
                    value = "${weather.visibility / 1000} km"
                )
                VerticalDivider()
                WeatherDetailItem(
                    emoji = "📊",
                    label = "Pressure",
                    value = "${weather.main.pressure} hPa"
                )
                VerticalDivider()
                WeatherDetailItem(
                    emoji = "🌅",
                    label = "Sunrise",
                    value = unixToTime(weather.sys.sunrise)
                )
            }
        }
    }
}

@Composable
fun WeatherDetailItem(emoji: String, label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(text = emoji, fontSize = 22.sp)
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
        Text(
            text = label,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f),
            fontSize = 11.sp
        )
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(52.dp)
            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f))
    )
}

//  5-DAY FORECAST SECTION
@Composable
fun ForecastSection(forecastItems: List<ForecastItem>) {
    // Filter to get one forecast per day (preferring noon)
    val dailyForecasts = remember(forecastItems) {
        getDailyForecasts(forecastItems)
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "5-Day Forecast",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Gray.copy(alpha = 0.12f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                dailyForecasts.forEachIndexed { index, item ->
                    ForecastRow(item = item)

                    if (index < dailyForecasts.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ForecastRow(item: ForecastItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Day name (e.g. "Mon", "Tue")
        Text(
            text = getDayName(item.dt),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.2f)
        )

        // Weather description (e.g. "Light rain")
        Text(
            text = item.weather.firstOrNull()?.main ?: "",
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
            fontSize = 13.sp,
            modifier = Modifier.weight(1.5f)
        )

        // Weather icon
        val iconCode = item.weather.firstOrNull()?.icon ?: "01d"
        AsyncImage(
            model = "${Constants.ICON_BASE_URL}${iconCode}@2x.png",
            contentDescription = null,
            modifier = Modifier.size(38.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        // High / Low temperature
        Row {
            Text(
                text = "${item.main.tempMax.roundToInt()}°",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = " / ${item.main.tempMin.roundToInt()}°",
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
        }
    }
}


// Returns today's date as a readable string, e.g. "Friday, June 20, 2025"
fun getTodayDate(): String {
    val sdf = SimpleDateFormat("EEEE, MMMM dd yyyy", Locale.getDefault())
    return sdf.format(Date())
}

fun unixToTime(unixSeconds: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(unixSeconds * 1000L))
}

fun getDayName(unixSeconds: Long): String {
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
    return sdf.format(Date(unixSeconds * 1000L))
}

fun getDailyForecasts(forecastList: List<ForecastItem>): List<ForecastItem> {
    val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    // Group all items by their date (yyyy-MM-dd)
    val grouped = forecastList.groupBy { item ->
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            .format(Date(item.dt * 1000L))
    }

    return grouped
        .filter { (date, _) -> date != todayDate }      // skip today
        .map { (_, items) ->
            // Calculate actual daily high/low by looking at all 3-hour slots for that day
            val maxTemp = items.maxOf { it.main.tempMax }
            val minTemp = items.minOf { it.main.tempMin }

            // Use the noon forecast for the icon and general weather description
            val noonItem = items.firstOrNull { it.dtTxt.contains("12:00:00") } ?: items.first()

            noonItem.copy(
                main = noonItem.main.copy(
                    tempMax = maxTemp,
                    tempMin = minTemp
                )
            )
        }
        .sortedBy { it.dt }
        .take(5)
}
