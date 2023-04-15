package com.example.customermanagement


import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/*
Name: DaHye Baker
ID: 30063368
Assessment 2: Activity 2
*/

class DBHelper(context: Context) :
    SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {
    private var context: Context

    // Save context parameter object
    init {
        this.context = context
    }

    // Create companion objects for database and app
    companion object {
        private const val DB_NAME = "smtbiz"
        private const val DB_VERSION = 1
        const val TABLE_NAME = "customer"
        const val ID = "id"
        const val NAME = "name"
        const val EMAIL = "email"
        const val MOBILE = "mobile"
    }

    // Create database
    override fun onCreate(db: SQLiteDatabase?) {
        val query = (
                "CREATE TABLE $TABLE_NAME (" +
                        "$ID INTEGER PRIMARY KEY," +
                        "$NAME TEXT," +
                        "$EMAIL TEXT," +
                        "$MOBILE TEXT," +
                        "CHECK(length($ID) == 6)" + ")"
                )
        db?.execSQL(query) // nullable
    }

    // Database upgrade
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int){
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME") // non-null assertion. Error if null at compile time
        onCreate(db)
    }

    // Add a customer record in DB
    fun addCustomer(id: Int, name: String, email: String, mobile: String) {
        // This ContentValues class is used to store a set of values
        val values = ContentValues()
        // insert key-value pairs
        values.put(ID, id)
        values.put(NAME, name)
        values.put(EMAIL, email)
        values.put(MOBILE, mobile)
        // create a writable DB variable of our database to insert record
        val db = this.writableDatabase
        // insert all values into DB
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    // Get customer records from database
    fun findCustomerRecords(action: String = "", searchItem: String = "", COLUMN: String = ""): ArrayList<Customer> {
        val db = this.readableDatabase

        val cursor: Cursor = when (action){
            "search" -> {
                db.rawQuery("SELECT * FROM $TABLE_NAME WHERE LOWER($COLUMN) = LOWER(?)", arrayOf(
                    searchItem.lowercase()
                ))
            }
            else -> {
                db.rawQuery("SELECT * FROM $TABLE_NAME", null)
            }
        }

        // read all records from DB and get the cursor

        val customerList = ArrayList<Customer>()
        if (cursor.moveToFirst()) {
            do {
                customerList.add(
                    Customer(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(MOBILE))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return customerList
    }

    // Delete an existing customer by ID
    fun deleteCustomer(id: String): Int {
        // create a writable DB variable of our database to delete record
        val db = this.writableDatabase
        // delete a customer by NAME
        val rows = db.delete(TABLE_NAME, "id=?", arrayOf(id))
        db.close()
        return rows
    }

    // Update a customer’s info by ID
    fun updateCustomer(id: String, name: String, email: String, mobile: String): Int {
        // create a writable DB variable of our database to update record
        val db = this.writableDatabase
        // ContentValues class stores a set of values
        val values = ContentValues()
        values.put(NAME, name)
        values.put(EMAIL, email)
        values.put(MOBILE, mobile)
        val rows = db.update(TABLE_NAME, values, "id=?", arrayOf(id))
        db.close()
        return rows
    }

    // Delete database, return boolean
    fun deleteDB(): Boolean {
        return context.deleteDatabase(DB_NAME)
    }
}