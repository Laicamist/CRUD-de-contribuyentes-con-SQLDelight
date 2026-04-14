package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Para el botón de atrás
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.laicamist.crudsqldelight.NavigationDestination
import com.laicamist.crudsqldelight.Ui.viewModels.pFisicasViewModel
import crudsqldelight.composeapp.generated.resources.Res
import crudsqldelight.composeapp.generated.resources.title_details_fisica

object detailsFisica : NavigationDestination {
    override val route = "detalles_fisica"
    override val titleRes = Res.string.title_details_fisica
    const val rfcArg = "rfc"
    val routeWithArgs = "$route/{$rfcArg}"
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesFisicaScreen(
    rfc: String,
    viewModel: pFisicasViewModel,
    onNavigateBack: () -> Unit
) {
    val detalle by viewModel.detallePersona.collectAsState()
    val direccion by viewModel.dState.collectAsState()
    // Cargar los datos al entrar
    LaunchedEffect(rfc) {
        viewModel.obtenerDetallePersona(rfc)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalles del Contribuyente") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        detalle?.let { persona ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 20.dp)
            ) {
                item { SectionHeader("Información Personal") }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow("Nombre completo", "${persona.nombre} ${persona.apellidos}")
                            InfoRow("RFC", persona.rfc)
                            InfoRow("CURP", persona.curp)
                            InfoRow("Fecha de Nac.", persona.fechaDeNacimiento)
                        }
                    }
                }

                item { SectionHeader("Contacto y Régimen") }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow("Email", persona.email)
                            InfoRow("Teléfono", persona.telefono)
                            InfoRow("Régimen", persona.regFiscal)
                            InfoRow("Actividad", persona.actEconomica)
                        }
                    }
                }

                item { SectionHeader("Domicilio Fiscal") }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Ahora leemos de 'direccion', que es el direccionState del ViewModel
                            InfoRow("C.P.", direccion.cp)

                            // Para ubicación, podrías necesitar una lógica extra para mostrar los nombres
                            // por ahora usamos los IDs o lo que tengas en el state
                            InfoRow("Dirección", "${direccion.tipoVialidad} ${direccion.calle} #${direccion.numeroExterior}")

                            if (direccion.numeroInterior?.isNotBlank() == true) {
                                InfoRow("Num. Interno", direccion.numeroInterior!!)
                            }

                            InfoRow("Colonia", direccion.colonia.ifBlank { "N/A" })

                            if (direccion.referencias.isNotBlank()) {
                                InfoRow("Referencias", direccion.referencias)
                            }
                        }
                    }
                }
            }
        } ?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
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
