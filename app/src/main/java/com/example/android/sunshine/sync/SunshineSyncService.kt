package com.example.android.sunshine.sync

import android.app.IntentService
import android.content.Intent

/**
 * Created by pmvb on 17-08-25.
 */
class SunshineSyncService(name: String) : IntentService(name) {
    override fun onHandleIntent(intent: Intent?) {
        SunshineSyncTask.syncWeather(this)
    }
}