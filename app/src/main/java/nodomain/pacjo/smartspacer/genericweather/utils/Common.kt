package nodomain.pacjo.smartspacer.genericweather.utils

import android.content.Context
import android.util.Log
import nodomain.pacjo.smartspacer.genericweather.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat

fun isFirstRun(context: Context) {
    val file = File(context.filesDir, "data.json")

    // If file doesn't exist, so
    //   - it's the first run after installation / data reset
    //   - something went wrong, but we can blame that on the user
    if (!file.exists()) {
        val outputFile = File(context.filesDir, "data.json")         // TODO: don't hardcode

        val outputStream: OutputStream = FileOutputStream(outputFile)
        context.resources.openRawResource(R.raw.default_data).use { input ->
            outputStream.use { output ->
                input.copyTo(output)
            }
        }
    }
}

fun convertTimeTo(timeInMilliseconds: Long, shortStyle: Boolean = false): String {
    val hours = timeInMilliseconds / 3600000
    val minutes = timeInMilliseconds % 3600 / 60

    val hoursWord = when (shortStyle) {
        true -> "hrs"
        else -> "hours"
    }

    val minutesWord = when (shortStyle) {
        true -> "m"
        else -> "minutes"
    }

    return if (hours > 0 && minutes > 0) {
        "$hours $hoursWord and $minutes $minutesWord"
    } else if (hours > 0) {
        "$hours $hoursWord"
    } else if (minutes > 0) {
        "$minutes $minutesWord"
    } else {
        "less than a minute"
    }
}

fun SimpleDateFormatWrapper(timeInMilliseconds: Long, shortStyle: Boolean = false): String {
    // probably should add check for number of days
    return if (shortStyle) {
        when (SimpleDateFormat("H").format(timeInMilliseconds).toInt() > 0) {
            true -> SimpleDateFormat("H 'hrs' mm 'm'").format(timeInMilliseconds)
            else -> SimpleDateFormat("mm 'm'").format(timeInMilliseconds)
        }
    } else {
        when (SimpleDateFormat("H").format(timeInMilliseconds).toInt() > 0) {
            true -> SimpleDateFormat("H 'hours' mm 'minutes'").format(timeInMilliseconds)
            else -> SimpleDateFormat("mm 'minutes'").format(timeInMilliseconds)
        }
    }
}