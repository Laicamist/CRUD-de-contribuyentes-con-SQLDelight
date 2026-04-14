package com.laicamist.crudsqldelight.data

import cache.*
import com.laicamist.crudsqldelight.cache.AppDatabase
import com.laicamist.crudsqldelight.cache.DatabaseDriverFactory
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.laicamist.crudsqldelight.dataClases.DataSeeder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow

class SatRepositoryImpl(databaseDriverFactory: DatabaseDriverFactory) : SatRepository {
    private val database = AppDatabase(databaseDriverFactory.createDriver())
    private val dbQuery = database.appDatabaseQueries

    init {
        // 2. Al arrancar el repositorio, llamamos al Seeder
        // Le pasamos la instancia de 'database' para que haga su magia
        DataSeeder.seed(database)
    }

    //1. ENTRADA DE DATOS (INSERTS)
    override fun insertarDomicilio(
        cp: String, estadoId: Long, municipioId: Long, tipoVialidad: String,
        localidad: String, colonia: String, calle: String,
        numeroExterior: String, numeroInterior: String?, entreCalle1: String,
        entreCalle2: String, referencias: String, caracteristicas: String
    ) {
        dbQuery.insertarDomicilio(
            cp, estadoId, municipioId, tipoVialidad, localidad, colonia,
            calle, numeroExterior, numeroInterior,
            entreCalle1, entreCalle2, referencias, caracteristicas
        )
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

    override fun insertarEstado(id: Long, nombre: String) {
        dbQuery.insertarEstado(id, nombre)
    }

    override fun insertarMunicipio(id: Long, estadoId: Long, nombre: String) {
        dbQuery.insertarMunicipio(id, estadoId, nombre)
    }

    //2. SALIDA DE DATOS (CONSULTAS)
    override fun consultarFisicaPorRFC(rfc: String): ConsultarFisicaPorRFC? =
        dbQuery.consultarFisicaPorRFC(rfc).executeAsOneOrNull()

    override fun consultarMoralPorRFC(rfc: String): ConsultarMoralPorRFC? =
        dbQuery.consultarMoralPorRFC(rfc).executeAsOneOrNull()

    override fun consultarDomicilioPorId(id: Long): ConsultarDomicilioPorId? =
        dbQuery.consultarDomicilioPorId(id).executeAsOneOrNull()

    override fun consultarSociosPorIdEmpresa(idEmpresa: Long): List<String> =
        dbQuery.consultarSociosPorIdEmpresa(idEmpresa).executeAsList()

    override fun existeRFCFisica(rfc: String): Boolean =
        dbQuery.existeRFCFisica(rfc).executeAsOne() > 0

    override fun existeRFCMoral(rfc: String): Boolean =
        dbQuery.existeRFCMoral(rfc).executeAsOne() > 0

    override fun contarEstados(): Long =
        dbQuery.contarEstados().executeAsOne()

    //3.LISTADOS
    override fun listarFisicasBase(): Flow<List<ListarFisicasBase>> =
        dbQuery.listarFisicasBase().asFlow().mapToList(Dispatchers.IO)

    override fun listarMoralesBase(): Flow<List<ListarMoralesBase>> =
        dbQuery.listarMoralesBase().asFlow().mapToList(Dispatchers.IO)

    override fun listarEstados(): Flow<List<Estado>> =
        dbQuery.listarEstados().asFlow().mapToList(Dispatchers.IO)

    override fun listarMunicipiosPorEstado(estadoId: Long): Flow<List<ListarMunicipiosPorEstado>> =
        dbQuery.listarMunicipiosPorEstado(estadoId).asFlow().mapToList(Dispatchers.IO)

    override fun obtenerNombreEstado(id: Long): String? =
        dbQuery.obtenerNombreEstadoPorId(id).executeAsOneOrNull()

    override fun obtenerNombreMunicipio(id: Long): String? =
        dbQuery.obtenerNombreMunicipioPorId(id).executeAsOneOrNull()

    //4. UPDATES
    override fun actualizarPersonaFisica(rfc: String, nuevoEmail: String, nuevoTel: String, actEconomica: String, regFiscal: String) {
        dbQuery.actualizarPersonaFisica(nuevoEmail, nuevoTel, actEconomica, regFiscal, rfc)
    }

    override fun actualizarPersonaMoral(rfc: String, razonSocial: String, regimenCapital: String, actividadEconomica: String, rfcRepresentante: String, numEscritura: String) {
        dbQuery.actualizarPersonaMoral(razonSocial, regimenCapital, actividadEconomica, rfcRepresentante, numEscritura, rfc)
    }

    override fun actualizarDomicilio(
        idDomicilio: Long, cp: String, estadoId: Long, municipioId: Long,
        localidad: String, colonia: String, tipoVialidad: String, calle: String,
        numeroExterior: String, numeroInterior: String?, entreCalle1: String,
        entreCalle2: String, referencias: String, caracteristicas: String
    ) {
        dbQuery.actualizarDomicilio(cp, estadoId, municipioId, localidad, colonia, tipoVialidad, calle, numeroExterior, numeroInterior, entreCalle1, entreCalle2, referencias, caracteristicas, idDomicilio)
    }

    //5. ELIMINACIÓN
    override fun obtenerIdDomicilioFisica(rfc: String): Long? =
        dbQuery.obtenerIdsParaBorrarFisica(rfc).executeAsOneOrNull()

    override fun obtenerIdsParaBorrarMoral(rfc: String): ObtenerIdsParaBorrarMoral? =
        dbQuery.obtenerIdsParaBorrarMoral(rfc).executeAsOneOrNull()

    override fun eliminarSociosPorEmpresa(idEmpresa: Long) {
        dbQuery.eliminarSociosPorEmpresa(idEmpresa)
    }

    override fun eliminarPersonaFisica(rfc: String) {
        database.transaction {
            val idDom = dbQuery.obtenerIdsParaBorrarFisica(rfc).executeAsOneOrNull()
            dbQuery.eliminarPersonaFisica(rfc)
            idDom?.let { dbQuery.eliminarDomicilioPorId(it) }
        }
    }

    override fun eliminarPersonaMoral(rfc: String) {
        database.transaction {
            val ids = dbQuery.obtenerIdsParaBorrarMoral(rfc).executeAsOneOrNull()
            ids?.let {
                dbQuery.eliminarSociosPorEmpresa(it.id)
                dbQuery.eliminarPersonaMoral(rfc)
                dbQuery.eliminarDomicilioPorId(it.idDomicilio)
            }
        }
    }

    override fun eliminarDomicilioPorId(id: Long) {
        dbQuery.eliminarDomicilioPorId(id)
    }

}