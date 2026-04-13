package com.laicamist.crudsqldelight.data

import cache.ConsultarFisicaPorRFC
import cache.ConsultarMoralPorRFC
import cache.ListarPersonasFisicas
import cache.ListarPersonasMorales
import cache.ObtenerIdsParaBorrarMoral
import com.laicamist.crudsqldelight.cache.AppDatabase
import com.laicamist.crudsqldelight.cache.DatabaseDriverFactory
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

class SatRepositoryImpl(databaseDriverFactory: DatabaseDriverFactory) : SatRepository {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries
    // --- INSERTS ---
    override fun insertarDomicilio(
        cp: String, estado: String, municipio: String, localidad: String,
        colonia: String, tipoVialidad: String, calle: String,
        numeroExterior: String, numeroInterior: String?, entreCalle1: String,
        entreCalle2: String, referencias: String, caracteristicas: String
    ) {
        dbQuery.insertarDomicilio(cp, estado, municipio, localidad, colonia, tipoVialidad, calle, numeroExterior, numeroInterior, entreCalle1, entreCalle2, referencias, caracteristicas)
    }

    override fun obtenerUltimoId(): Long = dbQuery.obtenerUltimoId().executeAsOne()

    override fun insertarPersonaFisica(
        rfc: String, curp: String, nombre: String, apellidos: String,
        fechaDeNacimiento: String, email: String, telefono: String,
        actEconomica: String, regFiscal: String, idDomicilio: Long
    ) {
        dbQuery.insertarPersonaFisica(rfc, curp, nombre, apellidos, fechaDeNacimiento, email, telefono, actEconomica, regFiscal, idDomicilio)
    }

    override fun insertarPersonaMoral(
        rfc: String, denominacionORazonSocial: String, regimenCapital: String,
        fechaDeConstitucion: String, rfcDelRepresentante: String,
        numEscrituraOpoliza: String, actividadEconomica: String, idDomicilio: Long
    ) {
        dbQuery.insertarPersonaMoral(rfc, denominacionORazonSocial, regimenCapital, fechaDeConstitucion, rfcDelRepresentante, numEscrituraOpoliza, actividadEconomica, idDomicilio)
    }

    override fun insertarSocio(idPersonaMoral: Long, rfcSocio: String) {
        dbQuery.insertarSocio(idPersonaMoral, rfcSocio)
    }

    // --- CONSULTAS Y LISTADOS ---
    override fun consultarFisicaPorRFC(rfc: String): ConsultarFisicaPorRFC? =
        dbQuery.consultarFisicaPorRFC(rfc).executeAsOneOrNull()

    override fun consultarMoralPorRFC(rfc: String): ConsultarMoralPorRFC? =
        dbQuery.consultarMoralPorRFC(rfc).executeAsOneOrNull()

    override fun consultarSociosPorRFCEmpresa(rfcEmpresa: String): List<String> =
        dbQuery.consultarSociosPorRFCEmpresa(rfcEmpresa).executeAsList()

    override fun existeRFCFisica(rfc: String): Boolean =
        dbQuery.existeRFCFisica(rfc).executeAsOne() > 0

    override fun existeRFCMoral(rfc: String): Boolean =
        dbQuery.existeRFCMoral(rfc).executeAsOne() > 0

    override fun listarTodasLasFisicas(): Flow<List<ListarPersonasFisicas>> {
        return dbQuery.listarPersonasFisicas()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    override fun listarTodasLasMorales(): Flow<List<ListarPersonasMorales>> {
        return dbQuery.listarPersonasMorales()
            .asFlow()
            .mapToList(Dispatchers.IO)
    }

    // --- UPDATES ---
    override fun actualizarContactoFisica(rfc: String, nuevoEmail: String, nuevoTel: String) {
        dbQuery.actualizarContactoFisica(nuevoEmail, nuevoTel, rfc)
    }

    override fun actualizarDatosEmpresa(rfc: String, denominacionORazonSocial: String, regimenCapital: String, actividadEconomica: String) {
        dbQuery.actualizarDatosEmpresa(denominacionORazonSocial, regimenCapital, actividadEconomica, rfc)
    }

    override fun actualizarDomicilio(
        idDomicilio: Long, cp: String, estado: String, municipio: String,
        localidad: String, colonia: String, tipoVialidad: String, calle: String,
        numeroExterior: String, numeroInterior: String?, entreCalle1: String,
        entreCalle2: String, referencias: String, caracteristicas: String
    ) {
        dbQuery.actualizarDomicilio(cp, estado, municipio, localidad, colonia, tipoVialidad, calle, numeroExterior, numeroInterior, entreCalle1, entreCalle2, referencias, caracteristicas, idDomicilio)
    }

    // --- ELIMINACIÓN CON LÓGICA DE NEGOCIO ---

    override fun obtenerIdDomicilioFisica(rfc: String): Long? =
        dbQuery.obtenerIdsParaBorrarFisica(rfc).executeAsOneOrNull()

    override fun obtenerIdsParaBorrarMoral(rfc: String): ObtenerIdsParaBorrarMoral? =
        dbQuery.obtenerIdsParaBorrarMoral(rfc).executeAsOneOrNull()

    override fun eliminarSociosPorEmpresa(idEmpresa: Long) {
        dbQuery.eliminarSociosPorEmpresa(idEmpresa)
    }

    override fun eliminarPersonaFisica(rfc: String) {
        database.transaction {
            // FÍSICA: Regresa 1 columna -> Dato directo (Sin paréntesis)
            val idDom = dbQuery.obtenerIdsParaBorrarFisica(rfc).executeAsOne()
            dbQuery.eliminarPersonaFisica(rfc)
            dbQuery.eliminarDomicilioPorId(idDom)
        }
    }

    override fun eliminarPersonaMoral(rfc: String) {
        database.transaction {
            // MORAL: Regresa 2 columnas -> Data Class (Con paréntesis)
            val (idEmpresa, idDom) = dbQuery.obtenerIdsParaBorrarMoral(rfc).executeAsOne()
            dbQuery.eliminarSociosPorEmpresa(idEmpresa)
            dbQuery.eliminarPersonaMoral(rfc)
            dbQuery.eliminarDomicilioPorId(idDom)
        }
    }

    override fun eliminarDomicilioPorId(id: Long) {
        dbQuery.eliminarDomicilioPorId(id)
    }
}