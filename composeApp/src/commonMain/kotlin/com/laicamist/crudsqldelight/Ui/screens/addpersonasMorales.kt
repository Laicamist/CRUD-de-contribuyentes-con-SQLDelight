package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.laicamist.crudsqldelight.Ui.viewModels.DataViewModel
import com.laicamist.crudsqldelight.Ui.viewModels.SatViewModel
import crudsqldelight.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMoralesScreen(
    dataViewModel: DataViewModel,
    satViewModel: SatViewModel,
    onNavigateBack: () -> Unit
) {
    val moral by dataViewModel.moralState.collectAsState()
    val direccion by dataViewModel.direccionState.collectAsState()
    val socio by dataViewModel.socioState.collectAsState()

    val estados by satViewModel.estados.collectAsState()
    val municipiosMap by satViewModel.municipios.collectAsState()
    val municipiosDelEstado = municipiosMap[direccion.estadoId] ?: emptyList()

    val vialidades = listOf("Calle", "Avenida", "Boulevard", "Circuito", "Privada")
    val opcionesCapital = listOf(
        "S.A. de C.V.",           // Sociedad Anónima de Capital Variable (La más común)
        "S.A.",                   // Sociedad Anónima
        "S. de R.L. de C.V.",     // Sociedad de Responsabilidad Limitada de Capital Variable
        "S.A.P.I. de C.V.",       // Sociedad Anónima Promotora de Inversión
        "S.C.",                   // Sociedad Civil (Común en despachos y escuelas)
        "A.C.",                   // Asociación Civil (Entidades sin fines de lucro)
        "S.A.S.",                 // Sociedad por Acciones Simplificada (Empresas de un solo dueño)
        "S.C. de R.L.",           // Sociedad Cooperativa de Responsabilidad Limitada
        "S.C.P.",                 // Sociedad Civil Particular
        "S.N.C."                  // Sociedad en Nombre Colectivo
    )
    val actividadesSAT = listOf(
        "Comercio al por mayor",
        "Comercio al por menor",
        "Servicios Profesionales, Científicos y Técnicos",
        "Construcción y Desarrollo Inmobiliario",
        "Industria Manufacturera",
        "Servicios de Salud y Asistencia Social",
        "Servicios Educativos",
        "Servicios de Alojamiento y Preparación de Alimentos",
        "Transporte, Correos y Almacenamiento",
        "Tecnologías de la Información y Medios Masivos",
        "Agricultura, Ganadería y Pesca",
        "Servicios Financieros y de Seguros",
        "Actividades Legislativas y Gubernamentales",
        "Minería y Extracción de Hidrocarburos"
    )

    val identificacionCompleta = moral.denominacionORazonSocial.isNotBlank() &&
            moral.rfc.length == 12 &&
            moral.fechaDeConstitucion.length == 10

    val domicilioCompleto = direccion.estadoId != 0L &&
            direccion.municipioId != 0L &&
            direccion.cp.length == 5 &&
            direccion.calle.isNotBlank()

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item { SectionHeader("Información Inicial de la Empresa") }

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
                                    val soloNumeros = fecha.filter { it.isDigit() }
                                    val formateado = buildString {
                                        for (i in soloNumeros.indices) {
                                            append(soloNumeros[i])
                                            if ((i == 1 || i == 3) && i != soloNumeros.lastIndex) append("/")
                                        }
                                    }
                                    if (formateado.length <= 10) {
                                        dataViewModel.updateMoral { m -> m.copy(fechaDeConstitucion = formateado) }
                                    }
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

            if (identificacionCompleta) {
                item { SectionHeader(stringResource(Res.string.title_add_domicilio)) }

                item {
                    Card(elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(
                                    value = direccion.cp,
                                    onValueChange = { cpNuevo ->
                                        if (cpNuevo.all { it.isDigit() } && cpNuevo.length <= 5) {
                                            dataViewModel.updateDireccion { d -> d.copy(cp = cpNuevo) }
                                        }
                                    },
                                    label = stringResource(Res.string.label_cp),
                                    modifier = Modifier.weight(1f),
                                    maxChar = 5
                                )
                                CustomComboBox(
                                    label = "Vialidad",
                                    opciones = vialidades,
                                    textoOpcion = { v -> v },
                                    seleccionado = vialidades.find { it == direccion.tipoVialidad },
                                    onSeleccion = { vialidad ->
                                        dataViewModel.updateDireccion { d -> d.copy(tipoVialidad = vialidad) }
                                    },
                                    modifier = Modifier.weight(1.5f)
                                )
                            }

                            CustomComboBox(
                                label = stringResource(Res.string.label_estado),
                                opciones = estados,
                                textoOpcion = { e -> e.nombre },
                                seleccionado = estados.find { it.id == direccion.estadoId },
                                onSeleccion = { est ->
                                    dataViewModel.updateDireccion { d -> d.copy(estadoId = est.id, municipioId = 0L) }
                                }
                            )

                            CustomComboBox(
                                label = stringResource(Res.string.label_municipio),
                                opciones = municipiosDelEstado,
                                textoOpcion = { m -> m.nombre },
                                seleccionado = municipiosDelEstado.find { it.id == direccion.municipioId },
                                onSeleccion = { mun ->
                                    dataViewModel.updateDireccion { d -> d.copy(municipioId = mun.id) }
                                }
                            )

                            CustomTextField(
                                value = direccion.calle,
                                onValueChange = { calle ->
                                    dataViewModel.updateDireccion { d -> d.copy(calle = calle) }
                                },
                                label = stringResource(Res.string.label_calle)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(
                                    value = direccion.numeroExterior,
                                    onValueChange = { nExt ->
                                        dataViewModel.updateDireccion { d -> d.copy(numeroExterior = nExt) }
                                    },
                                    label = stringResource(Res.string.label_num_ext),
                                    modifier = Modifier.weight(1f)
                                )
                                CustomTextField(
                                    value = direccion.numeroInterior ?: "",
                                    onValueChange = { nInt ->
                                        dataViewModel.updateDireccion { d -> d.copy(numeroInterior = nInt) }
                                    },
                                    label = stringResource(Res.string.label_num_int),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            CustomTextField(
                                value = direccion.colonia,
                                onValueChange = { col ->
                                    dataViewModel.updateDireccion { d -> d.copy(colonia = col) }
                                },
                                label = stringResource(Res.string.label_colonia)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(
                                    value = direccion.entreCalle1 ?: "",
                                    onValueChange = { e1 ->
                                        dataViewModel.updateDireccion { d -> d.copy(entreCalle1 = e1) }
                                    },
                                    label = "Entre Calle 1",
                                    modifier = Modifier.weight(1f)
                                )
                                CustomTextField(
                                    value = direccion.entreCalle2 ?: "",
                                    onValueChange = { e2 ->
                                        dataViewModel.updateDireccion { d -> d.copy(entreCalle2 = e2) }
                                    },
                                    label = "Entre Calle 2",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            CustomTextField(
                                value = direccion.localidad ?: "",
                                onValueChange = { loc ->
                                    dataViewModel.updateDireccion { d -> d.copy(localidad = loc) }
                                },
                                label = "Localidad"
                            )

                            CustomTextField(
                                value = direccion.referencias ?: "",
                                onValueChange = { ref ->
                                    dataViewModel.updateDireccion { d -> d.copy(referencias = ref) }
                                },
                                label = "Referencias Adicionales"
                            )

                            CustomTextField(
                                value = direccion.caracteristicas ?: "",
                                onValueChange = { car ->
                                    dataViewModel.updateDireccion { d -> d.copy(caracteristicas = car) }
                                },
                                label = "Características de la Fachada"
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = {
                            dataViewModel.addPersonaMoral()
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = domicilioCompleto
                    ) {
                        Text("GUARDAR PERSONA MORAL")
                    }
                }
            }
        }
    }
}