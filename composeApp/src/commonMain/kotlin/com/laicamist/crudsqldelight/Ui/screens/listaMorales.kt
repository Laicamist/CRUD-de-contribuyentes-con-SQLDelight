package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cache.ListarMoralesBase
import com.laicamist.crudsqldelight.NavigationDestination
import com.laicamist.crudsqldelight.Ui.viewModels.pMoralesViewModel
import crudsqldelight.composeapp.generated.resources.Res
import crudsqldelight.composeapp.generated.resources.msg_lista_vacia
import crudsqldelight.composeapp.generated.resources.title_listaMorales
import org.jetbrains.compose.resources.stringResource

object listaMorales : NavigationDestination{
    override val route: String = "listaMorales"
    override val titleRes = Res.string.title_listaMorales
}
@Composable
fun ListaMoralesScreen(
    viewModel: pMoralesViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val empresas by viewModel.listaMorales.collectAsState()
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAdd,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Añadir Empresa")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        if (empresas.isEmpty()) {
            // Estado vacío: se muestra cuando no hay datos
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
            // Lista con datos
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(empresas) { empresa ->
                    ItemMoralCard(
                        empresa = empresa,
                        onClick = { onNavigateToDetails(empresa.rfc) }
                    )
                }
            }
        }
    }
}
@Composable
fun ItemMoralCard(
    empresa: ListarMoralesBase, // Clase generada por SQLDelight
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
            // Icono con fondo circular sutil
            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
            ) {
                Icon(
                    imageVector = Icons.Default.Place, // Usamos Place en lugar de Business
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = empresa.denominacionORazonSocial,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = empresa.rfc,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary, // Usamos tu color primario
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