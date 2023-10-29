package com.chihiro.skip

import android.app.Application
import android.view.accessibility.AccessibilityEvent
import com.chihiro.skip.accessibility.FastAccessibilityService
import com.chihiro.skip.service.MyAccessibilityService

class MyApp : Application() {
    companion object {
        lateinit var instance: Application
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        FastAccessibilityService.init(
            instance, MyAccessibilityService::class.java, arrayListOf(
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED,
                AccessibilityEvent.TYPE_VIEW_CLICKED
            )
        )
    }
}