package com.example.bmi

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, dbName, null, 1) {

    companion object{
        val dbName: String = "BmiDatabase.db"
        val historyTableName = "history"
        val historyColId = "id"
        val historyColMass = "mass"
        val historyColHeight = "height"
        val historyColUnitType = "unitType"
        val historyColDate = "date"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $historyTableName (" +
                    "$historyColId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$historyColMass REAL, " +
                    "$historyColHeight REAL, " +
                    "$historyColUnitType INTEGER, " +
                    "$historyColDate TEXT)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE $historyTableName")
        onCreate(db)
    }

    fun insertHistoryItem(mass: Double, height: Double, unitType: Int, date: String){
        val contentValues: ContentValues = ContentValues()
        contentValues.put(historyColMass, mass)
        contentValues.put(historyColHeight, height)
        contentValues.put(historyColUnitType, unitType)
        contentValues.put(historyColDate, date)
        writableDatabase.insert(historyTableName, null, contentValues)
    }

    fun numOfRows(): Int{
        return DatabaseUtils.queryNumEntries(readableDatabase, historyTableName).toInt()
    }

    fun deleteOldestItem(){
        val curs = readableDatabase.rawQuery(
            "SELECT $historyColId FROM $historyTableName ORDER BY $historyColId ASC LIMIT 1", null
        )
        curs.moveToFirst()
        val id = curs.getInt(curs.getColumnIndex(historyColId))
        writableDatabase.delete(historyTableName, "$historyColId = ?",  arrayOf(id.toString()))
    }

    fun getHistory(): MutableList<HistoryItemData> {
        val list = ArrayList<HistoryItemData>()
        val curs = readableDatabase.rawQuery(
            "SELECT * FROM $historyTableName", null
        )
        with(curs){
            while (moveToNext()){
                list.add(
                    HistoryItemData(
                        getDouble(getColumnIndex(historyColHeight)),
                        getDouble(getColumnIndex(historyColMass)),
                        BMICalc.UnitType.fromOrdinal(getInt(getColumnIndex(historyColUnitType)))!!,
                        getString(getColumnIndex(historyColDate))
                    )
                )
            }
        }
        return list
    }
}