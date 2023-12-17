package nodomain.pacjo.smartspacer.genericweather.ui.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerTargetProvider
import nodomain.pacjo.smartspacer.genericweather.R
import nodomain.pacjo.smartspacer.genericweather.targets.GenericWeatherTarget
import nodomain.pacjo.smartspacer.genericweather.ui.theme.getColorScheme
import nodomain.pacjo.smartspacer.genericweather.utils.PreferenceMenu
import nodomain.pacjo.smartspacer.genericweather.utils.PreferenceSlider
import nodomain.pacjo.smartspacer.genericweather.utils.SettingsTopBar
import nodomain.pacjo.smartspacer.genericweather.utils.isFirstRun
import nodomain.pacjo.smartspacer.genericweather.utils.savePreference
import org.json.JSONObject
import java.io.File

class TargetConfigurationActivity : ComponentActivity() {
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

            MaterialTheme (
                // Change default colorScheme to our dynamic one
                colorScheme = getColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        SettingsTopBar("Generic Weather")

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
                                "Temperature only",
                                "Condition only",               // TODO: can we make it scrolling?
                                "Temperature and condition"
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
                                "Kelvin",
                                "Celsius",
                                "Fahrenheit"
                            )
                        )
                    }
                }
            }
        }
    }
}

