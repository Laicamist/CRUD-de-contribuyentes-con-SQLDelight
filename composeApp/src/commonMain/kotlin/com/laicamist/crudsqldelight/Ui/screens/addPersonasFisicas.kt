package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laicamist.crudsqldelight.Ui.viewModels.DataViewModel
import com.laicamist.crudsqldelight.Ui.viewModels.SatViewModel
import crudsqldelight.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFisicaScreen(
    dataViewModel: DataViewModel,
    satViewModel: SatViewModel,
    onNavigateBack: () -> Unit
) {

    val fisica by dataViewModel.fisicaState.collectAsState()
    val direccion by dataViewModel.direccionState.collectAsState()
    val identificacionCompleta = fisica.nombre.isNotBlank() &&
            fisica.apellidos.isNotBlank() &&
            fisica.rfc.length == 13 &&
            fisica.curp.length == 18 &&
            fisica.regFiscal.isNotBlank() &&
            fisica.telefono.length == 10

    val domicilioCompleto = direccion.estadoId != 0L &&
            direccion.municipioId != 0L &&
            direccion.cp.length == 5 &&
            direccion.calle.isNotBlank() &&
            direccion.colonia.isNotBlank() &&
            direccion.tipoVialidad.isNotBlank()

    val estados by satViewModel.estados.collectAsState()
    val municipiosMap by satViewModel.municipios.collectAsState()
    val municipiosDelEstado = municipiosMap[direccion.estadoId] ?: emptyList()

    val regimenes = listOf("Sueldos y Salarios","Ingresos Asimilados a Salarios","Enajenación de Bienes"
        ,"Ingresos por Dividendos","RESICO", "Actividad Empresarial", "Arrendamiento", "Servicios Profesionales")
    val vialidades = listOf("Calle", "Avenida", "Boulevard", "Circuito", "Privada")

    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
        ) {
            item { SectionHeader("Datos de Identificación") }

            item {
                Card(elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CustomTextField(
                            value = fisica.nombre,
                            onValueChange = { nuevo -> dataViewModel.updateFisica { it.copy(nombre = nuevo) } },
                            label = stringResource(Res.string.label_nombre)
                        )

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomTextField(
                                value = fisica.apellidos,
                                onValueChange = { nuevo -> dataViewModel.updateFisica { it.copy(apellidos = nuevo) } },
                                label = stringResource(Res.string.label_apellidos),
                                modifier = Modifier.weight(1f)
                            )
                        }
                        CustomTextField(
                            value = fisica.rfc,
                            onValueChange = { nuevo ->
                                if (nuevo.length <= 13) dataViewModel.updateFisica { it.copy(rfc = nuevo.uppercase()) }
                            },
                            label = stringResource(Res.string.label_rfc),
                            maxChar = 13
                        )

                        CustomTextField(
                            value = fisica.curp,
                            onValueChange = { nuevo ->
                                if (nuevo.length <= 18) dataViewModel.updateFisica { it.copy(curp = nuevo.uppercase()) }
                            },
                            label = stringResource(Res.string.label_curp),
                            maxChar = 18
                        )

                        CustomTextField(
                            value = fisica.fechaDeNacimiento,
                            onValueChange = { nuevo ->
                                val soloNumeros = nuevo.filter { it.isDigit() }
                                val formateado = buildString {
                                    for (i in soloNumeros.indices) {
                                        append(soloNumeros[i])
                                        if ((i == 1 || i == 3) && i != soloNumeros.lastIndex) {
                                            append("/")
                                        }
                                    }
                                }
                                if (formateado.length <= 10) {
                                    dataViewModel.updateFisica { it.copy(fechaDeNacimiento = formateado) }
                                }
                            },
                            label = stringResource(Res.string.label_fecha_nac),
                        )

                        CustomTextField(
                            value = fisica.email,
                            onValueChange = { nuevo -> dataViewModel.updateFisica { it.copy(email = nuevo) } },
                            label = stringResource(Res.string.label_email)
                        )

                        CustomTextField(
                            value = fisica.telefono,
                            onValueChange = { nuevo ->
                                if (nuevo.all { it.isDigit() } && nuevo.length <= 10) {
                                    dataViewModel.updateFisica { it.copy(telefono = nuevo) }
                                }
                            },
                            label = stringResource(Res.string.label_tel),
                            maxChar = 10
                        )

                        CustomComboBox(
                            label = stringResource(Res.string.label_reg_fiscal),
                            opciones = regimenes,
                            textoOpcion = { it },
                            seleccionado = regimenes.find { it == fisica.regFiscal },
                            onSeleccion = { nuevo -> dataViewModel.updateFisica { it.copy(regFiscal = nuevo) } }
                        )

                        CustomComboBox(
                            label = stringResource(Res.string.label_act_econ),
                            opciones = listOf("Servicios Profesionales","Comercio al por Menor","Servicios de Hostelería y Preparación de Alimentos"
                                ,"Servicios Educativos","Servicios de Transporte","Actividades Agrícolas y Ganaderas","Servicios de Salud","Construcción", "Servicios inmobiliarios"),
                            textoOpcion = { it },
                            seleccionado = fisica.actEconomica.takeIf { it.isNotBlank() },
                            onSeleccion = { nuevo -> dataViewModel.updateFisica { it.copy(actEconomica = nuevo) } }
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
                                    onValueChange = { nuevo ->
                                        if (nuevo.all { it.isDigit() } && nuevo.length <= 5) {
                                            dataViewModel.updateDireccion { it.copy(cp = nuevo) }
                                        }
                                    },
                                    label = stringResource(Res.string.label_cp),
                                    modifier = Modifier.weight(1f),
                                    maxChar = 5
                                )
                                CustomComboBox(
                                    label = "Vialidad",
                                    opciones = vialidades,
                                    textoOpcion = { it },
                                    seleccionado = vialidades.find { it == direccion.tipoVialidad },
                                    onSeleccion = { nuevo -> dataViewModel.updateDireccion { it.copy(tipoVialidad = nuevo) } },
                                    modifier = Modifier.weight(1.5f)
                                )
                            }

                            CustomComboBox(
                                label = stringResource(Res.string.label_estado),
                                opciones = estados,
                                textoOpcion = { it.nombre },
                                seleccionado = estados.find { it.id == direccion.estadoId },
                                onSeleccion = { est ->
                                    dataViewModel.updateDireccion { it.copy(estadoId = est.id, municipioId = 0L) }
                                }
                            )

                            CustomComboBox(
                                label = stringResource(Res.string.label_municipio),
                                opciones = municipiosDelEstado,
                                textoOpcion = { it.nombre },
                                seleccionado = municipiosDelEstado.find { it.id == direccion.municipioId },
                                onSeleccion = { mun ->
                                    dataViewModel.updateDireccion { it.copy(municipioId = mun.id) }
                                }
                            )

                            CustomTextField(
                                value = direccion.calle,
                                onValueChange = { nuevo -> dataViewModel.updateDireccion { it.copy(calle = nuevo) } },
                                label = stringResource(Res.string.label_calle)
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(
                                    value = direccion.numeroExterior,
                                    onValueChange = { nuevo -> dataViewModel.updateDireccion { it.copy(numeroExterior = nuevo) } },
                                    label = stringResource(Res.string.label_num_ext),
                                    modifier = Modifier.weight(1f)
                                )
                                CustomTextField(
                                    value = direccion.numeroInterior ?: "",
                                    onValueChange = { nuevo -> dataViewModel.updateDireccion { it.copy(numeroInterior = nuevo) } },
                                    label = stringResource(Res.string.label_num_int),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            CustomTextField(
                                value = direccion.colonia,
                                onValueChange = { nuevo -> dataViewModel.updateDireccion { it.copy(colonia = nuevo) } },
                                label = stringResource(Res.string.label_colonia)
                            )
                        }
                    }
                }
                item {
                    Card(elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(
                                    value = direccion.entreCalle1 ?: "",
                                    onValueChange = { nuevo -> dataViewModel.updateDireccion { it.copy(entreCalle1 = nuevo) } },
                                    label = "Entre Calle 1",
                                    modifier = Modifier.weight(1f)
                                )
                                CustomTextField(
                                    value = direccion.entreCalle2 ?: "",
                                    onValueChange = { nuevo -> dataViewModel.updateDireccion { it.copy(entreCalle2 = nuevo) } },
                                    label = "Entre Calle 2",
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            CustomTextField(
                                value = direccion.referencias ?: "",
                                onValueChange = { nuevo -> dataViewModel.updateDireccion { it.copy(referencias = nuevo) } },
                                label = "Referencias Visuales / Adicionales",
                                supportingText = "Ej: Portón café, frente a parque"
                            )

                            CustomTextField(
                                value = direccion.caracteristicas ?: "",
                                onValueChange = { nuevo -> dataViewModel.updateDireccion { it.copy(caracteristicas = nuevo) } },
                                label = "Características del Domicilio",
                                supportingText = "Descripción física de la fachada"
                            )
                        }
                    }
                }
                item {
                    Button(
                        onClick = {
                            dataViewModel.addPersonaFisica()
                            onNavigateBack()
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = domicilioCompleto
                    ) {
                        Text(stringResource(Res.string.btn_guardar))
                    }
                }
            }
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    maxChar: Int? = null,
    isError: Boolean = false,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (maxChar == null || it.length <= maxChar) onValueChange(it)
        },
        label = { Text(label) },
        isError = isError,
        supportingText = supportingText?.let { { Text(it) } },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> CustomComboBox(
    label: String,
    opciones: List<T>,
    textoOpcion: (T) -> String,
    seleccionado: T?,
    onSeleccion: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = seleccionado?.let { textoOpcion(it) } ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            opciones.forEach { opcion ->
                DropdownMenuItem(
                    text = { Text(textoOpcion(opcion)) },
                    onClick = {
                        onSeleccion(opcion)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}
