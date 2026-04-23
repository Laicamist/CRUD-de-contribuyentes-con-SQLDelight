package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.laicamist.crudsqldelight.Ui.theme.AppTheme
import com.laicamist.crudsqldelight.Ui.viewModels.DataViewModel
import com.laicamist.crudsqldelight.pantallas
import crudsqldelight.composeapp.generated.resources.Res
import crudsqldelight.composeapp.generated.resources.app_name
import crudsqldelight.composeapp.generated.resources.btn_fisicas
import crudsqldelight.composeapp.generated.resources.btn_morales
import crudsqldelight.composeapp.generated.resources.title_home
import org.jetbrains.compose.resources.stringResource


@Composable
fun homeScreen(navController: NavHostController, viewModel: DataViewModel){
    LaunchedEffect(Unit){
        viewModel.clearFields()
    }
    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título Principal con Inter Bold
            Text(
                text = stringResource(Res.string.app_name),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            // Tarjeta de acceso a Personas Físicas
            MenuOptionCard(
                title = stringResource(Res.string.btn_fisicas),
                icon = Icons.Default.Person,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                onClick = { navController.navigate(pantallas.listaF.name) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Tarjeta de acceso a Personas Morales
            MenuOptionCard(
                title = stringResource(Res.string.btn_morales),
                icon = Icons.Default.Home, // O Icons.Default.Build si prefieres herramientas
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                onClick = { navController.navigate(pantallas.listaM.name) }
            )
        }
    }
}

@Composable
fun MenuOptionCard(
    title: String,
    icon: ImageVector,
    containerColor: androidx.compose.ui.graphics.Color,
    contentColor: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit
) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = containerColor,
            contentColor = contentColor
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Círculo decorativo para el icono
            Surface(
                modifier = Modifier.size(52.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.4f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp).size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold // Usará Inter SemiBold de tu Typography
            )
        }
    }
}