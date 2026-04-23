package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.laicamist.crudsqldelight.Ui.viewModels.DataViewModel
import com.laicamist.crudsqldelight.Ui.viewModels.SatViewModel
import crudsqldelight.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMoralesScreen(
    dataViewModel: DataViewModel,
    satViewModel: SatViewModel,
    navController: NavController
) {
    val moral by dataViewModel.moralState.collectAsState()
    val direccion by dataViewModel.direccionState.collectAsState()
    val socio by dataViewModel.socioState.collectAsState()
    val fisica by dataViewModel.fisicaState.collectAsState()

    val estados by satViewModel.estados.collectAsState()
    val municipiosMap by satViewModel.municipios.collectAsState()
    val municipiosDelEstado = municipiosMap[direccion.estadoId] ?: emptyList()

    val vialidades = listOf("Calle", "Avenida", "Boulevard", "Circuito", "Privada")
    val opcionesCapital = listOf("S.A. de C.V.", "S.C.", "S. de R.L. de C.V.", "S.A.P.I. de C.V.")
    val actividadesSAT = listOf("Comercio", "Servicios Profesionales", "Construcción", "Manufactura")

    Scaffold() { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item { SectionHeader("Información de la Empresa") }

            item {
                Card(elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                        CustomTextField(
                            value = moral.denominacionORazonSocial,
                            onValueChange = { texto ->
                                dataViewModel.updateMoral { m -> m.copy(denominacionORazonSocial = texto.uppercase()) }
                            },
                            label = "Denominación o Razón Social"
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomTextField(
                                value = moral.rfc,
                                onValueChange = { rfcEmpresa ->
                                    if (rfcEmpresa.length <= 12) {
                                        dataViewModel.updateMoral { m -> m.copy(rfc = rfcEmpresa.uppercase()) }
                                    }
                                },
                                label = "RFC Empresa",
                                modifier = Modifier.weight(1f),
                                maxChar = 12
                            )

                            CustomTextField(
                                value = moral.fechaDeConstitucion,
                                onValueChange = { fecha ->
                                    dataViewModel.updateMoral { m -> m.copy(fechaDeConstitucion = fecha) }
                                },
                                label = "F. Constitución",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        CustomTextField(
                            value = socio.rfcSocio,
                            onValueChange = { rfcSocio ->
                                if (rfcSocio.length <= 13) {
                                    dataViewModel.updateSocio { s -> s.copy(rfcSocio = rfcSocio.uppercase()) }
                                }
                            },
                            label = "RFC de Socios/Accionistas",
                            maxChar = 13
                        )

                        CustomTextField(
                            value = moral.numEscrituraOpoliza,
                            onValueChange = { nEscritura ->
                                dataViewModel.updateMoral { m -> m.copy(numEscrituraOpoliza = nEscritura) }
                            },
                            label = "Número de Escritura o Póliza"
                        )

                        CustomComboBox(
                            label = "Régimen Capital",
                            opciones = opcionesCapital,
                            textoOpcion = { opcion -> opcion },
                            seleccionado = opcionesCapital.find { it == moral.regimenCapital },
                            onSeleccion = { seleccion ->
                                dataViewModel.updateMoral { m -> m.copy(regimenCapital = seleccion) }
                            }
                        )

                        CustomComboBox(
                            label = "Actividad Económica",
                            opciones = actividadesSAT,
                            textoOpcion = { opcion -> opcion },
                            seleccionado = actividadesSAT.find { it == moral.actividadEconomica },
                            onSeleccion = { seleccion ->
                                dataViewModel.updateMoral { m -> m.copy(actividadEconomica = seleccion) }
                            }
                        )
                    }
                }
            }

            item { SectionHeader("Domicilio Fiscal") }

            item {
                Card(elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomTextField(
                                value = direccion.cp,
                                onValueChange = { cp -> dataViewModel.updateDireccion { it.copy(cp = cp) } },
                                label = "C.P.", modifier = Modifier.weight(1f)
                            )
                            CustomComboBox(
                                label = "Vialidad",
                                opciones = vialidades,
                                textoOpcion = { it },
                                seleccionado = vialidades.find { it == direccion.tipoVialidad },
                                onSeleccion = { v -> dataViewModel.updateDireccion { it.copy(tipoVialidad = v) } },
                                modifier = Modifier.weight(1.5f)
                            )
                        }
                        CustomComboBox(
                            label = "Estado",
                            opciones = estados,
                            textoOpcion = { it.nombre },
                            seleccionado = estados.find { it.id == direccion.estadoId },
                            onSeleccion = { est ->
                                dataViewModel.updateDireccion { it.copy(estadoId = est.id, municipioId = 0L) }
                            }
                        )

                        CustomComboBox(
                            label = "Municipio",
                            opciones = municipiosDelEstado,
                            textoOpcion = { it.nombre },
                            seleccionado = municipiosDelEstado.find { it.id == direccion.municipioId }, // Esto ahora sí lo encontrará
                            onSeleccion = { mun ->
                                dataViewModel.updateDireccion { it.copy(municipioId = mun.id) }
                            }
                        )
                        CustomTextField(
                            value = direccion.calle,
                            onValueChange = { c -> dataViewModel.updateDireccion { it.copy(calle = c) } },
                            label = "Calle"
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomTextField(
                                value = direccion.numeroExterior,
                                onValueChange = { n -> dataViewModel.updateDireccion { it.copy(numeroExterior = n) } },
                                label = "Num. Ext", modifier = Modifier.weight(1f)
                            )
                            CustomTextField(
                                value = direccion.numeroInterior ?: "",
                                onValueChange = { n -> dataViewModel.updateDireccion { it.copy(numeroInterior = n) } },
                                label = "Num. Int", modifier = Modifier.weight(1f)
                            )
                        }

                        CustomTextField(
                            value = direccion.colonia,
                            onValueChange = { col -> dataViewModel.updateDireccion { it.copy(colonia = col) } },
                            label = "Colonia"
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomTextField(
                                value = direccion.entreCalle1 ?: "",
                                onValueChange = { e1 -> dataViewModel.updateDireccion { it.copy(entreCalle1 = e1) } },
                                label = "Entre Calle 1", modifier = Modifier.weight(1f)
                            )
                            CustomTextField(
                                value = direccion.entreCalle2 ?: "",
                                onValueChange = { e2 -> dataViewModel.updateDireccion { it.copy(entreCalle2 = e2) } },
                                label = "Entre Calle 2", modifier = Modifier.weight(1f)
                            )
                        }

                        CustomTextField(
                            value = direccion.localidad ?: "",
                            onValueChange = { loc -> dataViewModel.updateDireccion { it.copy(localidad = loc) } },
                            label = "Localidad"
                        )

                        CustomTextField(
                            value = direccion.referencias ?: "",
                            onValueChange = { ref -> dataViewModel.updateDireccion { it.copy(referencias = ref) } },
                            label = "Referencias"
                        )

                        CustomTextField(
                            value = direccion.caracteristicas ?: "",
                            onValueChange = { car -> dataViewModel.updateDireccion { it.copy(caracteristicas = car) } },
                            label = "Fachada / Características"
                        )
                    }
                }
            }

            item {
                Button(
                    onClick = {

                        dataViewModel.savePersonaMoral(moral, direccion, fisica, socio, satViewModel)
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("GUARDAR CAMBIOS")
                }
            }
        }
    }
}