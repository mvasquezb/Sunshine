package com.example.android.sunshine.sync

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.provider.BaseColumns
import com.example.android.sunshine.data.WeatherContract

/**
 * Created by pmvb on 17-08-25.
 */
object SunshineSyncUtils {

    private var sInitialized = false

    @Synchronized
    fun initialize(context: Context) {
        if (sInitialized) {
            return
        }

        val queryTask = object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                val projection = arrayOf(BaseColumns._ID)
                val selection = WeatherContract.WeatherEntry.getSelectFromTodayOnwards()

                val result = context.contentResolver.query(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        projection,
                        selection,
                        null, null
                )

                if (result == null || result.count == 0) {
                    startImmediateSync(context)
                }

                result.close()
            }
        }
        queryTask.execute()

        sInitialized = true
    }

    fun startImmediateSync(context: Context) {
        val syncService = Intent(context, SunshineSyncService::class.java)
        context.startService(syncService)
    }
}