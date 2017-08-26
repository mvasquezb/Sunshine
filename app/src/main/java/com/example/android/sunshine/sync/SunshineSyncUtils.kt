package com.example.android.sunshine.sync

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.AsyncTask
import android.provider.BaseColumns
import com.example.android.sunshine.data.WeatherContract
import com.firebase.jobdispatcher.*
import java.util.concurrent.TimeUnit

/**
 * Created by pmvb on 17-08-25.
 */
object SunshineSyncUtils {

    private val SYNC_INTERVAL_HOURS = 3
    private val SYNC_INTERVAL_SECONDS = TimeUnit.HOURS.toSeconds(SYNC_INTERVAL_HOURS.toLong()).toInt()
    private val SYNC_FLEXTIME_SECONDS = SYNC_INTERVAL_SECONDS / 3

    private val SUNSHINE_SYNC_TAG = "sunshine-sync"

    private var sInitialized = false

    fun scheduleDispatcherSync(context: Context) {
        val driver = GooglePlayDriver(context)
        val jobDispatcher = FirebaseJobDispatcher(driver)
        val syncJob = jobDispatcher.newJobBuilder()
                .setService(SunshineFirebaseJobService::class.java)
                .setTag(SUNSHINE_SYNC_TAG)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        SYNC_INTERVAL_SECONDS,
                        SYNC_INTERVAL_HOURS + SYNC_FLEXTIME_SECONDS
                ))
                .setReplaceCurrent(true)
                .build()
        jobDispatcher.schedule(syncJob)
    }

    @Synchronized
    fun initialize(context: Context) {
        if (sInitialized) {
            return
        }
        sInitialized = true

        scheduleDispatcherSync(context)

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
    }

    fun startImmediateSync(context: Context) {
        val syncService = Intent(context, SunshineSyncService::class.java)
        context.startService(syncService)
    }
}