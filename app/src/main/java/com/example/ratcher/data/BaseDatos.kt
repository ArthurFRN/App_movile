package com.example.ratcher.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ratcher.util.Seguridad

// Base de datos local (SQLite). Aquí se crean las 4 tablas de la app.
class BaseDatos(context: Context) : SQLiteOpenHelper(context, "ratcher.db", null, 1) {

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        // Activa las claves foráneas (para que al borrar un levantamiento se borren sus componentes)
        db.setForeignKeyConstraintsEnabled(true)
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE usuarios (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                correo TEXT NOT NULL UNIQUE,
                cargo TEXT NOT NULL,
                clave TEXT NOT NULL
            )
            """
        )
        db.execSQL(
            """
            CREATE TABLE levantamientos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                rack_id TEXT NOT NULL,
                area TEXT NOT NULL,
                fecha TEXT NOT NULL,
                observaciones TEXT NOT NULL,
                ruta_imagen TEXT NOT NULL,
                responsable TEXT NOT NULL,
                estado TEXT NOT NULL
            )
            """
        )
        db.execSQL(
            """
            CREATE TABLE componentes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                levantamiento_id INTEGER NOT NULL,
                categoria TEXT NOT NULL,
                descripcion TEXT NOT NULL,
                confianza INTEGER NOT NULL,
                estado TEXT NOT NULL,
                categoria_original TEXT NOT NULL,
                FOREIGN KEY (levantamiento_id) REFERENCES levantamientos(id) ON DELETE CASCADE
            )
            """
        )
        db.execSQL(
            """
            CREATE TABLE historial (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                levantamiento_id INTEGER NOT NULL,
                accion TEXT NOT NULL,
                detalle TEXT NOT NULL,
                fecha TEXT NOT NULL,
                usuario TEXT NOT NULL,
                FOREIGN KEY (levantamiento_id) REFERENCES levantamientos(id) ON DELETE CASCADE
            )
            """
        )

        // Usuario de prueba para poder entrar la primera vez
        val valores = ContentValues()
        valores.put("nombre", "Usuario de prueba")
        valores.put("correo", "test@test.com")
        valores.put("cargo", "Inspector técnico")
        valores.put("clave", Seguridad.hash("123456"))
        db.insert("usuarios", null, valores)
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAnterior: Int, versionNueva: Int) {
        db.execSQL("DROP TABLE IF EXISTS historial")
        db.execSQL("DROP TABLE IF EXISTS componentes")
        db.execSQL("DROP TABLE IF EXISTS levantamientos")
        db.execSQL("DROP TABLE IF EXISTS usuarios")
        onCreate(db)
    }

    companion object {
        // Solo debe existir una instancia de la base de datos (singleton)
        @Volatile
        private var instancia: BaseDatos? = null

        fun obtener(context: Context): BaseDatos {
            return instancia ?: synchronized(this) {
                instancia ?: BaseDatos(context.applicationContext).also { instancia = it }
            }
        }
    }
}
