package com.laicamist.crudsqldelight.data

import cache.ConsultarFisicaPorRFC
import cache.ConsultarMoralPorRFC
import cache.ListarPersonasFisicas
import cache.ListarPersonasMorales
import cache.ObtenerIdsParaBorrarMoral
import kotlinx.coroutines.flow.Flow

interface SatRepository {
    // 1. ENTRADA DE DATOS (INSERTS)
    fun insertarDomicilio(
        cp: String, estado: String, municipio: String, localidad: String,
        colonia: String, tipoVialidad: String, calle: String,
        numeroExterior: String, numeroInterior: String?, entreCalle1: String,
        entreCalle2: String, referencias: String, caracteristicas: String
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

    // 2. SALIDA DE DATOS (CONSULTAS)
    fun consultarFisicaPorRFC(rfc: String): ConsultarFisicaPorRFC?

    fun consultarMoralPorRFC(rfc: String): ConsultarMoralPorRFC?

    fun consultarSociosPorRFCEmpresa(rfcEmpresa: String): List<String>

    fun existeRFCFisica(rfc: String): Boolean

    fun existeRFCMoral(rfc: String): Boolean

    fun listarTodasLasFisicas(): Flow<List<ListarPersonasFisicas>>

    fun listarTodasLasMorales(): Flow<List<ListarPersonasMorales>>

    // 3. ACTUALIZACIONES (UPDATES)
    fun actualizarContactoFisica(rfc: String, nuevoEmail: String, nuevoTel: String)

    fun actualizarDatosEmpresa(
        rfc: String, denominacionORazonSocial: String,
        regimenCapital: String, actividadEconomica: String
    )

    fun actualizarDomicilio(
        idDomicilio: Long, cp: String, estado: String, municipio: String,
        localidad: String, colonia: String, tipoVialidad: String, calle: String,
        numeroExterior: String, numeroInterior: String?, entreCalle1: String,
        entreCalle2: String, referencias: String, caracteristicas: String
    )

    // 4. ELIMINACIÓN (DELETES)

    fun obtenerIdDomicilioFisica(rfc: String): Long?

    fun obtenerIdsParaBorrarMoral(rfc: String): ObtenerIdsParaBorrarMoral?

    fun eliminarSociosPorEmpresa(idEmpresa: Long)

    fun eliminarPersonaFisica(rfc: String)

    fun eliminarPersonaMoral(rfc: String)

    fun eliminarDomicilioPorId(id: Long)
}