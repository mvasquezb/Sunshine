package com.example.android.sunshine.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

/**
 * Created by pmvb on 17-08-22.
 */
class WeatherProvider : ContentProvider() {

    private lateinit var mDbHelper: WeatherDbHelper

    companion object {
        val CODE_WEATHER = 100
        val CODE_WEATHER_WITH_DATE = 101
        val sUriMatcher = buildUriMatcher()

        @JvmStatic
        fun buildUriMatcher(): UriMatcher {
            val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)
            uriMatcher.addURI(
                    WeatherContract.CONTENT_AUTHORITY,
                    WeatherContract.WeatherEntry.PATH_WEATHER,
                    CODE_WEATHER
            )
            uriMatcher.addURI(
                    WeatherContract.CONTENT_AUTHORITY,
                    "${WeatherContract.WeatherEntry.PATH_WEATHER}/#",
                    CODE_WEATHER_WITH_DATE
            )
            return uriMatcher
        }
    }

    override fun onCreate(): Boolean {
        mDbHelper = WeatherDbHelper(context)

        return true
    }

    override fun bulkInsert(uri: Uri?, values: Array<out ContentValues>?): Int {
        return super.bulkInsert(uri, values)
    }

    override fun insert(uri: Uri, values: ContentValues): Uri {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
    ): Cursor {
        var cursor: Cursor
        val match = sUriMatcher.match(uri)
        when (match) {
            CODE_WEATHER -> {
                val db = mDbHelper.readableDatabase
                cursor = db.query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                )
            }
            CODE_WEATHER_WITH_DATE -> {
                val db = mDbHelper.readableDatabase
                cursor = db.query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        "${WeatherContract.WeatherEntry.COLUMN_DATE} = ?",
                        arrayOf<String>(uri.lastPathSegment),
                        null,
                        null,
                        sortOrder
                )
            }
            else -> {
                throw UnsupportedOperationException("Unknown uri: $uri")
            }
        }
        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun update(
            uri: Uri,
            values: ContentValues,
            selection: String?,
            selectionArgs: Array<out String>?
    ): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(uri: Uri): String {
        return ""
    }
}