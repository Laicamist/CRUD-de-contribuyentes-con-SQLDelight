package com.laicamist.crudsqldelight.Ui.viewModels

import androidx.lifecycle.ViewModel
import cache.Contribuyente
import com.laicamist.crudsqldelight.cache.Database
import com.laicamist.crudsqldelight.dataClases.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DataViewModel(private val sat: Database) : ViewModel() {
    private val _contribuyentes = MutableStateFlow<List<Contribuyente>>(emptyList())
    val contribuyentes: StateFlow<List<Contribuyente>> = _contribuyentes.asStateFlow()
    private val _fisicaState = MutableStateFlow(personaFisicaState())
    val fisicaState: StateFlow<personaFisicaState> = _fisicaState.asStateFlow()

    private val _moralState = MutableStateFlow(personaMoralState())
    val moralState: StateFlow<personaMoralState> = _moralState.asStateFlow()

    private val _direccionState = MutableStateFlow(direccionState())
    val direccionState: StateFlow<direccionState> = _direccionState.asStateFlow()

    private val _socioState = MutableStateFlow(socioState())
    val socioState: StateFlow<socioState> = _socioState.asStateFlow()

    fun updateFisica(update: (personaFisicaState) -> personaFisicaState) {
        _fisicaState.update(update)
    }

    fun updateMoral(update: (personaMoralState) -> personaMoralState) {
        _moralState.update(update)
    }

    fun updateDireccion(update: (direccionState) -> direccionState) {
        _direccionState.update(update)
    }

    fun updateSocio(update: (socioState) -> socioState) {
        _socioState.update(update)
    }

    fun addPersonaFisica() {
        val f = _fisicaState.value
        val d = _direccionState.value

        val contribuyente = Contribuyente(
            id = 0,
            tipo = "Fisica",
            rfc = f.rfc,
            curp = f.curp,
            nombre = f.nombre,
            apellido_paterno = f.apellidos,
            apellido_materno = null,
            fecha_nacimiento = f.fechaDeNacimiento,
            fecha_constitucion = null,
            email = f.email,
            telefono = f.telefono,
            razon_social = null,
            rfc_socios = null,
            poliza = null,
            actividad_economica = f.actEconomica,
            regimen_fiscal = f.regFiscal,
            // Dirección usando direccionState
            estado_id = d.estadoId,
            municipio_id = d.municipioId,
            cp = d.cp,
            regimen_capital = null,
            localidad = d.localidad,
            colonia = d.colonia,
            calle = d.calle,
            numero_exterior = d.numeroExterior,
            numero_interior = d.numeroInterior,
            tipo_vialidad = d.tipoVialidad,
            nombre_vialidad = d.calle,
            entre_calle1 = d.entreCalle1,
            entre_calle2 = d.entreCalle2,
            referencia_adicional = d.referencias,
            caracteristicas_domicilio = d.caracteristicas
        )

        sat.insert(contribuyente)
        clearFields()
    }

    fun addPersonaMoral() {
        val m = _moralState.value
        val d = _direccionState.value

        val contribuyente = Contribuyente(
            id = 0,
            tipo = "Moral",
            rfc = m.rfc,
            curp = null,
            nombre = null,
            apellido_paterno = null,
            apellido_materno = null,
            fecha_nacimiento = null,
            fecha_constitucion = m.fechaDeConstitucion,
            email = null,
            telefono = null,
            razon_social = m.denominacionORazonSocial,
            rfc_socios = _socioState.value.rfcSocio,
            poliza = m.numEscrituraOpoliza,
            actividad_economica = m.actividadEconomica,
            regimen_fiscal = "General de Ley",
            estado_id = d.estadoId,
            municipio_id = d.municipioId,
            cp = d.cp,
            regimen_capital = m.regimenCapital,
            localidad = d.localidad,
            colonia = d.colonia,
            calle = d.calle,
            numero_exterior = d.numeroExterior,
            numero_interior = d.numeroInterior,
            tipo_vialidad = d.tipoVialidad,
            nombre_vialidad = d.calle,
            entre_calle1 = d.entreCalle1,
            entre_calle2 = d.entreCalle2,
            referencia_adicional = d.referencias,
            caracteristicas_domicilio = d.caracteristicas
        )
        sat.insert(contribuyente)
        clearFields()
    }
    fun prepararEdicion(c: Contribuyente) {
        updateFisica {
            it.copy(
                nombre = c.nombre ?: "",
                apellidos = "${c.apellido_paterno} ${c.apellido_materno ?: ""}".trim(),
                rfc = c.rfc ?: "",
                curp = c.curp ?: "",
                fechaDeNacimiento = c.fecha_nacimiento ?: "",
                email = c.email ?: "",
                telefono = c.telefono ?: "",
                regFiscal = c.regimen_fiscal ?: "",
                actEconomica = c.actividad_economica ?: ""
            )
        }
        updateDireccion {
            it.copy(
                estadoId = c.id,
                municipioId = c.municipio_id,
                cp = c.cp,
                calle = c.calle,
                numeroExterior = c.numero_exterior ?: "",
                numeroInterior = c.numero_interior ?: "",
                colonia = c.colonia ?: "",
                tipoVialidad = c.tipo_vialidad ?: "",
                entreCalle1 = c.entre_calle1 ?: "",
                entreCalle2 = c.entre_calle2 ?: "",
                referencias = c.referencia_adicional ?: "",
                caracteristicas = c.caracteristicas_domicilio ?: "",
                localidad = c.localidad ?: ""
            )
        }
    }
    fun savePersonaFisica(
        f: personaFisicaState,
        d: direccionState,
        satViewModel: SatViewModel
    ) {
        val actualizado = Contribuyente(
            id = d.estadoId,
            tipo = "Fisica",
            nombre = f.nombre,
            apellido_paterno = f.apellidos,
            apellido_materno = null,
            rfc = f.rfc,
            curp = f.curp,
            email = f.email,
            telefono = f.telefono,
            regimen_fiscal = f.regFiscal,
            actividad_economica = f.actEconomica,
            fecha_nacimiento = f.fechaDeNacimiento,
            estado_id = d.estadoId,
            municipio_id = d.municipioId,
            cp = d.cp,
            tipo_vialidad = d.tipoVialidad,
            calle = d.calle,
            numero_exterior = d.numeroExterior,
            numero_interior = d.numeroInterior,
            colonia = d.colonia,
            localidad = d.localidad,
            entre_calle1 = d.entreCalle1,
            entre_calle2 = d.entreCalle2,
            referencia_adicional = d.referencias,
            caracteristicas_domicilio = d.caracteristicas,
            nombre_vialidad = d.calle,
            fecha_constitucion = null,
            razon_social = null,
            rfc_socios = null,
            poliza = null,
            regimen_capital = null
        )
        sat.update(actualizado)
        clearFields()
    }

    fun prepararEdicionMoral(c: Contribuyente) {
        updateMoral {
            it.copy(
                denominacionORazonSocial = c.razon_social ?:"",
                rfc = c.rfc ?: "",
                fechaDeConstitucion = c.fecha_constitucion ?: "",
                regimenCapital = c.regimen_capital ?: "",
                actividadEconomica = c.actividad_economica ?: "",
                numEscrituraOpoliza = c.poliza ?: ""
            )
        }


        updateFisica {
            it.copy(rfc = c.rfc ?: "")
        }
        updateSocio {
            it.copy(rfcSocio = c.rfc_socios ?: "")
        }

        updateDireccion {
            it.copy(
                estadoId = c.estado_id,
                municipioId = c.municipio_id,
                cp = c.cp,
                calle = c.calle,
                numeroExterior = c.numero_exterior ?: "",
                numeroInterior = c.numero_interior ?: "",
                colonia = c.colonia ?: "",
                tipoVialidad = c.tipo_vialidad ?: "",
                entreCalle1 = c.entre_calle1 ?: "",
                entreCalle2 = c.entre_calle2 ?: "",
                referencias = c.referencia_adicional ?: "",
                caracteristicas = c.caracteristicas_domicilio ?: "",
                localidad = c.localidad ?: ""
            )
        }

        updateSocio { it.copy(rfcSocio = c.rfc_socios ?: "") }
    }

    fun savePersonaMoral(
        m: personaMoralState,
        d: direccionState,
        f: personaFisicaState,
        s: socioState,
        satViewModel: SatViewModel
    ) {
        val actualizado = Contribuyente(
            id = d.estadoId,
            tipo = "Moral",
            nombre = m.denominacionORazonSocial,
            razon_social = m.denominacionORazonSocial,
            rfc = m.rfc,
            fecha_constitucion = m.fechaDeConstitucion,
            regimen_capital = m.regimenCapital,
            actividad_economica = m.actividadEconomica,
            poliza = m.numEscrituraOpoliza,
            rfc_socios = s.rfcSocio,
            // Domicilio
            estado_id = d.estadoId,
            municipio_id = d.municipioId,
            cp = d.cp,
            tipo_vialidad = d.tipoVialidad,
            calle = d.calle,
            numero_exterior = d.numeroExterior,
            numero_interior = d.numeroInterior,
            colonia = d.colonia,
            localidad = d.localidad,
            entre_calle1 = d.entreCalle1,
            entre_calle2 = d.entreCalle2,
            referencia_adicional = d.referencias,
            caracteristicas_domicilio = d.caracteristicas,
            nombre_vialidad = d.calle,
            apellido_paterno = null,
            apellido_materno = null,
            curp = null,
            email = null,
            telefono = null,
            fecha_nacimiento = null,
            regimen_fiscal = "General de Ley Personas Morales"
        )

        satViewModel.update(actualizado)
        clearFields()
    }

    fun clearFields() {
        _fisicaState.value = personaFisicaState()
        _moralState.value = personaMoralState()
        _direccionState.value = direccionState()
        _socioState.value = socioState()
    }
}