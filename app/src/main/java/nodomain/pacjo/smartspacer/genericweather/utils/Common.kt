package nodomain.pacjo.smartspacer.genericweather.utils

import android.content.Context
import nodomain.pacjo.smartspacer.genericweather.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

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