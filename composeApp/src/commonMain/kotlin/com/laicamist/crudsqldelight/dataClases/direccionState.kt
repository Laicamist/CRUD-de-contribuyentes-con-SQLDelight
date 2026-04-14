package com.laicamist.crudsqldelight.dataClases

data class direccionState(
    val cp: String = "",
    val estado: String = "",
    val municipio: String = "",
    val localidad: String = "",
    val colonia: String = "",
    val tipoVialidad: String = "",
    val calle: String = "",
    val numeroExterior: String = "",
    val numeroInterior: String? = null,
    val entreCalle1: String = "",
    val entreCalle2: String = "",
    val referencias: String = "",
    val caracteristicas: String = "",
    // Agrega estos dos campos:
    val estadoId: Long = 0,
    val municipioId: Long = 0,
    val isLoadingCP: Boolean = false // Para el indicador de carga de Ktor
)
