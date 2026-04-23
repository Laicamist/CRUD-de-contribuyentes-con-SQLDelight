package com.laicamist.crudsqldelight.dataClases

data class personaMoralState(
    val rfc: String = "",
    val denominacionORazonSocial: String = "",
    val regimenCapital: String = "",
    val fechaDeConstitucion: String = "",
    val numEscrituraOpoliza: String = "",
    val actividadEconomica: String = "",
    val mensajeError: String? = null,
    val guardadoExitoso: Boolean = false
)
