package nodomain.pacjo.smartspacer.genericweather.complications

import android.content.Intent
import android.graphics.drawable.Icon
import android.icu.text.SimpleDateFormat
import android.util.Log
import com.google.gson.Gson
import com.kieronquinn.app.smartspacer.sdk.annotations.DisablingTrim
import com.kieronquinn.app.smartspacer.sdk.model.SmartspaceAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.TapAction
import com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Text
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.utils.ComplicationTemplate
import com.kieronquinn.app.smartspacer.sdk.utils.TrimToFit
import nodomain.pacjo.smartspacer.genericweather.R
import nodomain.pacjo.smartspacer.genericweather.ui.activities.ConfigurationActivity
import nodomain.pacjo.smartspacer.genericweather.utils.SimpleDateFormatWrapper
import nodomain.pacjo.smartspacer.genericweather.utils.WeatherData
import nodomain.pacjo.smartspacer.genericweather.utils.convertTimeTo
import nodomain.pacjo.smartspacer.genericweather.utils.isFirstRun
import org.json.JSONObject
import java.io.File
import kotlin.math.max
import kotlin.math.min

class GenericSunTimesComplication: SmartspacerComplicationProvider() {

    @OptIn(DisablingTrim::class)
    override fun getSmartspaceActions(smartspacerId: String): List<SmartspaceAction> {
        val file = File(context?.filesDir, "data.json")

        isFirstRun(context!!)
        val jsonString = file.readText()
        val jsonObject = JSONObject(jsonString)

        // get preferences
        val preferences = jsonObject.getJSONObject("preferences")
        val complicationStyle = preferences.optString("complication_suntimes_style","exact")
        val complicationTrimToFit = preferences.optBoolean("complication_suntimes_trim_to_fit",true)
        val launchPackage = preferences.optString("complication_launch_package", "")        // TODO: maybe make one for all

        // get weather data
        val weather = jsonObject.getJSONObject("weather").toString()
        if (weather != "{}") {

            val gson = Gson()
            val weatherData = gson.fromJson(weather, WeatherData::class.java)

            val nextSunrise: Long = when (System.currentTimeMillis() < weatherData.sunRise * 1000L) {
                true -> weatherData.sunRise                 // if we're still before today's sunrise
                else -> weatherData.forecasts[0].sunRise
            } * 1000L           // broken without this

            val nextSunset: Long = when (System.currentTimeMillis() < weatherData.sunSet * 1000L) {
                true -> weatherData.sunSet                 // if we're still before today's sunset
                else -> weatherData.forecasts[0].sunSet
            } * 1000L           // broken without this

            // so, we have next sunrise and sunset
            // we'll always show next event (relative to the current time)
            // so it'll be min(nextSunrise, nextSunset)

            val nextEvent = min(nextSunrise, nextSunset)

            return listOf(
                ComplicationTemplate.Basic(
                    id = "example_$smartspacerId",
                    icon = com.kieronquinn.app.smartspacer.sdk.model.uitemplatedata.Icon(
                        Icon.createWithResource(
                            context,
                            when (nextEvent == nextSunrise) {
                                true -> R.drawable.ic_sunrise
                                else -> R.drawable.ic_sunset
                            }
                        )
                    ),
                    content = Text(
                        when (complicationStyle) {
                            "time_to" -> "in ${SimpleDateFormatWrapper(nextEvent - System.currentTimeMillis(), true)}"
                            "both" -> "in ${SimpleDateFormatWrapper(nextEvent - System.currentTimeMillis(), true)} (${SimpleDateFormat("HH:mm").format(nextEvent)})"
                            else -> SimpleDateFormat("HH:mm").format(nextEvent)
                        }
                    ),
                    onClick = when (context!!.packageManager.getLaunchIntentForPackage(launchPackage)) {
                        null -> null
                        else -> TapAction(
                            intent = Intent(context!!.packageManager.getLaunchIntentForPackage(launchPackage))
                        )
                    },
                    trimToFit = when (complicationTrimToFit) {
                        false -> TrimToFit.Disabled
                        else -> TrimToFit.Enabled
                    }
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
                    onClick = null
                ).create()
            )
        }
    }

    override fun getConfig(smartspacerId: String?): Config {
        return Config(
            label = "Generic sun times",
            description = "Shows sunrise / sunset information from supported apps",
            icon = Icon.createWithResource(context, R.drawable.ic_sunrise),
            configActivity = Intent(context, ConfigurationActivity::class.java),
            broadcastProvider = "nodomain.pacjo.smartspacer.genericweather.providers.weather"
        )
    }

}