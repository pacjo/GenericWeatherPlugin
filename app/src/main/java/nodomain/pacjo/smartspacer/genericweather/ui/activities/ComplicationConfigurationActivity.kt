package nodomain.pacjo.smartspacer.genericweather.ui.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.kieronquinn.app.smartspacer.sdk.provider.SmartspacerComplicationProvider
import nodomain.pacjo.smartspacer.genericweather.R
import nodomain.pacjo.smartspacer.genericweather.complications.GenericWeatherComplication
import nodomain.pacjo.smartspacer.genericweather.ui.theme.getColorScheme
import nodomain.pacjo.smartspacer.genericweather.utils.PreferenceInput
import nodomain.pacjo.smartspacer.genericweather.utils.PreferenceMenu
import nodomain.pacjo.smartspacer.genericweather.utils.SettingsTopBar
import nodomain.pacjo.smartspacer.genericweather.utils.isFirstRun
import nodomain.pacjo.smartspacer.genericweather.utils.savePreference
import org.json.JSONObject
import java.io.File

class ComplicationConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            isFirstRun(context)

            // get launch package (as we need it to show the default)
            val file = File(context.filesDir, "data.json")
            val jsonObject = JSONObject(file.readText())
            val preferences = jsonObject.getJSONObject("preferences")
            val launchPackage = preferences.optString("complication_launch_package", "")

            MaterialTheme (
                // Change default colorScheme to our dynamic one
                colorScheme = getColorScheme()
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        SettingsTopBar("Generic Weather")

                        PreferenceMenu(
                            icon = R.drawable.baseline_error_24,
                            title = "Style",
                            subtitle = "Select complication style",
                            stateCallback = {
                                value -> savePreference(context,"complication_style", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                            },
                            items = listOf(
                                "Temperature only",
                                "Condition only",
                                "Temperature and condition"
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
                                "Kelvin",
                                "Celsius",
                                "Fahrenheit"
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
                            defaultText = launchPackage
                        )

                        PreferenceMenu(
                            icon = R.drawable.baseline_error_24,
                            title = "Allow longer complication text",
                            subtitle = "Enable if text is getting cut off. May cause unexpected results",
                            stateCallback = {
                                value -> savePreference(context,"complication_trim_to_fit", value)
                                SmartspacerComplicationProvider.notifyChange(context, GenericWeatherComplication::class.java)
                            },
                            items = listOf(
                                "Enabled",
                                "Disabled"
                            )
                        )
                    }
                }
            }
        }
    }
}

