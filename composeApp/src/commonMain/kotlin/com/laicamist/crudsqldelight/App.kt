package com.laicamist.crudsqldelight

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.laicamist.crudsqldelight.Ui.screens.HomeScreen
import com.laicamist.crudsqldelight.Ui.screens.homeScreen
import com.laicamist.crudsqldelight.Ui.theme.AppTheme
import com.laicamist.crudsqldelight.Ui.viewModels.pFisicasViewModel
import com.laicamist.crudsqldelight.Ui.viewModels.pMoralesViewModel
import com.laicamist.crudsqldelight.cache.Database
import com.laicamist.crudsqldelight.cache.DatabaseDriverFactory
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import com.laicamist.crudsqldelight.client.NetworkClient
import com.laicamist.crudsqldelight.data.SatRepositoryImpl
import jdk.javadoc.internal.doclets.formats.html.markup.HtmlStyle
import org.jetbrains.compose.resources.stringResource


interface NavigationDestination{
    val route: String
    val titleRes: StringResource
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App(driverFactory: DatabaseDriverFactory) {
    val navController = rememberNavController()
    val httpClient = NetworkClient.httpClient
    val satRepository = remember { SatRepositoryImpl(driverFactory) }
    val viewMPersonasFisicas = remember{pFisicasViewModel(satRepository,httpClient)}
    val viewMPersonasMorelas = remember{ pMoralesViewModel(satRepository,httpClient) }
    // Obtener la pantalla actual para el título
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: homeScreen.route

    // Determinar el título de acuerdo a la pantalla en la que este
    val currentTitle = when (currentRoute) {
        homeScreen.route -> homeScreen.titleRes
        // Aquí iremos agregando las demás rutas conforme las conectemos
        else -> homeScreen.titleRes
    }
    val canNavigateBack = navController.previousBackStackEntry != null

    AppTheme{
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(currentTitle),
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    navigationIcon = {
                        if (canNavigateBack) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = homeScreen.route,
                modifier = Modifier.padding(innerPadding)
            ){
                //Ruta de la pantalla principal
                composable(route = homeScreen.route){
                    HomeScreen(
                        onNavigateToFisicas ={
                            println("Navigating to Fisicas") //Testeo
                        },
                        onNavigateToMorales = {
                            println("Navigating to Morales") //Testeo
                        }
                    )
                }
            }
            Box(modifier = Modifier.padding(innerPadding)) {
                // Aquí conectaremos los composables
            }
        }
    }
}