package com.laicamist.crudsqldelight.Ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cache.ListarPersonasMorales
import com.laicamist.crudsqldelight.data.SatRepository
import com.laicamist.crudsqldelight.dataClases.direccionState
import com.laicamist.crudsqldelight.dataClases.personaMoralState
import com.laicamist.crudsqldelight.dataClases.socioState
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class pMoralesViewModel (
    private val repository: SatRepository,
    private val client: HttpClient
) : ViewModel() {

    private val _pMoralState = MutableStateFlow(personaMoralState())
    val pMoralState = _pMoralState.asStateFlow()

    private val _direccionState = MutableStateFlow(direccionState())
    val dState = _direccionState.asStateFlow()

    private val _sociosTemporales = MutableStateFlow<List<socioState>>(emptyList())
    val sociosTemporales = _sociosTemporales.asStateFlow()

    val listaMorales: StateFlow<List<ListarPersonasMorales>> =
        repository.listarTodasLasMorales()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    val desbloquearDomicilio: StateFlow<Boolean> = _pMoralState.map { m ->
        m.rfc.length == 12 && m.denominacionORazonSocial.isNotBlank() && m.rfcDelRepresentante.isNotBlank()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val desbloquearGuardar: StateFlow<Boolean> = combine(_direccionState, _sociosTemporales) { dir, socios ->
        dir.calle.isNotBlank() && dir.cp.length == 5 && socios.isNotEmpty()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    //Manejo de socios
    fun agregarSocio(rfc: String) {
        if (rfc.length in 12..13) {
            _sociosTemporales.update { lista -> lista + socioState(rfcSocio = rfc) }
        }
    }

    fun removerSocio(socio: socioState) {
        _sociosTemporales.update { lista -> lista - socio }
    }

    // Actualización de datos (para cambios en los textfields)
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

    //Guardado
    fun guardarEmpresa() {
        viewModelScope.launch {
            val m = _pMoralState.value
            val d = _direccionState.value
            val socios = _sociosTemporales.value

            try {
                withContext(Dispatchers.IO) {
                    // A. Guardar Domicilio
                    repository.insertarDomicilio(
                        cp = d.cp, estado = d.estado, municipio = d.municipio,
                        localidad = "", colonia = d.colonia, tipoVialidad = d.tipoVialidad,
                        calle = d.calle, numeroExterior = d.numeroExterior,
                        numeroInterior = d.numeroInterior, entreCalle1 = d.entreCalle1,
                        entreCalle2 = d.entreCalle2, referencias = d.referencias,
                        caracteristicas = d.caracteristicas
                    )
                    val idDom = repository.obtenerUltimoId()

                    // B. Guardar Persona Moral
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

                    // C. Guardar Socios (Usando el ID de la moral recién creada)
                    val idMoral = repository.obtenerUltimoId()
                    socios.forEach { socio ->
                        repository.insertarSocio(idPersonaMoral = idMoral, rfcSocio = socio.rfcSocio)
                    }
                }
                // Reset de estados tras éxito
                _pMoralState.value = personaMoralState(guardadoExitoso = true)
                _sociosTemporales.value = emptyList()
                _direccionState.value = direccionState()
            } catch (e: Exception) {
                _pMoralState.update { it.copy(mensajeError = "Error al registrar Persona Moral") }
            }
        }
    }
    fun resetearFormulario() {
        _pMoralState.value = personaMoralState()
        _direccionState.value = direccionState()
        _sociosTemporales.value = emptyList()
    }
}
