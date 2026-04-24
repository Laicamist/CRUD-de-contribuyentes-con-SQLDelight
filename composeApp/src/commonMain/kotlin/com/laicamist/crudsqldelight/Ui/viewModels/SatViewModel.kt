package com.laicamist.crudsqldelight.Ui.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cache.*
import com.laicamist.crudsqldelight.cache.Database
import crudsqldelight.composeapp.generated.resources.Res
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.laicamist.crudsqldelight.dataClases.DataSeeder


class SatViewModel(
    private val sat: Database
) : ViewModel() {

    private val _contribuyentes = MutableStateFlow<List<Contribuyente>>(emptyList())
    val contribuyentes = _contribuyentes.asStateFlow()
    private val _contribuyente = MutableStateFlow<Contribuyente?>(null)
    val contribuyente = _contribuyente.asStateFlow()
    fun cargarContribuyente(id: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            _contribuyente.value = sat.getById(id).first()
        }
    }
    init {
        viewModelScope.launch(Dispatchers.IO) {
            if (sat.getEstados().isEmpty()) {
                DataSeeder.seedEstados(sat)
            }
            val bytes = Res.readBytes("files/municipios.json")
            val inputStream = bytes.inputStream()
            DataSeeder.seedFromJson(sat, inputStream)
            loadEstados()
            loadContribuyentes()
        }
    }

     fun loadContribuyentes() {
        viewModelScope.launch(Dispatchers.IO) {
            _contribuyentes.value = sat.getAll()
        }
    }

    fun insert(contribuyente: Contribuyente) {
        viewModelScope.launch(Dispatchers.IO) {
            sat.insert(contribuyente)
            loadContribuyentes() // refrescar lista
        }
    }

    fun delete(contribuyente: Contribuyente) {
        viewModelScope.launch(Dispatchers.IO) {
            sat.delete(contribuyente)
            loadContribuyentes()
        }
    }

    fun update(contribuyente: Contribuyente) {
        viewModelScope.launch(Dispatchers.IO) {
            sat.update(contribuyente)
            loadContribuyentes()
        }
    }
    fun getEstados(): List<Estado> {
        return sat.getEstados()
    }
    fun getMunicipios(id : Long): List<Municipio> {
        return sat.getMunicipiosByEstado(id)
    }

    fun getEstadoById(id: Long): Estado {
        return sat.getEstadoByID(id)
    }
    fun getMunicipioById(id: Long): Municipio {
        return sat.getMunicipioByID(id)
    }
    private val _estados = MutableStateFlow<List<Estado>>(emptyList())
    val estados = _estados.asStateFlow()

    private val _municipios = MutableStateFlow<Map<Long, List<Municipio>>>(emptyMap())
    val municipios = _municipios.asStateFlow()

    private fun loadEstados() {
        viewModelScope.launch(Dispatchers.IO) {
            val listaEstados = sat.getEstados()
            _estados.value = listaEstados


            _municipios.value = listaEstados.associate { estado ->
                estado.id to sat.getMunicipiosByEstado(estado.id)
            }
        }
    }
}