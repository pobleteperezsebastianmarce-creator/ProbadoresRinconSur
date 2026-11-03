package com.example.rinconsur.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [Trabajador::class, Tienda::class, InformeEntity::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trabajadorDao(): TrabajadorDao
    abstract fun tiendaDao(): TiendaDao
    abstract fun informeDao(): InformeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val seedCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)

                // 1. Tiendas iniciales
                db.execSQL(
                    "INSERT OR IGNORE INTO tiendas (id, nombre, categoria, tieneProbadores) VALUES " +
                            "(1, 'Rincón Sur Costanera', 'Moda', 1), " +
                            "(2, 'Rincón Sur Parque Arauco', 'Accesorios', 0)"
                )

                // 2. Trabajadores iniciales
                db.execSQL(
                    "INSERT OR IGNORE INTO trabajadores (id, nombre, apellido, usuario, clave, tienda, claveProvisoria, rol) " +
                            "VALUES (1, 'Administrador', 'General', 'admin', 'admin123', 'Rincón Sur Costanera', 0, 'ADMINISTRADOR')"
                )
                db.execSQL(
                    "INSERT OR IGNORE INTO trabajadores (id, nombre, apellido, usuario, clave, tienda, claveProvisoria, rol) " +
                            "VALUES (2, 'Carlos', 'Pérez', 'cperez', '1234', 'Rincón Sur Costanera', 0, 'TRABAJADOR')"
                )

                // 3. Registro histórico inicial
                db.execSQL(
                    "INSERT OR IGNORE INTO informes_historial (id, usuario, nombreTrabajador, tienda, seccion, fechaMillis, probadoresUsados, excesosTiempo, personasIngresaron) " +
                            "VALUES (1, 'cperez', 'Carlos Pérez', 'Rincón Sur Costanera', 'Hombres', 1730000000000, 8, 2, 10)"
                )
            }
        }

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(lock = this) {
                INSTANCE ?: Room.databaseBuilder(
                    context = context.applicationContext,
                    klass = AppDatabase::class.java,
                    name = "rinconsur.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(seedCallback)
                    .build().also { INSTANCE = it }
            }
    }
}