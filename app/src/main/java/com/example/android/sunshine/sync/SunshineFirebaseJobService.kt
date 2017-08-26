package com.example.android.sunshine.sync

import android.os.AsyncTask
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

/**
 * Created by pmvb on 17-08-26.
 */
class SunshineFirebaseJobService : JobService() {

    private var mFetchWeatherTask: AsyncTask<Unit, Unit, Unit>? = null

    override fun onStartJob(job: JobParameters): Boolean {
        mFetchWeatherTask = object : AsyncTask<Unit, Unit, Unit>() {
            override fun doInBackground(vararg params: Unit?) {
                SunshineSyncUtils.startImmediateSync(applicationContext)
            }

            override fun onPostExecute(result: Unit?) {
                super.onPostExecute(result)

                jobFinished(job, false)
            }
        }
        mFetchWeatherTask?.execute()
        return true
    }

    override fun onStopJob(job: JobParameters?): Boolean {
        mFetchWeatherTask?.cancel(true)
        return true
    }
}