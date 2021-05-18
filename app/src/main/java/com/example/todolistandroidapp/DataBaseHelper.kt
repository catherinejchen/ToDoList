package com.example.todolistandroidapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.*

val DATABASENAME = "MY DATABASE"

class DataBaseHelper(var context: Context) : SQLiteOpenHelper(context, DATABASENAME, null,
        1) {
    object FeedEntry : BaseColumns {
        const val TABLE_NAME = "entries"
        const val COLUMN_NAME_TITLE = "title"
        const val COLUMN_NAME_SUBTITLE = "subtitle"
        const val COLUMN_NAME_DATE = "dueDate"
    }

    private val SQL_CREATE_ENTRIES =
            "CREATE TABLE ${FeedEntry.TABLE_NAME} (" +
                    "${BaseColumns._ID} INTEGER PRIMARY KEY," +
                    "${FeedEntry.COLUMN_NAME_TITLE} TEXT," +
                    "${FeedEntry.COLUMN_NAME_SUBTITLE} TEXT," +
                    "${FeedEntry.COLUMN_NAME_DATE} DATETIME DEFAULT CURRENT_TIMESTAMP)"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${FeedEntry.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        //onCreate(db);
    }

    fun insertData(title: String, subtitle: String) {
        val db = this.writableDatabase
        // Create a new map of values, where column names are the keys
        val values = ContentValues().apply {
            put(FeedEntry.COLUMN_NAME_TITLE, title)
            put(FeedEntry.COLUMN_NAME_SUBTITLE, subtitle)
        }

        // Insert the new row, returning the primary key value of the new row
        db.insert(FeedEntry.TABLE_NAME, null, values)
//        if (result == (0).toLong()) {
//            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
//        }
//        else {
//            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show()
//        }
    }

    fun removeData(id: String) {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM ${FeedEntry.TABLE_NAME} WHERE _id='${id}'")
    }

    fun readData(): MutableList<Pair<String, String>> {
        val list: MutableList<Pair<String, String>> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from ${FeedEntry.TABLE_NAME}"
        val result = db.rawQuery(query, null)
        if (result.moveToFirst()) {
            do {
                list.add(Pair(result.getString(0), result.getString(result.getColumnIndex("${FeedEntry.COLUMN_NAME_TITLE}"))))
            }
            while (result.moveToNext())
        }
        return list
    }
}