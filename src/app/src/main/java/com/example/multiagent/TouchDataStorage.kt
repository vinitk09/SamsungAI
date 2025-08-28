package com.example.multiagent

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

class TouchDataStorage(private val context: Context) {
    private val tag = "TouchDataStorage"
    private val fileName = "touch_events.json"
    private val gson = Gson()
    private val maxEvents = 1000

    fun saveTouchEvent(event: TouchEvent) {
        try {
            val allEvents = loadAllTouchEvents().toMutableList()
            allEvents.add(event)
            val eventsToSave = if (allEvents.size > maxEvents) {
                allEvents.takeLast(maxEvents)
            } else {
                allEvents
            }
            val jsonString = gson.toJson(eventsToSave)
            File(context.filesDir, fileName).writeText(jsonString)
        } catch (e: Exception) {
            Log.e(tag, "Error saving touch event: ${e.message}")
        }
    }

    fun loadAllTouchEvents(): List<TouchEvent> {
        return try {
            val file = File(context.filesDir, fileName)
            if (!file.exists()) return emptyList()
            val jsonString = file.readText()
            val type = object : TypeToken<List<TouchEvent>>() {}.type
            gson.fromJson(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            Log.e(tag, "Error loading touch events: ${e.message}")
            emptyList()
        }
    }
}