package com.laicamist.crudsqldelight.dataClases

data class personaFisicaState(
    val rfc: String = "",
    val curp: String = "",
    val nombre: String = "",
    val apellidos: String = "",
    val fechaDeNacimiento: String = "",
    val email: String = "",
    val telefono: String = "",
    val actEconomica: String = "",
    val regFiscal: String = "",
    val mensajeError: String? = null,
    val guardadoExitoso: Boolean = false
)
