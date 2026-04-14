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
import cache.ListarFisicasBase
import com.laicamist.crudsqldelight.NavigationDestination
import com.laicamist.crudsqldelight.Ui.viewModels.pFisicasViewModel
import crudsqldelight.composeapp.generated.resources.Res
import crudsqldelight.composeapp.generated.resources.msg_lista_vacia
import crudsqldelight.composeapp.generated.resources.title_listaFisicas
import org.jetbrains.compose.resources.stringResource

// 1. Objeto de navegación (Se queda igual)
object listaFisicas : NavigationDestination {
    override val route: String = "listaFisicas"
    override val titleRes = Res.string.title_listaFisicas
}

// 2. CAMBIO: Nombre con Mayúscula y agregamos @Composable
@Composable
fun ListaFisicasScreen(
    viewModel: pFisicasViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // 3. CAMBIO: Asegúrate que el nombre coincida con tu ViewModel (listaPFisicas)
    val personas by viewModel.listaFisicas.collectAsState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Persona")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (personas.isEmpty()) {
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
                items(personas) { persona ->
                    ItemFisicaCard(
                        persona = persona,
                        onClick = { onNavigateToDetails(persona.rfc) }
                    )
                }
            }
        }
    }
}

@Composable
fun ItemFisicaCard(
    persona: ListarFisicasBase,
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
        Row(
            modifier = Modifier.padding(16.dp),
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
                // CAMBIO: Asegúrate que los nombres de campos coincidan con SQLDelight (nombres/apellidos)
                Text(
                    text = "${persona.nombre} ${persona.apellidos}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = persona.rfc,
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
    }
}