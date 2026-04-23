package com.laicamist.crudsqldelight.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import cache.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

 class Database(databaseDriverFactory : DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    // 1. ENTRADA DE DATOS
    fun getAll(): List<Contribuyente> {
        return dbQuery.selectAll().executeAsList()
    }

    fun getAllStream(): Flow<List<Contribuyente>> {
        return dbQuery.selectAll().asFlow().mapToList(Dispatchers.IO)
    }


    fun insert(contribuyente: Contribuyente) {
        dbQuery.insert(
            contribuyente.tipo,
            contribuyente.curp,
            contribuyente.nombre,
            contribuyente.apellido_paterno,
            contribuyente.apellido_materno,
            contribuyente.fecha_nacimiento,
            contribuyente.fecha_constitucion,
            contribuyente.email,
            contribuyente.telefono,
            contribuyente.razon_social,
            contribuyente.rfc,
            contribuyente.rfc_socios,
            contribuyente.poliza,
            contribuyente.estado_id,
            contribuyente.municipio_id,
            contribuyente.cp,
            contribuyente.actividad_economica,
            contribuyente.regimen_fiscal,
            contribuyente.regimen_capital,
            contribuyente.localidad,
            contribuyente.colonia,
            contribuyente.calle,
            contribuyente.numero_exterior,
            contribuyente.numero_interior,
            contribuyente.tipo_vialidad,
            contribuyente.nombre_vialidad,
            contribuyente.entre_calle1,
            contribuyente.entre_calle2,
            contribuyente.referencia_adicional,
            contribuyente.caracteristicas_domicilio
        )
    }


    fun deleteById(id: Long) {
        dbQuery.deleteByID(id)
    }

    fun delete(contribuyente: Contribuyente) {
        dbQuery.deleteByID(contribuyente.id)
    }


    fun getById(id: Long): Flow<Contribuyente?> {
        return dbQuery.getByID(id).asFlow().mapToOneOrNull(Dispatchers.IO)
    }


    fun getByRFC(rfc: String): Flow<Contribuyente?> {
        return dbQuery.getByRFC(rfc).asFlow().mapToOneOrNull(Dispatchers.IO)
    }


    fun update(contribuyente: Contribuyente) {
        dbQuery.update(
            contribuyente.tipo,
            contribuyente.curp,
            contribuyente.nombre,
            contribuyente.apellido_paterno,
            contribuyente.apellido_materno,
            contribuyente.fecha_nacimiento,
            contribuyente.fecha_constitucion,
            contribuyente.email,
            contribuyente.telefono,
            contribuyente.razon_social,
            contribuyente.rfc,
            contribuyente.rfc_socios,
            contribuyente.poliza,
            contribuyente.estado_id,
            contribuyente.municipio_id,
            contribuyente.cp,
            contribuyente.actividad_economica,
            contribuyente.regimen_fiscal,
            contribuyente.regimen_capital,
            contribuyente.localidad,
            contribuyente.colonia,
            contribuyente.calle,
            contribuyente.numero_exterior,
            contribuyente.numero_interior,
            contribuyente.tipo_vialidad,
            contribuyente.nombre_vialidad,
            contribuyente.entre_calle1,
            contribuyente.entre_calle2,
            contribuyente.referencia_adicional,
            contribuyente.caracteristicas_domicilio,
            contribuyente.id
        )
    }


    fun getEstados(): List<Estado> {
        return dbQuery.getEstados().executeAsList()
    }


    fun getMunicipiosByEstado(estadoId: Long): List<Municipio> {
        return dbQuery.getMunicipiosByEstado(estadoId).executeAsList()
    }

    fun insertEstado(nombre: String) {
        dbQuery.insertEstado(nombre)
    }

    fun insertMunicipio(nombre: String, estadoId: Long) {
        dbQuery.insertMunicipio(nombre, estadoId)
    }
    fun getEstadoByID(id: Long): Estado {
        return dbQuery.getEstadoByID(id).executeAsOne()
    }
    fun getMunicipioByID(id: Long): Municipio {
        return dbQuery.getMunicipioByID(id).executeAsOne()
    }
    fun reiniciarBD() {
        dbQuery.deleteAllContribuyentes()
        dbQuery.deleteAllMunicipios()
        dbQuery.deleteAllEstados()
    }
}