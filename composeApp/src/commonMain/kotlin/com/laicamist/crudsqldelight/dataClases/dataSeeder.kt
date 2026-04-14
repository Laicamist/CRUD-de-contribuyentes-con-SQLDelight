package com.laicamist.crudsqldelight.dataClases

import com.laicamist.crudsqldelight.cache.AppDatabase

object DataSeeder {
    fun seed(db: AppDatabase) {
        val queries = db.appDatabaseQueries

        // Solo llenamos si la tabla está vacía
        val count = queries.contarEstados().executeAsOne()
        if (count > 0L) return

        db.transaction {
            // --- 32 ESTADOS DE MÉXICO ---
            val estados = listOf(
                1L to "Aguascalientes", 2L to "Baja California", 3L to "Baja California Sur",
                4L to "Campeche", 5L to "Coahuila", 6L to "Colima", 7L to "Chiapas",
                8L to "Chihuahua", 9L to "Ciudad de México", 10L to "Durango",
                11L to "Guanajuato", 12L to "Guerrero", 13L to "Hidalgo", 14L to "Jalisco",
                15L to "México", 16L to "Michoacán", 17L to "Morelos", 18L to "Nayarit",
                19L to "Nuevo León", 20L to "Oaxaca", 21L to "Puebla", 22L to "Querétaro",
                23L to "Quintana Roo", 24L to "San Luis Potosí", 25L to "Sinaloa",
                26L to "Sonora", 27L to "Tabasco", 28L to "Tamaulipas", 29L to "Tlaxcala",
                30L to "Veracruz", 31L to "Yucatán", 32L to "Zacatecas"
            )

            estados.forEach { (id, nombre) ->
                queries.insertarEstado(id, nombre)
            }

            //GUANAJUATO (ID: 11)
            val gto = listOf(
                "Abasolo", "Acámbaro", "Apaseo el Alto", "Apaseo el Grande", "Atarjea", "Celaya", "Comonfort",
                "Coroneo", "Cortazar", "Cuerámaro", "Doctor Mora", "Dolores Hidalgo", "Guanajuato", "Huanímaro",
                "Irapuato", "Jaral del Progreso", "Jerécuaro", "León", "Manuel Doblado", "Moroleón", "Ocampo",
                "Pénjamo", "Pueblo Nuevo", "Purísima del Rincón", "Romita", "Salamanca", "Salvatierra",
                "San Diego de la Unión", "San Felipe", "San Francisco del Rincón", "San José Iturbide",
                "San Luis de la Paz", "San Miguel de Allende", "Santa Catarina", "Santa Cruz de Juventino Rosas",
                "Santiago Maravatío", "Silao", "Tarandacuao", "Tarimoro", "Tierra Blanca", "Uriangato",
                "Valle de Santiago", "Victoria", "Villagrán", "Xichú", "Yuriria"
            )
            var idMun = 1L
            gto.forEach { queries.insertarMunicipio(idMun++, 11, it) }

            //MICHOACÁN (ID: 16)
            val mich = listOf(
                "Acuitzio", "Aguililla", "Álvaro Obregón", "Angamacutiro", "Angangueo", "Apatzingán", "Aporo", "Aquila",
                "Ario", "Arteaga", "Briseñas", "Buenavista", "Carácuaro", "Casilda", "Charapan", "Charo", "Chavinda",
                "Cherán", "Chilchota", "Chinicuila", "Chucándiro", "Churintzio", "Churumuco", "Coahuayana", "Coalcomán",
                "Coeneo", "Contepec", "Copándaro", "Cotija", "Cuitzeo", "Epitacio Huerta", "Erongarícuaro", "Gabriel Zamora",
                "Hidalgo", "Huandacareo", "Huaniqueo", "Huetamo", "Huiramba", "Indaparapeo", "Irimbo", "Ixtlán", "Jacona",
                "Jiménez", "Jiquilpan", "Juárez", "Jungapeo", "Lagunillas", "La Huacana", "La Piedad", "Lázaro Cárdenas",
                "Los Reyes", "Madero", "Maravatío", "Marcos Castellanos", "Morelia", "Morelos", "Múgica", "Nahuatzen",
                "Nocupétaro", "Nuevo Parangaricutiro", "Nuevo Urecho", "Numarán", "Ocampo", "Pajacuarán", "Panindícuaro",
                "Paracho", "Parácuaro", "Pátzcuaro", "Penjamillo", "Peribán", "Purépero", "Puruándiro", "Queréndaro",
                "Quiroga", "Sahuayo", "San Lucas", "Santa Ana Maya", "Salvador Escalante", "Senguio", "Susupuato",
                "Tacámbaro", "Tancítaro", "Tangamandapio", "Tangancícuaro", "Tanhuato", "Taretan", "Tarímbaro", "Tepalcatepec",
                "Tingambato", "Tingüindín", "Tiquicheo", "Tlalpujahua", "Tlazazalca", "Tocumbo", "Tumbiscatío", "Turicato",
                "Tuxpan", "Tuzantla", "Tzintzuntzan", "Tzitzio", "Uruapan", "Venustiano Carranza", "Villamar", "Vista Hermosa",
                "Yurécuaro", "Zacapu", "Zamora", "Zináparo", "Zinapécuaro", "Ziracuaretiro", "Zitácuaro"
            )
            mich.forEach { queries.insertarMunicipio(idMun++, 16, it) }

            // Aguascalientes (1)
            queries.insertarMunicipio(idMun++, 1, "Aguascalientes"); queries.insertarMunicipio(idMun++, 1, "Calvillo")
            // CDMX (9)
            queries.insertarMunicipio(idMun++, 9, "Cuauhtémoc"); queries.insertarMunicipio(idMun++, 9, "Benito Juárez")
            // Jalisco (14)
            queries.insertarMunicipio(idMun++, 14, "Guadalajara"); queries.insertarMunicipio(idMun++, 14, "Zapopan")
            // Nuevo León (19)
            queries.insertarMunicipio(idMun++, 19, "Monterrey"); queries.insertarMunicipio(idMun++, 19, "San Pedro")
            // Puebla (21)
            queries.insertarMunicipio(idMun++, 21, "Puebla"); queries.insertarMunicipio(idMun++, 21, "Cholula")
            // Veracruz (30)
            queries.insertarMunicipio(idMun++, 30, "Xalapa"); queries.insertarMunicipio(idMun++, 30, "Veracruz")
            // Yucatán (31)
            queries.insertarMunicipio(idMun++, 31, "Mérida"); queries.insertarMunicipio(idMun++, 31, "Valladolid")
        }
    }
}