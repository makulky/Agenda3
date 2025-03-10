package com.example.agenda3.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "agenda.db"
        private const val DATABASE_VERSION = 2
        private const val TABLE_USERS = "users"
        private const val TABLE_CONTACTS = "contacts"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"

        private const val COLUMN_CONTACT_ID = "contact_id"
        private const val COLUMN_CONTACT_NAME = "contact_name"
        private const val COLUMN_CONTACT_SURNAME = "contact_surname"
        private const val COLUMN_CONTACT_PHONE = "contact_phone"
        private const val COLUMN_USER_ID = "user_id"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createUserTable = """
        CREATE TABLE IF NOT EXISTS $TABLE_USERS (
            $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_NAME TEXT NOT NULL,
            $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
            $COLUMN_PASSWORD TEXT NOT NULL
        )
    """.trimIndent()

        val createContactTable = """
        CREATE TABLE IF NOT EXISTS $TABLE_CONTACTS (
            $COLUMN_CONTACT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_CONTACT_NAME TEXT NOT NULL,
            $COLUMN_CONTACT_SURNAME TEXT NOT NULL,
            $COLUMN_CONTACT_PHONE TEXT NOT NULL,
            $COLUMN_USER_ID INTEGER NOT NULL,
            FOREIGN KEY ($COLUMN_USER_ID) REFERENCES $TABLE_USERS($COLUMN_ID)
        )
    """.trimIndent()

        db.execSQL(createUserTable)
        db.execSQL(createContactTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CONTACTS")
        onCreate(db)
    }


    fun registerUser(name: String, email: String, password: String, context: Context): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, name)
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)  // ⚠️ En producción, usa cifrado
        }

        return try {
            val result = db.insert(TABLE_USERS, null, values)
            db.close()
            if (result == -1L) {
                Toast.makeText(context, "Error al registrar", Toast.LENGTH_SHORT).show()
                false
            } else {
                Toast.makeText(context, "Registro exitoso", Toast.LENGTH_SHORT).show()
                true
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun loginUser(email: String, password: String, context: Context): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USERS WHERE $COLUMN_EMAIL = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(email, password))

        return if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putInt("user_id", userId)
                apply()
            }
            cursor.close()
            true
        } else {
            cursor.close()
            Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun addContact(
        userId: Int,
        name: String,
        surname: String,
        phone: String,
        context: Context
    ): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CONTACT_NAME, name)
            put(COLUMN_CONTACT_SURNAME, surname)
            put(COLUMN_CONTACT_PHONE, phone)
            put(COLUMN_USER_ID, userId)
        }

        return try {
            val result = db.insert(TABLE_CONTACTS, null, values)
            db.close()
            if (result == -1L) {
                Toast.makeText(context, "Error al agregar contacto", Toast.LENGTH_SHORT).show()
                false
            } else {
                Toast.makeText(context, "Contacto agregado", Toast.LENGTH_SHORT).show()
                true
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            false
        }
    }

    fun getContacts(userId: Int): List<Contact> {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_CONTACTS WHERE $COLUMN_USER_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        val contacts = mutableListOf<Contact>()

        if (cursor.moveToFirst()) {
            do {
                val contact = Contact(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_SURNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE))
                )
                contacts.add(contact)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return contacts
    }

    fun getContactById(contactId: Int): Contact? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_CONTACTS WHERE $COLUMN_CONTACT_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(contactId.toString()))

        return if (cursor.moveToFirst()) {
            val contact = Contact(
                cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_NAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_SURNAME)),
                cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CONTACT_PHONE))
            )
            cursor.close()
            contact
        } else {
            cursor.close()
            null
        }
    }


}

data class Contact(val id: Int, val name: String, val surname: String, val phone: String)
