package com.example.agenda3.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "agenda.db"   // Nombre de la base de datos
        private const val DATABASE_VERSION = 1          // Versión de la base de datos
        private const val TABLE_USERS = "users"         // Nombre de la tabla
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Crear la tabla users si no existe
        val createTable = """
            CREATE TABLE IF NOT EXISTS $TABLE_USERS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_EMAIL TEXT UNIQUE NOT NULL,
                $COLUMN_PASSWORD TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
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
            // Si se encuentra el usuario, el inicio de sesión es exitoso
            cursor.close()
            true
        } else {
            // Si no se encuentra el usuario, el inicio de sesión falla
            cursor.close()
            Toast.makeText(context, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
            false
        }
    }
}