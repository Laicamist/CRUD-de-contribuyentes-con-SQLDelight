package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.laicamist.crudsqldelight.Ui.viewModels.DataViewModel
import com.laicamist.crudsqldelight.Ui.viewModels.SatViewModel
import com.laicamist.crudsqldelight.Ui.theme.AppTheme
import com.laicamist.crudsqldelight.pantallas
import crudsqldelight.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource

@Composable
fun DetallesFisicaScreen(
    viewModel: SatViewModel,
    dataViewModel: DataViewModel,
    navController: NavController
) {
    val direccion by dataViewModel.direccionState.collectAsState()
    val contribuyente by viewModel.contribuyente.collectAsState()
    var dialogoDeBorrado by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        if (direccion.estadoId != 0L) {
            viewModel.cargarContribuyente(direccion.estadoId)
        }
    }

    AppTheme {
        Scaffold { padding ->
            contribuyente?.let { persona ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 40.dp)
                ) {
                    item { SectionHeader("Información Personal") }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoRow("Nombre completo", "${persona.nombre} ${persona.apellido_paterno} ${persona.apellido_materno ?: ""}")
                                InfoRow("RFC", persona.rfc ?: "N/A")
                                InfoRow("CURP", persona.curp ?: "N/A")
                                InfoRow("Fecha de Nac.", persona.fecha_nacimiento ?: "N/A")
                            }
                        }
                    }

                    item { SectionHeader("Contacto y Régimen") }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                InfoRow("Email", persona.email ?: "N/A")
                                InfoRow("Teléfono", persona.telefono ?: "N/A")
                                InfoRow("Régimen Fiscal", persona.regimen_fiscal ?: "N/A")
                                InfoRow("Actividad Económica", persona.actividad_economica ?: "N/A")
                            }
                        }
                    }
                    item { SectionHeader("Domicilio Fiscal") }
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                val nombreEstado = viewModel.getEstadoById(persona.estado_id).nombre
                                val nombreMunicipio = viewModel.getMunicipioById(persona.municipio_id).nombre

                                InfoRow("Estado", nombreEstado)
                                InfoRow("Municipio", nombreMunicipio)
                                InfoRow("C.P.", persona.cp)
                                InfoRow("Dirección", "${persona.tipo_vialidad ?: ""} ${persona.calle} #${persona.numero_exterior}")

                                if (!persona.numero_interior.isNullOrBlank()) {
                                    InfoRow("Num. Interno", persona.numero_interior)
                                }

                                InfoRow("Colonia", persona.colonia ?: "N/A")

                                if (!persona.entre_calle1.isNullOrBlank() || !persona.entre_calle2.isNullOrBlank()) {
                                    InfoRow("Entre calles", "${persona.entre_calle1 ?: ""} y ${persona.entre_calle2 ?: ""}")
                                }

                                if (!persona.referencia_adicional.isNullOrBlank()) {
                                    InfoRow("Referencias", persona.referencia_adicional)
                                }

                                if (!persona.caracteristicas_domicilio.isNullOrBlank()) {
                                    InfoRow("Características fachada", persona.caracteristicas_domicilio)
                                }

                                if (!persona.localidad.isNullOrBlank()) {
                                    InfoRow("Localidad", persona.localidad)
                                }
                            }
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = {
                                    dataViewModel.prepararEdicion(persona)
                                    navController.navigate(pantallas.editDataFisica.name)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Editar")
                            }
                            Button(
                                onClick = { dialogoDeBorrado = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.error
                                )
                            ) {
                                Text("Borrar")
                            }
                        }
                    }
                }
            } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        if (dialogoDeBorrado) {
            AlertDialog(
                onDismissRequest = { dialogoDeBorrado = false },
                title = { Text("¿Eliminar registro?") },
                text = { Text("Esta acción eliminará permanentemente a ${contribuyente?.nombre}. ¿Deseas continuar?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            contribuyente?.let {
                                viewModel.delete(it)
                                navController.popBackStack()
                            }
                            dialogoDeBorrado = false
                        }
                    ) {
                        Text("Eliminar", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { dialogoDeBorrado = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
    }
}