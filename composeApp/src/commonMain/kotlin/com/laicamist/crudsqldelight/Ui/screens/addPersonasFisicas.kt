package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laicamist.crudsqldelight.NavigationDestination
import com.laicamist.crudsqldelight.Ui.viewModels.pFisicasViewModel
import crudsqldelight.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

object AddFisicaDestination : NavigationDestination {
    override val route = "add_fisica"
    override val titleRes = Res.string.title_add_fisica
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFisicaScreen(
    viewModel: pFisicasViewModel,
    onNavigateBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.resetearFormulario()
    }
    val fisicaState by viewModel.pfisicaState.collectAsState()
    val direccionState by viewModel.dState.collectAsState()
    val estados by viewModel.estados.collectAsState()
    val municipios by viewModel.municipios.collectAsState()

    val canProceedToDir by viewModel.desbloquearDomicilio.collectAsState()
    val canSave by viewModel.desbloquearGuardar.collectAsState()

    // Opciones para ComboBoxes simples
    val regimenes = listOf("Sueldos y Salarios","Ingresos Asimilados a Salarios","Enajenación de Bienes"
        ,"Ingresos por Dividendos","RESICO", "Actividad Empresarial", "Arrendamiento", "Servicios Profesionales")
    val vialidades = listOf("Calle", "Avenida", "Boulevard", "Circuito", "Privada")
    val actEconomica = listOf("Servicios Profesionales","Comercio al por Menor","Servicios de Hostelería y Preparación de Alimentos"
        ,"Servicios Educativos","Servicios de Transporte","Actividades Agrícolas y Ganaderas","Servicios de Salud","Construcción", "Servicios inmobiliarios")
    LaunchedEffect(fisicaState.guardadoExitoso) {
        if (fisicaState.guardadoExitoso) onNavigateBack()
    }

    Scaffold() { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // IDENTIFICACIÓN
            item { SectionHeader("Datos de Identificación") }
            item {
                Card(elevation = CardDefaults.cardElevation(2.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        CustomTextField(fisicaState.nombre, { viewModel.onFisicaChange("nombre", it) }, "Nombre(s)")
                        CustomTextField(fisicaState.apellidos, { viewModel.onFisicaChange("apellidos", it) }, "Apellidos")
                        //Custom textfield que se deivide en 2 a lo largo del espacio de un textfield "normal"
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CustomTextField(fisicaState.rfc, { viewModel.onFisicaChange("rfc", it.uppercase()) }, "RFC", Modifier.weight(1f), 13)
                            CustomTextField(fisicaState.curp, { viewModel.onFisicaChange("curp", it.uppercase()) }, "CURP", Modifier.weight(1.2f), 18)
                        }
                        // Fecha con Máscara Automática
                        CustomTextField(
                            value = fisicaState.fechaDeNacimiento,
                            onValueChange = { input ->
                                val digits = input.filter { it.isDigit() }
                                val formatted = when {
                                    digits.length <= 2 -> digits
                                    digits.length <= 4 -> "${digits.substring(0, 2)}/${digits.substring(2)}"
                                    else -> "${digits.substring(0, 2)}/${digits.substring(2, 4)}/${digits.substring(4, minOf(digits.length, 8))}"
                                }
                                viewModel.onFisicaChange("fechaNac", formatted)
                            },
                            label = "Fecha de Nacimiento (DD/MM/AAAA)",
                            maxChar = 10
                        )

                        CustomTextField(fisicaState.email, { viewModel.onFisicaChange("email", it) }, "Correo Electrónico")
                        CustomTextField(fisicaState.telefono, { viewModel.onFisicaChange("tel", it) }, "Teléfono (10 dígitos)", maxChar = 10)
                        //Combo boxes
                        CustomComboBox("Régimen Fiscal", regimenes, { it }, regimenes.find { it == fisicaState.regFiscal }, { viewModel.onFisicaChange("regFiscal", it) })
                        CustomComboBox(label = "Actividad Económica", opciones = actEconomica, textoOpcion = { it }, seleccionado = actEconomica.find { it == fisicaState.actEconomica }, onSeleccion = { viewModel.onFisicaChange("actEcon", it) })
                    }
                }
            }

            // --- SECCIÓN 2: DOMICILIO (Solo aparece si la Sección 1 es válida) ---
            if (canProceedToDir) {
                item { SectionHeader("Domicilio Fiscal") }
                item {
                    Card(elevation = CardDefaults.cardElevation(2.dp)) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(direccionState.cp, { viewModel.onDireccionChange("cp", it) }, "C.P.", Modifier.weight(1f), 5)
                                CustomComboBox("Vialidad", vialidades, { it }, vialidades.find { it == direccionState.tipoVialidad }, { viewModel.onDireccionChange("vialidad", it) }, Modifier.weight(1.5f))
                            }

                            CustomComboBox("Estado", estados, { it.nombre }, estados.find { it.id == direccionState.estadoId }, { viewModel.onDireccionChange("estadoId", it.id) })

                            CustomComboBox("Municipio", municipios, { it.nombre }, municipios.find { it.id == direccionState.municipioId }, { viewModel.onDireccionChange("municipioId", it.id) })

                            CustomTextField(direccionState.calle, { viewModel.onDireccionChange("calle", it) }, "Calle")

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(direccionState.numeroExterior, { viewModel.onDireccionChange("nExt", it) }, "Ext.", Modifier.weight(1f))
                                CustomTextField(direccionState.numeroInterior ?: "", { viewModel.onDireccionChange("nInt", it) }, "Int.", Modifier.weight(1f))
                            }

                            CustomTextField(direccionState.colonia, { viewModel.onDireccionChange("colonia", it) }, "Colonia")

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CustomTextField(direccionState.entreCalle1, { viewModel.onDireccionChange("entre1", it) }, "Entre calle 1", Modifier.weight(1f))
                                CustomTextField(direccionState.entreCalle2, { viewModel.onDireccionChange("entre2", it) }, "Entre calle 2", Modifier.weight(1f))
                            }

                            CustomTextField(direccionState.referencias, { viewModel.onDireccionChange("ref", it) }, "Referencias")
                        }
                    }
                }

                // --- BOTÓN DE GUARDADO ---
                item {
                    Button(
                        onClick = { viewModel.guardarTodo() },
                        enabled = canSave,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("GUARDAR REGISTRO")
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
    maxChar: Int? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (maxChar == null || it.length <= maxChar) onValueChange(it)
        },
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge
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
            shape = RoundedCornerShape(12.dp)
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
                    }
                )
            }
        }
    }
}