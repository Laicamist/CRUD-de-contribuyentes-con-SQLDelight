package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.laicamist.crudsqldelight.Ui.theme.AppTheme
import com.laicamist.crudsqldelight.Ui.viewModels.DataViewModel
import com.laicamist.crudsqldelight.Ui.viewModels.SatViewModel
import crudsqldelight.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFisicaScreen(
    dataViewModel: DataViewModel,
    satViewModel: SatViewModel,
    navController: NavController
) {
    val contribuyente by satViewModel.contribuyente.collectAsState()
    val fisica by dataViewModel.fisicaState.collectAsState()
    val direccion by dataViewModel.direccionState.collectAsState()

    LaunchedEffect(contribuyente) {
        contribuyente?.let { dataViewModel.prepararEdicion(it) }
    }

    val estados by satViewModel.estados.collectAsState()
    val municipiosMap by satViewModel.municipios.collectAsState()
    val municipiosDelEstado = municipiosMap[direccion.estadoId] ?: emptyList()

    val regimenes = listOf("Sueldos y Salarios","Ingresos Asimilados a Salarios","Enajenación de Bienes"
        ,"Ingresos por Dividendos","RESICO", "Actividad Empresarial", "Arrendamiento", "Servicios Profesionales")
    val vialidades = listOf("Calle", "Avenida", "Boulevard", "Circuito", "Privada")

    AppTheme {
        Scaffold { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 40.dp)
            ) {
                item {
                    SectionHeader("Datos Personales Modificables")
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Card(
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CustomTextField(fisica.nombre, { dataViewModel.updateFisica { f -> f.copy(nombre = it) } }, "Nombre")
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                CustomTextField(
                                    value = fisica.rfc,
                                    onValueChange = { if (it.length <= 13) dataViewModel.updateFisica { f -> f.copy(rfc = it.uppercase()) } },
                                    label = "RFC",
                                    modifier = Modifier.weight(1f),
                                    maxChar = 13
                                )
                                CustomTextField(
                                    value = fisica.curp,
                                    onValueChange = { if (it.length <= 18) dataViewModel.updateFisica { f -> f.copy(curp = it.uppercase()) } },
                                    label = "CURP",
                                    modifier = Modifier.weight(1.2f),
                                    maxChar = 18
                                )
                            }

                            CustomTextField(fisica.email, { dataViewModel.updateFisica { f -> f.copy(email = it) } }, "Email")

                            CustomTextField(
                                value = fisica.telefono,
                                onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 10) dataViewModel.updateFisica { f -> f.copy(telefono = it) } },
                                label = "Teléfono",
                                maxChar = 10
                            )

                            CustomComboBox("Régimen Fiscal", regimenes, { it }, regimenes.find { it == fisica.regFiscal }, {
                                dataViewModel.updateFisica { f -> f.copy(regFiscal = it) }
                            })
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }

                item {
                    SectionHeader("Domicilio Fiscal Completo")
                    Spacer(modifier = Modifier.height(8.dp))
                }

                item {
                    Card(
                        elevation = CardDefaults.cardElevation(2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CustomComboBox("Estado", estados, { it.nombre }, estados.find { it.id == direccion.estadoId }, { est ->
                                dataViewModel.updateDireccion { it.copy(estadoId = est.id, municipioId = 0L) }
                            })

                            CustomComboBox("Municipio", municipiosDelEstado, { it.nombre }, municipiosDelEstado.find { it.id == direccion.municipioId }, { mun ->
                                dataViewModel.updateDireccion { it.copy(municipioId = mun.id) }
                            })

                            CustomTextField(direccion.cp, { if (it.length <= 5) dataViewModel.updateDireccion { d -> d.copy(cp = it) } }, "C.P.", maxChar = 5)

                            CustomComboBox(
                                label = "Vialidad",
                                opciones = vialidades,
                                textoOpcion = { it },
                                seleccionado = vialidades.find { it == direccion.tipoVialidad },
                                onSeleccion = { nuevaVialidad ->
                                    dataViewModel.updateDireccion { estadoActual ->
                                        estadoActual.copy(tipoVialidad = nuevaVialidad)
                                    }
                                }
                            )

                            CustomTextField(direccion.calle, { dataViewModel.updateDireccion { d -> d.copy(calle = it) } }, "Nombre de Vialidad / Calle")

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                CustomTextField(direccion.numeroExterior, { dataViewModel.updateDireccion { d -> d.copy(numeroExterior = it) } }, "Num. Ext", Modifier.weight(1f))
                                CustomTextField(direccion.numeroInterior ?: "", { dataViewModel.updateDireccion { d -> d.copy(numeroInterior = it) } }, "Num. Int", Modifier.weight(1f))
                            }

                            CustomTextField(direccion.colonia, { dataViewModel.updateDireccion { d -> d.copy(colonia = it) } }, "Colonia")

                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp)

                            CustomTextField(direccion.entreCalle1 ?: "", { dataViewModel.updateDireccion { d -> d.copy(entreCalle1 = it) } }, "Entre Calle 1")
                            CustomTextField(direccion.entreCalle2 ?: "", { dataViewModel.updateDireccion { d -> d.copy(entreCalle2 = it) } }, "Entre Calle 2")
                            CustomTextField(direccion.referencias ?: "", { dataViewModel.updateDireccion { d -> d.copy(referencias = it) } }, "Referencia Adicional")
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(24.dp)) }

                item {
                    Button(
                        onClick = {
                            dataViewModel.savePersonaFisica(fisica, direccion, satViewModel)
                            navController.popBackStack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                    ) {
                        Text("GUARDAR CAMBIOS", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}