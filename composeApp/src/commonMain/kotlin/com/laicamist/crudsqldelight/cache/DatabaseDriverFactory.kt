package com.laicamist.crudsqldelight.cache

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.db.SqlDriver
import cache.ListarPersonasFisicas
import cache.ListarPersonasMorales
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

interface DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}

internal class Database(databaseDriverFactory : DatabaseDriverFactory) {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    internal fun insertarDomicilio(
        cp: String, estado: String, municipio: String, localidad: String,
        colonia: String, tipoVialidad: String, calle: String,
        nExt: String, nInt: String?, e1: String, e2: String,
        ref: String, carac: String
    ) {
        dbQuery.insertarDomicilio(cp, estado, municipio, localidad, colonia, tipoVialidad, calle, nExt, nInt, e1, e2, ref, carac)
    }

    internal fun obtenerUltimoId(): Long = dbQuery.obtenerUltimoId().executeAsOne()

    internal fun insertarPersonaFisica(
        rfc: String, curp: String, nombre: String, apellidos: String,
        fechaNac: String, email: String, tel: String,
        actEcon: String, regFiscal: String, idDom: Long
    ) {
        dbQuery.insertarPersonaFisica(rfc, curp, nombre, apellidos, fechaNac, email, tel, actEcon, regFiscal, idDom)
    }

    internal fun insertarPersonaMoral(
        rfc: String, razonSocial: String, regimen: String, fechaConst: String,
        rfcRep: String, numEscritura: String, actEcon: String, idDom: Long
    ) {
        dbQuery.insertarPersonaMoral(rfc, razonSocial, regimen, fechaConst, rfcRep, numEscritura, actEcon, idDom)
    }

    internal fun insertarSocio(idMoral: Long, rfcSocio: String) {
        dbQuery.insertarSocio(idMoral, rfcSocio)
    }

    internal fun consultarFisicaPorRFC(rfc: String) = dbQuery.consultarFisicaPorRFC(rfc).executeAsOneOrNull()

    internal fun consultarMoralPorRFC(rfc: String) = dbQuery.consultarMoralPorRFC(rfc).executeAsOneOrNull()

    internal fun consultarSociosPorRFCEmpresa(rfcEmpresa: String) = dbQuery.consultarSociosPorRFCEmpresa(rfcEmpresa).executeAsList()

    internal fun listarTodasLasFisicas(): Flow<List<ListarPersonasFisicas>> {
        return dbQuery.listarPersonasFisicas()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    internal fun listarTodasLasMorales(): Flow<List<ListarPersonasMorales>> {
        return dbQuery.listarPersonasMorales()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    internal fun existeRFCFisica(rfc: String): Boolean = dbQuery.existeRFCFisica(rfc).executeAsOne() > 0

    internal fun existeRFCMoral(rfc: String): Boolean = dbQuery.existeRFCMoral(rfc).executeAsOne() > 0

    internal fun actualizarContactoFisica(rfc: String, nuevoEmail: String, nuevoTel: String) {
        dbQuery.actualizarContactoFisica(nuevoEmail, nuevoTel, rfc)
    }

    internal fun actualizarDatosEmpresa(rfc: String, razonSocial: String, regimen: String, actividad: String) {
        dbQuery.actualizarDatosEmpresa(razonSocial, regimen, actividad, rfc)
    }

    internal fun actualizarDomicilio(
        idDom: Long, cp: String, edo: String, mun: String, loc: String,
        col: String, vialidad: String, calle: String, nExt: String,
        nInt: String?, e1: String, e2: String, ref: String, carac: String
    ) {
        dbQuery.actualizarDomicilio(cp, edo, mun, loc, col, vialidad, calle, nExt, nInt, e1, e2, ref, carac, idDom)
    }

    internal fun eliminarPersonaFisicaCompleta(rfc: String) {
        database.transaction {
            val idDom = dbQuery.obtenerIdsParaBorrarFisica(rfc).executeAsOne()
            dbQuery.eliminarPersonaFisica(rfc)
            dbQuery.eliminarDomicilioPorId(idDom)
        }
    }

    internal fun eliminarPersonaMoralCompleta(rfc: String) {
        database.transaction {
            val ids = dbQuery.obtenerIdsParaBorrarMoral(rfc).executeAsOne()
            dbQuery.eliminarSociosPorEmpresa(ids.id)
            dbQuery.eliminarPersonaMoral(rfc)
            dbQuery.eliminarDomicilioPorId(ids.idDomicilio)
        }
    }
}