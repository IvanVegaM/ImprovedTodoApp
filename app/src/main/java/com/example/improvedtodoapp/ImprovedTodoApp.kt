package com.example.improvedtodoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import com.google.android.material.color.DynamicColors

@HiltAndroidApp
class ImprovedTodoApp: Application() {
    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
    }
}