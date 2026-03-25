package com.example.controldeacceso

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "ControlAcceso.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_USUARIOS = "usuarios"
        const val COLUMN_ID = "id"
        const val COLUMN_USER = "usuario"
        const val COLUMN_PASSWORD = "password"

        const val TABLE_PERSONAS = "personas"
        const val COLUMN_PER_ID = "id"
        const val COLUMN_PER_NOMBRE = "nombre"
        const val COLUMN_PER_DNI = "dni"
        const val COLUMN_PER_CARGO = "cargo"

        const val TABLE_ACCESOS = "accesos"
        const val COLUMN_ACC_ID = "id"
        const val COLUMN_ACC_PERSONA_ID = "persona_id"
        const val COLUMN_ACC_TIPO = "tipo"
        const val COLUMN_ACC_FECHA = "fecha"

        private const val CREATE_TABLE_USUARIOS = ("CREATE TABLE " + TABLE_USUARIOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_USER + " TEXT UNIQUE,"
                + COLUMN_PASSWORD + " TEXT" + ")")

        private const val CREATE_TABLE_PERSONAS = ("CREATE TABLE " + TABLE_PERSONAS + "("
                + COLUMN_PER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_PER_NOMBRE + " TEXT,"
                + COLUMN_PER_DNI + " TEXT UNIQUE,"
                + COLUMN_PER_CARGO + " TEXT" + ")")

        private const val CREATE_TABLE_ACCESOS = ("CREATE TABLE " + TABLE_ACCESOS + "("
                + COLUMN_ACC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_ACC_PERSONA_ID + " INTEGER,"
                + COLUMN_ACC_TIPO + " TEXT,"
                + COLUMN_ACC_FECHA + " DATETIME DEFAULT CURRENT_TIMESTAMP,"
                + "FOREIGN KEY($COLUMN_ACC_PERSONA_ID) REFERENCES $TABLE_PERSONAS($COLUMN_PER_ID))")
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE_USUARIOS)
        db?.execSQL(CREATE_TABLE_PERSONAS)
        db?.execSQL(CREATE_TABLE_ACCESOS)
        insertarDatosPrueba(db)
    }

    private fun insertarDatosPrueba(db: SQLiteDatabase?) {
        val personas = listOf(
            ContentValues().apply { put(COLUMN_PER_NOMBRE, "Juan Perez"); put(COLUMN_PER_DNI, "12345678"); put(COLUMN_PER_CARGO, "Docente") },
            ContentValues().apply { put(COLUMN_PER_NOMBRE, "Maria Lopez"); put(COLUMN_PER_DNI, "87654321"); put(COLUMN_PER_CARGO, "Personal Administrativo") },
            ContentValues().apply { put(COLUMN_PER_NOMBRE, "Carlos Diaz"); put(COLUMN_PER_DNI, "11223344"); put(COLUMN_PER_CARGO, "Docente") }
        )
        personas.forEach { db?.insert(TABLE_PERSONAS, null, it) }
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USUARIOS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_PERSONAS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACCESOS")
        onCreate(db)
    }

    fun registrarUsuario(usuario: String, contrasena: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USER, usuario)
            put(COLUMN_PASSWORD, contrasena)
        }
        val result = db.insert(TABLE_USUARIOS, null, values)
        return result != -1L
    }

    fun validarUsuario(usuario: String, contrasena: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIOS WHERE $COLUMN_USER = ? AND $COLUMN_PASSWORD = ?"
        val cursor = db.rawQuery(query, arrayOf(usuario, contrasena))
        val existe = cursor.count > 0
        cursor.close()
        return existe
    }

    fun buscarPersonas(filtro: String): List<Persona> {
        val lista = mutableListOf<Persona>()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_PERSONAS WHERE $COLUMN_PER_NOMBRE LIKE ? OR $COLUMN_PER_DNI LIKE ?"
        val cursor = db.rawQuery(query, arrayOf("%$filtro%", "%$filtro%"))
        if (cursor.moveToFirst()) {
            do {
                lista.add(Persona(
                    cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PER_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PER_NOMBRE)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PER_DNI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PER_CARGO))
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun registrarAcceso(personaId: Int, tipo: String): Boolean {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_ACC_PERSONA_ID, personaId)
            put(COLUMN_ACC_TIPO, tipo)
        }
        val result = db.insert(TABLE_ACCESOS, null, values)
        return result != -1L
    }

    fun obtenerHistorial(): List<Acceso> {
        val lista = mutableListOf<Acceso>()
        val db = this.readableDatabase
        val query = """
            SELECT a.$COLUMN_ACC_ID, p.$COLUMN_PER_NOMBRE, p.$COLUMN_PER_DNI, p.$COLUMN_PER_CARGO, a.$COLUMN_ACC_TIPO, a.$COLUMN_ACC_FECHA
            FROM $TABLE_ACCESOS a
            INNER JOIN $TABLE_PERSONAS p ON a.$COLUMN_ACC_PERSONA_ID = p.$COLUMN_PER_ID
            ORDER BY a.$COLUMN_ACC_FECHA DESC
        """.trimIndent()
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                lista.add(Acceso(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5)
                ))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return lista
    }

    fun contarAccesosHoy(tipo: String): Int {
        val db = this.readableDatabase
        // Filtramos por el tipo y por la fecha actual (solo la parte YYYY-MM-DD del DATETIME)
        val query = "SELECT COUNT(*) FROM $TABLE_ACCESOS WHERE $COLUMN_ACC_TIPO = ? AND date($COLUMN_ACC_FECHA) = date('now', 'localtime')"
        val cursor = db.rawQuery(query, arrayOf(tipo))
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        return count
    }

    fun contarAlertasHoy(): Int {
        // Ejemplo: Alertas si hay más de 50 registros hoy o algún criterio específico.
        // Por ahora devolveremos 0 o una lógica de ejemplo.
        return 0
    }
}