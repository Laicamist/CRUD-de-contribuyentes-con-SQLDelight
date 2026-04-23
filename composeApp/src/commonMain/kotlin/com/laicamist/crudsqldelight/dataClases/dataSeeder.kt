package com.laicamist.crudsqldelight.dataClases

import com.laicamist.crudsqldelight.cache.AppDatabase
import com.laicamist.crudsqldelight.cache.Database
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.InputStream

object DataSeeder {
    fun seedEstados(sat: Database) {

        val estados = listOf(
            "Aguascalientes",
            "Baja California",
            "Baja California Sur",
            "Campeche",
            "Chiapas",
            "Chihuahua",
            "Ciudad de México",
            "Coahuila",
            "Colima",
            "Durango",
            "Estado de México",
            "Guanajuato",
            "Guerrero",
            "Hidalgo",
            "Jalisco",
            "Michoacán",
            "Morelos",
            "Nayarit",
            "Nuevo León",
            "Oaxaca",
            "Puebla",
            "Querétaro",
            "Quintana Roo",
            "San Luis Potosí",
            "Sinaloa",
            "Sonora",
            "Tabasco",
            "Tamaulipas",
            "Tlaxcala",
            "Veracruz",
            "Yucatán",
            "Zacatecas"
        )

        estados.forEach {
            sat.insertEstado(it)

        }
    }
    @Serializable
    data class EstadoMunicipios(
        val estado: String,
        val municipios: List<String>
    )


    fun seedFromJson(sat: Database, jsonStream: InputStream) {
        val json = Json { ignoreUnknownKeys = true }
        val jsonString = jsonStream.bufferedReader().readText()
        val data = try {
            json.decodeFromString<List<EstadoMunicipios>>(jsonString)
        } catch (e: Exception) {
            println("Error al leer el JSON: ${e.message}")
            return
        }
        var contador = 1
        data.forEach { item ->
            val estadoId = contador

            if(sat.getMunicipiosByEstado(estadoId.toLong()).isEmpty()) {
                item.municipios.forEach { municipio ->
                    sat.insertMunicipio(municipio, estadoId.toLong())
                }
            }
            contador++
        }
    }
}

