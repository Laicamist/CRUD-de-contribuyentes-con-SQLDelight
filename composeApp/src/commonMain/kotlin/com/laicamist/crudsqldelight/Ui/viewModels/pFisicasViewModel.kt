package com.laicamist.crudsqldelight.Ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cache.ListarPersonasFisicas
import com.laicamist.crudsqldelight.data.SatRepository
import com.laicamist.crudsqldelight.dataClases.personaFisicaState
import com.laicamist.crudsqldelight.dataClases.direccionState
import com.laicamist.crudsqldelight.client.CpResponse
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.collections.emptyList

class pFisicasViewModel(
    private val repository: SatRepository,
    private val client: HttpClient
) : ViewModel() {
    private val _pfisicaState = MutableStateFlow(personaFisicaState())
    private val _direccionState = MutableStateFlow(direccionState())

    val pfisicaState: StateFlow<personaFisicaState> = _pfisicaState.asStateFlow()
    val dState: StateFlow<direccionState> = _direccionState.asStateFlow()

// Validación de botón
    val desbloquearDomicilio: StateFlow<Boolean> = _pfisicaState.map { fisica ->
        val rfcValido = fisica.rfc.length == 13
        val curpValida = fisica.curp.length == 18
        val emailvalida = fisica.email.length > 6
        val camposCompletos = listOf(
            fisica.nombre,
            fisica.fechaDeNacimiento,
            fisica.telefono,
            fisica.actEconomica,
            fisica.regFiscal
        ).all { it.isNotBlank() }

        rfcValido && curpValida && camposCompletos && emailvalida
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val desbloquearGuardar: StateFlow<Boolean> = _direccionState.map { dir ->
        val cpValido = dir.cp.length == 5
        val camposCompletos = listOf(
            dir.calle,
            dir.municipio,
            dir.tipoVialidad,
            dir.referencias,
            dir.caracteristicas,
            dir.numeroExterior,
            dir.entreCalle1,
            dir.entreCalle2
        ).all { it.isNotBlank() }

        cpValido && camposCompletos && !dir.isLoadingCP
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Actualización de datos (para cambios en los textfields)
    fun onFisicaChange(campo: String, valor: String) {
        _pfisicaState.update { actual ->
            when (campo) {
                "rfc" -> actual.copy(rfc = valor, mensajeError = null)
                "curp" -> actual.copy(curp = valor)
                "nombre" -> actual.copy(nombre = valor)
                "apellidos" -> actual.copy(apellidos = valor)
                "fechaNac" -> actual.copy(fechaDeNacimiento = valor)
                "email" -> actual.copy(email = valor)
                "tel" -> actual.copy(telefono = valor)
                "actEcon" -> actual.copy(actEconomica = valor)
                "regFiscal" -> actual.copy(regFiscal = valor)
                else -> actual
            }
        }
    }

    fun onDireccionChange(campo: String, valor: String) {
        _direccionState.update { actual ->
            val nuevaDir = when (campo) {
                "cp" -> {
                    if (valor.length == 5) buscarCp(valor)
                    actual.copy(cp = valor)
                }
                "estado" -> actual.copy(estado = valor)
                "municipio" -> actual.copy(municipio = valor)
                "colonia" -> actual.copy(colonia = valor)
                "calle" -> actual.copy(calle = valor)
                "nExt" -> actual.copy(numeroExterior = valor)
                "nInt" -> actual.copy(numeroInterior = valor)
                "entre1" -> actual.copy(entreCalle1 = valor)
                "entre2" -> actual.copy(entreCalle2 = valor)
                "ref" -> actual.copy(referencias = valor)
                "carac" -> actual.copy(caracteristicas = valor)
                else -> actual
            }
            nuevaDir
        }
    }

    //Autorrellenado (KTOR)
    private fun buscarCp(cp: String) {
        viewModelScope.launch {
            _direccionState.update { it.copy(isLoadingCP = true) }
            try {
                val response: CpResponse = client.get("https://api.zippopotam.us/mx/$cp").body()
                val lugar = response.places.firstOrNull()
                if (lugar != null) {
                    _direccionState.update { it.copy(
                        estado = lugar.state,
                        municipio = lugar.placeName,
                        isLoadingCP = false
                    )}
                }
            } catch (e: Exception) {
                _direccionState.update { it.copy(isLoadingCP = false) }
                _pfisicaState.update { it.copy(mensajeError = "Error al conectar con la API de CP") }
            }
        }
    }

    //Guardado
    fun guardarTodo() {
        viewModelScope.launch {
            val f = _pfisicaState.value
            val d = _direccionState.value
            try {
                withContext(Dispatchers.IO) {
                    repository.insertarDomicilio(
                        cp = d.cp, estado = d.estado, municipio = d.municipio,
                        localidad = "", colonia = d.colonia, tipoVialidad = "Calle",
                        calle = d.calle, numeroExterior = d.numeroExterior,
                        numeroInterior = d.numeroInterior, entreCalle1 = d.entreCalle1,
                        entreCalle2 = d.entreCalle2, referencias = d.referencias,
                        caracteristicas = d.caracteristicas
                    )
                    val idDom = repository.obtenerUltimoId()
                    repository.insertarPersonaFisica(
                        f.rfc, f.curp, f.nombre, f.apellidos, f.fechaDeNacimiento,
                        f.email, f.telefono, f.actEconomica, f.regFiscal, idDom
                    )
                }
                _pfisicaState.value = personaFisicaState(guardadoExitoso = true)
                _direccionState.value = direccionState()
            } catch (e: Exception) {
                _pfisicaState.update { it.copy(mensajeError = "Error al guardar en DB") }
            }
        }
    }

    //Lista
    val listaFisicas: StateFlow<List<ListarPersonasFisicas>> =
        repository.listarTodasLasFisicas() // Llamamos al nuevo método del Repo
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

    //Eliminar
    fun eliminarRegistro(rfc: String) {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    repository.eliminarPersonaFisica(rfc)
                }
            } catch (e: Exception) {
                _pfisicaState.update { it.copy(mensajeError = "No se pudo eliminar el registro") }
            }
        }
    }

    //Actualizacion de información de contacto
    fun actualizarContacto() {
        viewModelScope.launch {
            val f = _pfisicaState.value
            try {
                withContext(Dispatchers.IO) {
                    repository.actualizarContactoFisica(
                        rfc = f.rfc,
                        nuevoEmail = f.email,
                        nuevoTel = f.telefono
                    )
                }
                _pfisicaState.update { it.copy(mensajeError = "Datos actualizados") }
            } catch (e: Exception) {
                _pfisicaState.update { it.copy(mensajeError = "Error al actualizar contacto") }
            }
        }
    }

    //Consulta individual
    fun buscarPorRfc(rfc: String) {
        viewModelScope.launch {
            try {
                val persona = withContext(Dispatchers.IO) {
                    repository.consultarFisicaPorRFC(rfc)
                }

                if (persona != null) {
                    _pfisicaState.update { it.copy(
                        rfc = persona.rfc,
                        curp = persona.curp,
                        nombre = persona.nombre,
                        apellidos = persona.apellidos,
                        fechaDeNacimiento = persona.fechaDeNacimiento,
                        email = persona.email,
                        telefono = persona.telefono,
                        actEconomica = persona.actEconomica,
                        regFiscal = persona.regFiscal
                    )}
                    // Nota: Aquí podrías disparar otra búsqueda para el domicilio si lo necesitas
                }
            } catch (e: Exception) {
                _pfisicaState.update { it.copy(mensajeError = "No se encontró el RFC") }
            }
        }
    }

    fun resetearFormulario() {
        _pfisicaState.update { personaFisicaState() }
        _direccionState.update { direccionState() }
    }
}