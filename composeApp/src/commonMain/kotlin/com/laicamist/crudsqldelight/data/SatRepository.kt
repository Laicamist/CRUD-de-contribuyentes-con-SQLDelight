package com.laicamist.crudsqldelight.data

import cache.ConsultarDomicilioPorId
import cache.ConsultarFisicaPorRFC
import cache.ConsultarMoralPorRFC
import cache.DOMICILIOFISCAL
import cache.Estado
import cache.ListarFisicasBase
import cache.ListarMoralesBase
import cache.ListarMunicipiosPorEstado
import cache.Municipio
import cache.ObtenerIdsParaBorrarMoral
import cache.PERSONAFISICA
import cache.PERSONAMORAL
import kotlinx.coroutines.flow.Flow

interface SatRepository {
    //1. ENTRADA DE DATOS (INSERTS)
    fun insertarDomicilio(
        cp: String,
        estadoId: Long,
        municipioId: Long,
        tipoVialidad: String,
        localidad: String,
        colonia: String,
        calle: String,
        numeroExterior: String,
        numeroInterior: String?,
        entreCalle1: String,
        entreCalle2: String,
        referencias: String,
        caracteristicas: String
    )

    fun obtenerUltimoId(): Long

    fun insertarPersonaFisica(
        rfc: String, curp: String, nombre: String, apellidos: String,
        fechaDeNacimiento: String, email: String, telefono: String,
        actEconomica: String, regFiscal: String, idDomicilio: Long
    )

    fun insertarPersonaMoral(
        rfc: String, denominacionORazonSocial: String, regimenCapital: String,
        fechaDeConstitucion: String, rfcDelRepresentante: String,
        numEscrituraOpoliza: String, actividadEconomica: String, idDomicilio: Long
    )

    fun insertarSocio(idPersonaMoral: Long, rfcSocio: String)

    fun insertarEstado(id: Long, nombre: String)

    fun insertarMunicipio(id: Long, estadoId: Long, nombre: String)


    //2. SALIDA DE DATOS (CONSULTAS)

    fun consultarFisicaPorRFC(rfc: String): ConsultarFisicaPorRFC?

    fun consultarMoralPorRFC(rfc: String): ConsultarMoralPorRFC?

    fun consultarDomicilioPorId(id: Long): ConsultarDomicilioPorId?

    fun consultarSociosPorIdEmpresa(idEmpresa: Long): List<String>

    fun existeRFCFisica(rfc: String): Boolean

    fun existeRFCMoral(rfc: String): Boolean

    fun listarFisicasBase(): Flow<List<ListarFisicasBase>>

    fun listarMoralesBase(): Flow<List<ListarMoralesBase>>

    // Catálogos
    fun listarEstados(): Flow<List<Estado>>

    fun listarMunicipiosPorEstado(estadoId: Long): Flow<List<ListarMunicipiosPorEstado>>

    fun obtenerNombreEstado(id: Long): String?

    fun obtenerNombreMunicipio(id: Long): String?
    fun contarEstados(): Long

    //3. ACTUALIZACIONES (UPDATES)

    fun actualizarPersonaFisica(
        rfc: String,
        nuevoEmail: String,
        nuevoTel: String,
        actEconomica: String,
        regFiscal: String
    )

    fun actualizarPersonaMoral(
        rfc: String,
        denominacionORazonSocial: String,
        regimenCapital: String,
        actividadEconomica: String,
        rfcRepresentante: String,
        numEscritura: String
    )

    fun actualizarDomicilio(
        idDomicilio: Long,
        cp: String,
        estadoId: Long,
        municipioId: Long,
        localidad: String,
        colonia: String,
        tipoVialidad: String,
        calle: String,
        numeroExterior: String,
        numeroInterior: String?,
        entreCalle1: String,
        entreCalle2: String,
        referencias: String,
        caracteristicas: String
    )


    //4. ELIMINACIÓN (DELETES)

    fun obtenerIdDomicilioFisica(rfc: String): Long?

    fun obtenerIdsParaBorrarMoral(rfc: String): ObtenerIdsParaBorrarMoral?

    fun eliminarSociosPorEmpresa(idEmpresa: Long)

    fun eliminarPersonaFisica(rfc: String)

    fun eliminarPersonaMoral(rfc: String)

    fun eliminarDomicilioPorId(id: Long)
}