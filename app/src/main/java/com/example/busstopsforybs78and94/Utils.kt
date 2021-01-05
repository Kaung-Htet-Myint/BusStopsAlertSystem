package com.example.busstopsforybs78and94

import android.content.Context
import java.io.IOException

class Utils {
    fun getJsonDataFromAsset(context: Context, fileName: String): String {
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return ioException.toString()
        }
        return jsonString
    }
}