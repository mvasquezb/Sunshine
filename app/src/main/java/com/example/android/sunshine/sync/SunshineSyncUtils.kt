package com.example.android.sunshine.sync

import android.content.Context
import android.content.Intent

/**
 * Created by pmvb on 17-08-25.
 */
object SunshineSyncUtils {
    fun startImmediateSync(context: Context) {
        val syncService = Intent(context, SunshineSyncService::class.java)
        context.startService(syncService)
    }
}