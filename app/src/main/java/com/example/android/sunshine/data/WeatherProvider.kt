package com.example.android.sunshine.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.example.android.sunshine.utilities.SunshineDateUtils

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

    override fun bulkInsert(uri: Uri, values: Array<out ContentValues>): Int {
        when(sUriMatcher.match(uri)) {
            CODE_WEATHER -> {
                val db = mDbHelper.writableDatabase
                db.beginTransaction()
                var rowsInserted = 0
                try {
                    values.forEach { value ->
                        val _id = insertUtil(db, value)
                        if (_id != -1.toLong()) {
                            rowsInserted++
                        }
                    }
                    db.setTransactionSuccessful()
                } finally {
                    db.endTransaction()
                }
                if (rowsInserted > 0) {
                    context.contentResolver.notifyChange(uri, null)
                }
                return rowsInserted
            }
            else -> return super.bulkInsert(uri, values)
        }
    }

    override fun insert(uri: Uri, values: ContentValues): Uri? {
        when (sUriMatcher.match(uri)) {
            CODE_WEATHER -> {
                val db = mDbHelper.writableDatabase
                db.beginTransaction()
                try {
                    val _id = insertUtil(db, values)
                    if (_id != -1.toLong()) {
                        context.contentResolver.notifyChange(uri, null)
                    }
                } finally {
                    db.endTransaction()
                }
                return uri
            }
            else -> return null
        }
    }

    private fun insertUtil(db: SQLiteDatabase, values: ContentValues): Long {
        val date = values.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)
        if (SunshineDateUtils.isDateNormalized(date)) {
            throw IllegalArgumentException("Date must be normalized to insert")
        }
        val _id = db.insert(WeatherContract.WeatherEntry.TABLE_NAME, null, values)
        return _id
    }

    override fun update(
            uri: Uri,
            values: ContentValues,
            selection: String?,
            selectionArgs: Array<out String>?
    ): Int {
        TODO("Not yet implemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        val _selection = selection ?: "1"
        var rowsDeleted = 0
        when (sUriMatcher.match(uri)) {
            CODE_WEATHER -> {
                rowsDeleted = mDbHelper.writableDatabase.delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        _selection,
                        selectionArgs
                )
            }
            else -> throw UnsupportedOperationException("Unknown uri: $uri")
        }
        if (rowsDeleted != 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return rowsDeleted
    }

    override fun getType(uri: Uri): String {
        return ""
    }
}