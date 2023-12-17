package nodomain.pacjo.smartspacer.genericweather.complications

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Icon
import android.util.Log
import androidx.core.graphics.drawable.IconCompat
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import nodomain.pacjo.smartspacer.genericweather.R
import nodomain.pacjo.smartspacer.genericweather.ui.activities.ComplicationConfigurationActivity
import nodomain.pacjo.smartspacer.genericweather.utils.WeatherData
import nodomain.pacjo.smartspacer.genericweather.utils.temperatureUnitConverter
import nodomain.pacjo.smartspacer.genericweather.utils.weatherDataToIcon
import org.json.JSONObject
import java.io.File

class GenericWeatherComplication: SmartspacerComplicationProvider() {

    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {

        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)
        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val complicationUnit = preferences.optString("complication_unit", "Celsius")
        val complicationStyle = preferences.optString("complication_style","Temperature only")

        // get weather data
        val weather = jsonObject.getJSONObject("weather").toString()
        if (weather != "{}") {

            val gson = Gson()
            val weatherData = gson.fromJson(weather, WeatherData::class.java)

            return listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        IconCompat.createWithBitmap(
                            Bitmap.createScaledBitmap(
                                BitmapFactory.decodeResource(
                                    resources,
                                    weatherDataToIcon(weatherData, 0)
                                ),
                                50,
                                50,
                                true
                            )
                        ).toIcon(context),
                        shouldTint = false
                    ),
                    content = Text(when (complicationStyle) {
                        "Temperature only" -> temperatureUnitConverter(weatherData.currentTemp, complicationUnit)
                        "Condition only" -> weatherData.currentCondition
                        "Temperature and condition" -> "${temperatureUnitConverter(weatherData.currentTemp, complicationUnit)} ${weatherData.currentCondition}"
                        else -> temperatureUnitConverter(weatherData.currentTemp, complicationUnit)         // TODO: check if it can be re-written
                    }),
                    onClick = null      // TODO: open weather app
                ).create()
            )
        } else {
            // If nothing was returned above
            return listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            context,
                            R.drawable.baseline_error_24
                        )
                    ),
                    content = Text("No data"),
                    onClick = null      // TODO: open weather app
                ).create()
            )
        }
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Generic weather",
            description = "Shows temperature and/or condition icon from supported apps",
            icon = Icon.createWithResource(context, R.drawable.ic_launcher_foreground),     // TODO: change
            configActivity = Intent(context, ComplicationConfigurationActivity::class.java),
            broadcastProvider = "nodomain.pacjo.smartspacer.genericweather.providers.weather"
        )
    }

}