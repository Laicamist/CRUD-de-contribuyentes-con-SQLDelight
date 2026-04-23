package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun ListaMoralesScreen(navController: NavController, viewModel: DataViewModel, satViewModel: SatViewModel) {
    val todosLosContribuyentes by satViewModel.contribuyentes.collectAsState()
    val personasMorales = todosLosContribuyentes.filter {
        it.tipo.equals("Moral", ignoreCase = true)
    }

    viewModel.clearFields()
    satViewModel.loadContribuyentes()
    AppTheme {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate(pantallas.addM.name) },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Empresa")
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            if (personasMorales.isEmpty()) {
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
                    items(personasMorales) { empresa ->
                        ItemMoralCard(
                            empresa = empresa,
                            viewModel = satViewModel,
                            onClick = {
                                satViewModel.cargarContribuyente(empresa.id)
                                navController.navigate(pantallas.detailsM.name)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ItemMoralCard(
    empresa: Contribuyente,
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.padding(12.dp),
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = empresa.razon_social ?: "Sin Razón Social",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = empresa.rfc ?: "Sin RFC",
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
                InfoRowLabel("Régimen Capital", empresa.regimen_capital)
                InfoRowLabel("Régimen Fiscal", empresa.regimen_fiscal)

                // Ubicación
                val estado = viewModel.getEstadoById(empresa.estado_id).nombre
                val municipio = viewModel.getMunicipioById(empresa.municipio_id).nombre
                InfoRowLabel("Ubicación", "$municipio, $estado")

                InfoRowLabel("Dirección", "${empresa.calle} No. ${empresa.numero_exterior}, Col. ${empresa.colonia}")
            }
        }
    }
}