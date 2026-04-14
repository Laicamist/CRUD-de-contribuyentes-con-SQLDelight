package com.laicamist.crudsqldelight.Ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cache.*
import com.laicamist.crudsqldelight.data.SatRepository
import com.laicamist.crudsqldelight.dataClases.direccionState
import com.laicamist.crudsqldelight.dataClases.personaMoralState
import com.laicamist.crudsqldelight.dataClases.socioState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class pMoralesViewModel (
    private val repository: SatRepository,
) : ViewModel() {

    private val _pMoralState = MutableStateFlow(personaMoralState())
    val pMoralState = _pMoralState.asStateFlow()

    private val _direccionState = MutableStateFlow(direccionState())
    val dState = _direccionState.asStateFlow()

    private val _sociosTemporales = MutableStateFlow<List<socioState>>(emptyList())
    val sociosTemporales = _sociosTemporales.asStateFlow()

    val estados: StateFlow<List<Estado>> = repository.listarEstados()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _municipios = MutableStateFlow<List<ListarMunicipiosPorEstado>>(emptyList())
    val municipios = _municipios.asStateFlow()

    val listaMorales: StateFlow<List<ListarMoralesBase>> =
        repository.listarMoralesBase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val desbloquearDomicilio: StateFlow<Boolean> = _pMoralState.map { m ->
        m.rfc.length == 12 && m.denominacionORazonSocial.isNotBlank() && m.rfcDelRepresentante.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val desbloquearGuardar: StateFlow<Boolean> = combine(_direccionState, _sociosTemporales) { dir, socios ->
        dir.cp.length == 5 && dir.calle.isNotBlank() &&
                dir.estadoId != 0L && dir.municipioId != 0L && socios.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    //Manejo de socios
    fun agregarSocio(rfc: String) {
        if (rfc.length in 12..13) {
            _sociosTemporales.update { lista ->
                if (lista.any { it.rfcSocio == rfc }) lista else lista + socioState(rfcSocio = rfc)
            }
        }
    }

    fun removerSocio(socio: socioState) {
        _sociosTemporales.update { lista -> lista - socio }
    }

    // --- CAMBIOS EN FORMULARIO ---
    fun onMoralChange(campo: String, valor: String) {
        _pMoralState.update { actual ->
            when (campo) {
                "rfc" -> actual.copy(rfc = valor)
                "razon" -> actual.copy(denominacionORazonSocial = valor)
                "regimen" -> actual.copy(regimenCapital = valor)
                "fecha" -> actual.copy(fechaDeConstitucion = valor)
                "rfcRep" -> actual.copy(rfcDelRepresentante = valor)
                "escritura" -> actual.copy(numEscrituraOpoliza = valor)
                "actEcon" -> actual.copy(actividadEconomica = valor)
                else -> actual
            }
        }
    }

    fun onDireccionChange(campo: String, valor: Any) {
        _direccionState.update { actual ->
            when (campo) {
                "cp" -> actual.copy(cp = valor as String)
                "estadoId" -> {
                    cargarMunicipios(valor as Long)
                    actual.copy(estadoId = valor, municipioId = 0L)
                }
                "municipioId" -> actual.copy(municipioId = valor as Long)
                "calle" -> actual.copy(calle = valor as String)
                "colonia" -> actual.copy(colonia = valor as String)
                "vialidad" -> actual.copy(tipoVialidad = valor as String)
                "nExt" -> actual.copy(numeroExterior = valor as String)
                "nInt" -> actual.copy(numeroInterior = valor as String)
                else -> actual
            }
        }
    }

    private fun cargarMunicipios(idEstado: Long) {
        viewModelScope.launch {
            repository.listarMunicipiosPorEstado(idEstado).collect { lista ->
                _municipios.value = lista
            }
        }
    }

    // --- GUARDADO ---
    fun guardarEmpresa() {
        viewModelScope.launch {
            val m = _pMoralState.value
            val d = _direccionState.value
            val socios = _sociosTemporales.value

            try {
                withContext(Dispatchers.IO) {
                    // 1. Guardar Domicilio
                    repository.insertarDomicilio(
                        cp = d.cp, estadoId = d.estadoId, municipioId = d.municipioId,
                        tipoVialidad = d.tipoVialidad, localidad = d.localidad,
                        colonia = d.colonia, calle = d.calle, numeroExterior = d.numeroExterior,
                        numeroInterior = d.numeroInterior, entreCalle1 = "",
                        entreCalle2 = "", referencias = "", caracteristicas = ""
                    )
                    val idDom = repository.obtenerUltimoId()

                    // 2. Guardar Persona Moral
                    repository.insertarPersonaMoral(
                        rfc = m.rfc,
                        denominacionORazonSocial = m.denominacionORazonSocial,
                        regimenCapital = m.regimenCapital,
                        fechaDeConstitucion = m.fechaDeConstitucion,
                        rfcDelRepresentante = m.rfcDelRepresentante,
                        numEscrituraOpoliza = m.numEscrituraOpoliza,
                        actividadEconomica = m.actividadEconomica,
                        idDomicilio = idDom
                    )

                    // 3. Guardar Socios
                    val idMoral = repository.obtenerUltimoId()
                    socios.forEach { socio ->
                        repository.insertarSocio(idPersonaMoral = idMoral, rfcSocio = socio.rfcSocio)
                    }
                }
                _pMoralState.update { it.copy(guardadoExitoso = true) }
                resetearFormulario()
            } catch (e: Exception) {
                _pMoralState.update { it.copy(mensajeError = "Error al registrar") }
            }
        }
    }

    fun resetearFormulario() {
        _pMoralState.value = personaMoralState()
        _direccionState.value = direccionState()
        _sociosTemporales.value = emptyList()
        _municipios.value = emptyList()
    }
}