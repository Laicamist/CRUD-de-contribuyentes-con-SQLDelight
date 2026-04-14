package com.laicamist.crudsqldelight.Ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cache.*
import com.laicamist.crudsqldelight.data.SatRepository
import com.laicamist.crudsqldelight.dataClases.personaFisicaState
import com.laicamist.crudsqldelight.dataClases.direccionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class pFisicasViewModel(
    private val repository: SatRepository,
) : ViewModel() {
    private val _pfisicaState = MutableStateFlow(personaFisicaState())
    private val _direccionState = MutableStateFlow(direccionState())

    val pfisicaState: StateFlow<personaFisicaState> = _pfisicaState.asStateFlow()
    val dState: StateFlow<direccionState> = _direccionState.asStateFlow()

    val estados: StateFlow<List<Estado>> = repository.listarEstados()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    private val _municipios = MutableStateFlow<List<ListarMunicipiosPorEstado>>(emptyList())
    val municipios: StateFlow<List<ListarMunicipiosPorEstado>> = _municipios.asStateFlow()

    private val _detallePersona = MutableStateFlow<ConsultarFisicaPorRFC?>(null)
    val detallePersona: StateFlow<ConsultarFisicaPorRFC?> = _detallePersona.asStateFlow()


    // Validación de botón
    val desbloquearDomicilio: StateFlow<Boolean> = _pfisicaState.map { fisica ->
        val rfcValido = fisica.rfc.length == 13
        val curpValida = fisica.curp.length == 18
        val emailvalida = fisica.email.length > 6
        val emailval = fisica.email.contains("@")
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
        val cpValido = dir.cp.trim().length == 5
        val camposObligatorios = listOf(
            dir.calle,
            dir.colonia,
            dir.numeroExterior,
            dir.tipoVialidad
        ).all { it.isNotBlank() }
        val catalogosSeleccionados = dir.estadoId != 0L && dir.municipioId != 0L
        cpValido && camposObligatorios && catalogosSeleccionados && !dir.isLoadingCP
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Actualización de datos (para cambios en los textfields)
    // --- CAMBIOS EN FORMULARIO ---
    fun onFisicaChange(campo: String, valor: String) {
        _pfisicaState.update { actual ->
            when (campo) {
                "rfc" -> actual.copy(rfc = valor)
                "curp" -> actual.copy(curp = valor)
                "nombre" -> actual.copy(nombre = valor)
                "apellidos" -> actual.copy(apellidos = valor)
                "fechaNac" -> actual.copy(fechaDeNacimiento = valor) // <--- Checa este
                "email" -> actual.copy(email = valor)
                "tel" -> actual.copy(telefono = valor)
                "actEcon" -> actual.copy(actEconomica = valor)     // <--- DEBE SER "actEcon"
                "regFiscal" -> actual.copy(regFiscal = valor)
                else -> actual
            }
        }
    }

    fun onDireccionChange(campo: String, valor: Any) {
        _direccionState.update { actual ->
            when (campo) {
                "cp" -> actual.copy(cp = valor as String)
                "calle" -> actual.copy(calle = valor as String)
                "nExt" -> actual.copy(numeroExterior = valor as String)
                "nInt" -> actual.copy(numeroInterior = valor as String)
                "colonia" -> actual.copy(colonia = valor as String)
                "localidad" -> actual.copy(localidad = valor as String)
                "vialidad" -> actual.copy(tipoVialidad = valor as String)
                "estadoId" -> {
                    cargarMunicipios(valor as Long)
                    actual.copy(estadoId = valor, municipioId = 0L)
                }

                "municipioId" -> actual.copy(municipioId = valor as Long)
                "entre1" -> actual.copy(entreCalle1 = valor as String)
                "entre2" -> actual.copy(entreCalle2 = valor as String)
                "ref" -> actual.copy(referencias = valor as String)
                "carac" -> actual.copy(caracteristicas = valor as String)
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
    fun guardarTodo() {
        viewModelScope.launch {
            val f = _pfisicaState.value
            val d = _direccionState.value
            try {
                withContext(Dispatchers.IO) {
                    // 1. Insertamos Domicilio con TODOS los campos nuevos
                    repository.insertarDomicilio(
                        cp = d.cp,
                        estadoId = d.estadoId,
                        municipioId = d.municipioId,
                        tipoVialidad = d.tipoVialidad,
                        localidad = d.localidad,
                        colonia = d.colonia,
                        calle = d.calle,
                        numeroExterior = d.numeroExterior,
                        numeroInterior = d.numeroInterior,
                        entreCalle1 = d.entreCalle1,
                        entreCalle2 = d.entreCalle2,
                        referencias = d.referencias,
                        caracteristicas = d.caracteristicas
                    )

                    val idDom = repository.obtenerUltimoId()

                    // 2. Insertamos la Persona Física
                    repository.insertarPersonaFisica(
                        rfc = f.rfc,
                        curp = f.curp,
                        nombre = f.nombre,
                        apellidos = f.apellidos,
                        fechaDeNacimiento = f.fechaDeNacimiento,
                        email = f.email,
                        telefono = f.telefono,
                        actEconomica = f.actEconomica,
                        regFiscal = f.regFiscal,
                        idDomicilio = idDom
                    )
                }
                _pfisicaState.update { it.copy(guardadoExitoso = true) }
            } catch (e: Exception) {
                _pfisicaState.update { it.copy(mensajeError = "Error al guardar: ${e.message}") }
            }
        }
    }

    // --- LISTADO ---
    val listaFisicas: StateFlow<List<ListarFisicasBase>> =
        repository.listarFisicasBase()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- ELIMINAR ---
    fun eliminarRegistro(rfc: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                repository.eliminarPersonaFisica(rfc)
            }
        }
    }

    fun resetearFormulario() {
        _pfisicaState.value = personaFisicaState()
        _direccionState.value = direccionState()
        _municipios.value = emptyList()
    }
    // ... dentro de pFisicasViewModel ...

    fun obtenerDetallePersona(rfc: String) {
        viewModelScope.launch {
            // 1. Limpiamos estados previos para evitar mostrar datos viejos
            _detallePersona.value = null
            _direccionState.value = direccionState()

            try {
                // 2. Buscamos la Persona Física por RFC
                val persona = withContext(Dispatchers.IO) {
                    repository.consultarFisicaPorRFC(rfc)
                }

                _detallePersona.value = persona

                // 3. Si la persona existe, usamos su idDomicilio para traer la dirección
                persona?.idDomicilio?.let { idDom ->
                    cargarDireccionPorId(idDom)
                }
            } catch (e: Exception) {
                println("Error al obtener detalle: ${e.message}")
            }
        }
    }

    private fun cargarDireccionPorId(id: Long) {
        viewModelScope.launch {
            try {
                val domicilio = withContext(Dispatchers.IO) {
                    repository.consultarDomicilioPorId(id) // Asegúrate de tener este método en el repo
                }

                domicilio?.let { d ->
                    _direccionState.update {
                        it.copy(
                            cp = d.cp,
                            calle = d.calle,
                            colonia = d.colonia,
                            numeroExterior = d.numeroExterior,
                            numeroInterior = d.numeroInterior,
                            estadoId = d.estadoId,
                            municipioId = d.municipioId,
                            tipoVialidad = d.tipoVialidad,
                            entreCalle1 = d.entreCalle1,
                            entreCalle2 = d.entreCalle2,
                            referencias = d.referencias,
                            caracteristicas = d.caracteristicas
                        )
                    }
                }
            } catch (e: Exception) {
                println("Error al cargar domicilio: ${e.message}")
            }
        }
    }
}