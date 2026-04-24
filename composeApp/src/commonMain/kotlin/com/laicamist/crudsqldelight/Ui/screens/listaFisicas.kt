package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import cache.Contribuyente
import com.laicamist.crudsqldelight.Ui.theme.AppTheme
import com.laicamist.crudsqldelight.Ui.viewModels.DataViewModel
import com.laicamist.crudsqldelight.Ui.viewModels.SatViewModel
import com.laicamist.crudsqldelight.pantallas
import crudsqldelight.composeapp.generated.resources.Res
import crudsqldelight.composeapp.generated.resources.msg_lista_vacia
import crudsqldelight.composeapp.generated.resources.title_listaFisicas
import org.jetbrains.compose.resources.stringResource

@Composable
fun ListaFisicasScreen(navController: NavController, viewModel: DataViewModel, satViewModel: SatViewModel) {
    // Enlistado de todos los contribuyentes filtrando por tipo
    val todosLosContribuyentes by satViewModel.contribuyentes.collectAsState()
    val personasFisicas = todosLosContribuyentes.filter {
        it.tipo.equals("Fisica", ignoreCase = true)
    }
    satViewModel.loadContribuyentes()
    viewModel.clearFields()
    AppTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(pantallas.addF.name) },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Persona")
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            if (personasFisicas.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.msg_lista_vacia),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(personasFisicas) { persona ->
                        ItemFisicaCard(
                            persona = persona,
                            viewModel = satViewModel,
                            onClick = {
                                satViewModel.cargarContribuyente(persona.id)
                                navController.navigate(pantallas.detailsF.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemFisicaCard(
    persona: Contribuyente,
    viewModel: SatViewModel,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Cabecera: Nombre y CURP (Tu diseño original)
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val nombreCompleto = "${persona.nombre} ${persona.apellido_paterno} ${persona.apellido_materno ?: ""}"
                    Text(
                        text = nombreCompleto.trim(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = persona.curp ?: "Sin CURP",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                InfoRowLabel("Email", persona.email)
                InfoRowLabel("Teléfono", persona.telefono)
                InfoRowLabel("Régimen", persona.regimen_fiscal)
                val estado = viewModel.getEstadoById(persona.estado_id).nombre
                val municipio = viewModel.getMunicipioById(persona.municipio_id).nombre
                InfoRowLabel("Ubicación", "$municipio, $estado")

                InfoRowLabel("Dirección", "${persona.calle} No. ${persona.numero_exterior}, Col. ${persona.colonia}")
            }
        }
    }
}

@Composable
fun InfoRowLabel(label: String, value: String?) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value ?: "N/A",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}