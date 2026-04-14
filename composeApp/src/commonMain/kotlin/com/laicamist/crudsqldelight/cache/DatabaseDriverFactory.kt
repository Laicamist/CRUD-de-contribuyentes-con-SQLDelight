package com.laicamist.crudsqldelight.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import cache.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

internal class Database(databaseDriverFactory : DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries
    // 1. ENTRADA DE DATOS (INSERTS)
    internal fun insertarEstado(id: Long, nombre: String) {
        dbQuery.insertarEstado(id, nombre)
    }

    internal fun insertarMunicipio(id: Long, estadoId: Long, nombre: String) {
        dbQuery.insertarMunicipio(id, estadoId, nombre)
    }

    internal fun insertarDomicilio(
        cp: String, estadoId: Long, municipioId: Long, tipoVialidad: String,
        localidad: String, colonia: String, calle: String,
        numeroExterior: String, numeroInterior: String?, entreCalle1: String,
        entreCalle2: String, referencias: String, caracteristicas: String
    ) {
        dbQuery.insertarDomicilio(
            cp, estadoId, municipioId, tipoVialidad, localidad, colonia,
            calle, numeroExterior, numeroInterior, entreCalle1, entreCalle2,
            referencias, caracteristicas
        )
    }

    internal fun obtenerUltimoId(): Long = dbQuery.obtenerUltimoId().executeAsOne()

    internal fun insertarPersonaFisica(
        rfc: String, curp: String, nombre: String, apellidos: String,
        fechaDeNacimiento: String, email: String, telefono: String,
        actEconomica: String, regFiscal: String, idDomicilio: Long
    ) {
        dbQuery.insertarPersonaFisica(rfc, curp, nombre, apellidos, fechaDeNacimiento, email, telefono, actEconomica, regFiscal, idDomicilio)
    }

    internal fun insertarPersonaMoral(
        rfc: String, denominacionORazonSocial: String, regimenCapital: String,
        fechaDeConstitucion: String, rfcDelRepresentante: String,
        numEscrituraOpoliza: String, actividadEconomica: String, idDomicilio: Long
    ) {
        dbQuery.insertarPersonaMoral(rfc, denominacionORazonSocial, regimenCapital, fechaDeConstitucion, rfcDelRepresentante, numEscrituraOpoliza, actividadEconomica, idDomicilio)
    }

    internal fun insertarSocio(idPersonaMoral: Long, rfcSocio: String) {
        dbQuery.insertarSocio(idPersonaMoral, rfcSocio)
    }

    // 2. CONSULTAS
    internal fun consultarFisicaPorRFC(rfc: String): ConsultarFisicaPorRFC? =
        dbQuery.consultarFisicaPorRFC(rfc).executeAsOneOrNull()

    internal fun consultarMoralPorRFC(rfc: String): ConsultarMoralPorRFC? =
        dbQuery.consultarMoralPorRFC(rfc).executeAsOneOrNull()

    internal fun consultarDomicilioPorId(id: Long): ConsultarDomicilioPorId? =
        dbQuery.consultarDomicilioPorId(id).executeAsOneOrNull()

    internal fun consultarSociosPorIdEmpresa(idEmpresa: Long): List<String> =
        dbQuery.consultarSociosPorIdEmpresa(idEmpresa).executeAsList()

    // --- 3. LISTADOS
    internal fun listarFisicasBase(): Flow<List<ListarFisicasBase>> =
        dbQuery.listarFisicasBase().asFlow().mapToList(Dispatchers.IO)

    internal fun listarMoralesBase(): Flow<List<ListarMoralesBase>> =
        dbQuery.listarMoralesBase().asFlow().mapToList(Dispatchers.IO)

    internal fun listarEstados(): Flow<List<Estado>> =
        dbQuery.listarEstados().asFlow().mapToList(Dispatchers.IO)

    internal fun listarMunicipiosPorEstado(estadoId: Long): Flow<List<ListarMunicipiosPorEstado>> =
        dbQuery.listarMunicipiosPorEstado(estadoId).asFlow().mapToList(Dispatchers.IO)

    internal fun obtenerNombreEstadoPorId(id: Long): String? =
        dbQuery.obtenerNombreEstadoPorId(id).executeAsOneOrNull()

    internal fun obtenerNombreMunicipioPorId(id: Long): String? =
        dbQuery.obtenerNombreMunicipioPorId(id).executeAsOneOrNull()

    // --- 4. VALIDACIONES Y UPDATES ---
    internal fun existeRFCFisica(rfc: String): Boolean =
        dbQuery.existeRFCFisica(rfc).executeAsOne() > 0

    internal fun existeRFCMoral(rfc: String): Boolean =
        dbQuery.existeRFCMoral(rfc).executeAsOne() > 0

    internal fun actualizarPersonaFisica(rfc: String, email: String, telefono: String, actEconomica: String, regFiscal: String) {
        dbQuery.actualizarPersonaFisica(email, telefono, actEconomica, regFiscal, rfc)
    }

    internal fun actualizarPersonaMoral(rfc: String, razonSocial: String, regimen: String, actividad: String, rfcRep: String, numEsc: String) {
        dbQuery.actualizarPersonaMoral(razonSocial, regimen, actividad, rfcRep, numEsc, rfc)
    }

    internal fun actualizarDomicilio(
        idDomicilio: Long, cp: String, estadoId: Long, municipioId: Long, localidad: String,
        colonia: String, tipoVialidad: String, calle: String, numeroExterior: String,
        numeroInterior: String?, entreCalle1: String, entreCalle2: String, referencias: String, caracteristicas: String
    ) {
        dbQuery.actualizarDomicilio(cp, estadoId, municipioId, localidad, colonia, tipoVialidad, calle, numeroExterior, numeroInterior, entreCalle1, entreCalle2, referencias, caracteristicas, idDomicilio)
    }

    internal fun contarEstados(): Long =
        dbQuery.contarEstados().executeAsOne()

    //5. ELIMINACIÓN
    internal fun eliminarPersonaFisicaCompleta(rfc: String) {
        database.transaction {
            val idDom = dbQuery.obtenerIdsParaBorrarFisica(rfc).executeAsOneOrNull()
            dbQuery.eliminarPersonaFisica(rfc)
            idDom?.let { dbQuery.eliminarDomicilioPorId(it) }
        }
    }

    internal fun eliminarPersonaMoralCompleta(rfc: String) {
        database.transaction {
            val ids = dbQuery.obtenerIdsParaBorrarMoral(rfc).executeAsOneOrNull()
            ids?.let {
                dbQuery.eliminarSociosPorEmpresa(it.id)
                dbQuery.eliminarPersonaMoral(rfc)
                dbQuery.eliminarDomicilioPorId(it.idDomicilio)
            }
        }
    }
}