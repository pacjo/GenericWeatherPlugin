package nodomain.pacjo.smartspacer.genericweather.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.genericweather.R
import nodomain.pacjo.smartspacer.genericweather.complications.GenericSunTimesComplication
import nodomain.pacjo.smartspacer.genericweather.complications.GenericSunriseComplication
import nodomain.pacjo.smartspacer.genericweather.complications.GenericSunsetComplication
import nodomain.pacjo.smartspacer.genericweather.complications.GenericWeatherComplication
import nodomain.pacjo.smartspacer.genericweather.targets.GenericWeatherTarget
import nodomain.pacjo.smartspacer.genericweather.ui.theme.getColorScheme
import nodomain.pacjo.smartspacer.genericweather.utils.PreferenceInput
import nodomain.pacjo.smartspacer.genericweather.utils.PreferenceMenu
import nodomain.pacjo.smartspacer.genericweather.utils.PreferenceSlider
import nodomain.pacjo.smartspacer.genericweather.utils.PreferenceSwitch
import nodomain.pacjo.smartspacer.genericweather.utils.SettingsTopBar
import nodomain.pacjo.smartspacer.genericweather.utils.isFirstRun
import nodomain.pacjo.smartspacer.genericweather.utils.savePreference
import org.json.JSONObject
import java.io.File

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            isFirstRun(context)

            // get number of forecast points (as we need it to show the default)
            val file = File(context.filesDir, "data.json")
            val jsonObject = JSONObject(file.readText())
            val preferences = jsonObject.getJSONObject("preferences")
            val dataPoints = preferences.optInt("target_point_visible", 4)
            val targetlaunchPackage = preferences.optString("target_launch_package", "")

            val complicationTrimToFit = preferences.optBoolean("complication_launch_package", true)
            val complicationlaunchPackage = preferences.optString("complication_launch_package", "")

            val sunriseForceEnable = preferences.optBoolean("complication_sunrise_forceenable", false)
            val sunsetForceEnable = preferences.optBoolean("complication_sunset_forceenable", false)

            MaterialTheme (
                // Change default colorScheme to our dynamic one
                colorScheme = getColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState())

                    ) {                        // TODO: check lazycolumn
                        SettingsTopBar("Generic Weather")

                        Divider()
                        Text("Weather target")

                        PreferenceSlider(
                            icon = R.drawable.baseline_error_24,
                            title = "Forecast points to show",
                            subtitle = "Select number of visible forecast days/hours",
                            stateCallback = {
                                value -> savePreference(context,"target_point_visible", value)
                                SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                            },
                            range = (0..4),
                            defaultPosition = dataPoints.toFloat()
                        )

//                        PreferenceMenu(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Data source",
//                            subtitle = "Select complication style",
//                            stateCallback = {
//                                    value -> savePreference(context,"target_data_source", value)
//                                SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
//                            },
//                            items = listOf(
//                                "Hourly forecast",
//                                "Daily forecast"
//                            )
//                        )

                        PreferenceMenu(
                            icon = R.drawable.baseline_error_24,
                            title = "Style",
                            subtitle = "Select complication style",
                            stateCallback = {
                                value -> savePreference(context,"target_style", value)
                                SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                            },
                            items = listOf(
                                Pair("Temperature only", "temperature"),
                                Pair("Condition only", "condition"),
                                Pair("Temperature and condition", "both")
                            )
                        )

                        PreferenceMenu(
                            icon = R.drawable.baseline_error_24,
                            title = "Unit",
                            subtitle = "Select temperature unit",
                            stateCallback = {
                                value -> savePreference(context,"target_unit", value)
                                SmartspacerTargetProvider.notifyChange(context, GenericWeatherTarget::class.java)
                            },
                            items = listOf(
                                Pair("Kelvin", "K"),
                                Pair("Celsius", "C"),
                                Pair("Fahrenheit", "F")
                            )
                        )

                        PreferenceInput(
                            icon = R.drawable.baseline_error_24,
                            title = "Launch Package",
                            subtitle = "Select package name of an app to open when complication is clicked",
                            stateCallback = {
                                value -> savePreference(context,"target_launch_package", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                            },
                            dialogText = "Enter package name",
                            defaultText = targetlaunchPackage
                        )

                        Divider()
                        Text("Weather complication")

                        PreferenceMenu(
                            icon = R.drawable.baseline_error_24,
                            title = "Style",
                            subtitle = "Select complication style",
                            stateCallback = {
                                value -> savePreference(context,"complication_style", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                            },
                            items = listOf(
                                Pair("Temperature only", "temperature"),
                                Pair("Condition only", "condition"),
                                Pair("Temperature and condition", "both")
                            )
                        )

                        PreferenceMenu(
                            icon = R.drawable.baseline_error_24,
                            title = "Unit",
                            subtitle = "Select temperature unit",
                            stateCallback = {
                                value -> savePreference(context,"complication_unit", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                            },
                            items = listOf(
                                Pair("Kelvin", "K"),
                                Pair("Celsius", "C"),
                                Pair("Fahrenheit", "F")
                            )
                        )

                        PreferenceInput(
                            icon = R.drawable.baseline_error_24,
                            title = "Launch Package",
                            subtitle = "Select package name of an app to open when complication is clicked",
                            stateCallback = {
                                value -> savePreference(context,"complication_launch_package", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                            },
                            dialogText = "Enter package name",
                            defaultText = complicationlaunchPackage
                        )

                        PreferenceSwitch(
                            icon = R.drawable.baseline_error_24,
                            title = "Complication text trimming",
                            subtitle = "Disable this if text is getting cut off. May \ncause unexpected results",       // TODO: fix (also exchange with RelativeSizeSpan)
                            stateCallback = {
                                value -> savePreference(context,"complication_trim_to_fit", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                            },
                            checked = complicationTrimToFit
                        )

//                        Divider()
//                        Text("Sunrise complication")      // TODO: make proper title
//
//                        PreferenceMenu(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Style",
//                            subtitle = "Select complication style",
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunrise_style", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunriseComplication::class.java)
//                            },
//                            items = listOf(
//                                Pair("Exact time", "exact"),
//                                Pair("Time to", "time_to"),
//                                Pair("Time to + exact time", "both")
//                            )
//                        )
//
//                        PreferenceMenu(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Show before",
//                            subtitle = "Show complication x hours before sunrise",
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunrise_show_before", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunriseComplication::class.java)
//                            },
//                            items = listOf(
//                                Pair("10 minutes", 600000),
//                                Pair("15 minutes", 900000),
//                                Pair("30 minutes", 1800000),
//                                Pair("1 hour", 3600000),
//                                Pair("2 hours", 7200000),
//                                Pair("6 hours", 21600000),
//                                Pair("12 hours", 43200000)
//                            )
//                        )
//
//                        PreferenceMenu(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Show after",
//                            subtitle = "Show complication x hours after sunrise",
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunrise_show_after", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunriseComplication::class.java)
//                            },
//                            items = listOf(
//                                Pair("10 minutes", 600000),
//                                Pair("15 minutes", 900000),
//                                Pair("30 minutes", 1800000),
//                                Pair("1 hour", 3600000),
//                                Pair("2 hours", 7200000),
//                                Pair("6 hours", 21600000),
//                                Pair("12 hours", 43200000)
//                            )
//                        )
//
//                        PreferenceSwitch(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Complication text trimming",
//                            subtitle = "Disable this if text is getting cut off. May \ncause unexpected results",       // TODO: fix (also exchange with RelativeSizeSpan)
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunrise_trim_to_fit", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunriseComplication::class.java)
//                            },
//                            checked = complicationTrimToFit
//                        )
//
//                        PreferenceSwitch(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Force show complication",
//                            subtitle = "Forces complication to be visible, regardless\n other settings",
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunrise_forceenable", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunsetComplication::class.java)
//                            },
//                            checked = sunriseForceEnable
//                        )
//
//                        Divider()
//                        Text("Sunset complication")      // TODO: make proper title
//
//                        PreferenceMenu(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Style",
//                            subtitle = "Select complication style",
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunset_style", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunsetComplication::class.java)
//                            },
//                            items = listOf(
//                                Pair("Exact time", "exact"),
//                                Pair("Time to", "time_to"),
//                                Pair("Time to + exact time", "both")
//                            )
//                        )
//
//                        PreferenceMenu(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Show before",
//                            subtitle = "Show complication x hours before sunset",
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunset_show_before", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunsetComplication::class.java)
//                            },
//                            items = listOf(
//                                Pair("10 minutes", 600000),
//                                Pair("15 minutes", 900000),
//                                Pair("30 minutes", 1800000),
//                                Pair("1 hour", 3600000),
//                                Pair("2 hours", 7200000),
//                                Pair("6 hours", 21600000),
//                                Pair("12 hours", 43200000)
//                            )
//                        )
//
//                        PreferenceMenu(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Show after",
//                            subtitle = "Show complication x hours after sunset",
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunset_show_after", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunsetComplication::class.java)
//                            },
//                            items = listOf(
//                                Pair("10 minutes", 600000),
//                                Pair("15 minutes", 900000),
//                                Pair("30 minutes", 1800000),
//                                Pair("1 hour", 3600000),
//                                Pair("2 hours", 7200000),
//                                Pair("6 hours", 21600000),
//                                Pair("12 hours", 43200000)
//                            )
//                        )
//
//                        PreferenceSwitch(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Complication text trimming",
//                            subtitle = "Disable this if text is getting cut off. May \ncause unexpected results",       // TODO: fix (also exchange with RelativeSizeSpan)
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunset_trim_to_fit", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunsetComplication::class.java)
//                            },
//                            checked = complicationTrimToFit
//                        )
//
//                        PreferenceSwitch(
//                            icon = R.drawable.baseline_error_24,
//                            title = "Force show complication",
//                            subtitle = "Forces complication to be visible, regardless\n other settings",
//                            stateCallback = {
//                                value -> savePreference(context,"complication_sunset_forceenable", value)
//                                SmartspacerComplicationProvider.notifyChange(context, GenericSunsetComplication::class.java)
//                            },
//                            checked = sunsetForceEnable
//                        )

                        Divider()
                        Text("Sun times complication")

                        PreferenceMenu(
                            icon = R.drawable.baseline_error_24,
                            title = "Style",
                            subtitle = "Select complication style",
                            stateCallback = {
                                value -> savePreference(context,"complication_suntimes_style", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericSunTimesComplication::class.java)
                            },
                            items = listOf(
                                Pair("Exact time", "exact"),
                                Pair("Time to", "time_to"),
                                Pair("Time to + exact time", "both")
                            )
                        )

                        PreferenceSwitch(
                            icon = R.drawable.baseline_error_24,
                            title = "Complication text trimming",
                            subtitle = "Disable this if text is getting cut off. May \ncause unexpected results",       // TODO: fix (also exchange with RelativeSizeSpan)
                            stateCallback = {
                                value -> savePreference(context,"complication_suntimes_trim_to_fit", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericSunTimesComplication::class.java)
                            },
                            checked = complicationTrimToFit
                        )
                    }
                }
            }
        }
    }
}

