package com.laicamist.crudsqldelight

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.laicamist.crudsqldelight.Ui.screens.AddFisicaScreen
import com.laicamist.crudsqldelight.Ui.screens.DetallesFisicaScreen
import com.laicamist.crudsqldelight.Ui.screens.ListaFisicasScreen
import com.laicamist.crudsqldelight.Ui.screens.ListaMoralesScreen
import com.laicamist.crudsqldelight.Ui.screens.homeScreen
import com.laicamist.crudsqldelight.Ui.theme.AppTheme
import com.laicamist.crudsqldelight.Ui.viewModels.DataViewModel
import com.laicamist.crudsqldelight.Ui.viewModels.SatViewModel
import com.laicamist.crudsqldelight.cache.Database
import com.laicamist.crudsqldelight.cache.DatabaseDriverFactory
import crudsqldelight.composeapp.generated.resources.Res
import crudsqldelight.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.laicamist.crudsqldelight.Ui.screens.AddMoralesScreen
import com.laicamist.crudsqldelight.Ui.screens.DetallesMoralScreen
import com.laicamist.crudsqldelight.Ui.screens.EditFisicaScreen
import com.laicamist.crudsqldelight.Ui.screens.EditMoralesScreen

enum class pantallas(val titulo : StringResource) {
    inicio(titulo = Res.string.app_name),
    listaF(titulo = Res.string.title_listaFisicas),
    listaM(titulo = Res.string.title_listaMorales),
    addF(titulo = Res.string.title_add_fisica),
    addM(titulo = Res.string.title_add_moral),
    detailsF(titulo = Res.string.title_details_fisica),
    detailsM(titulo = Res.string.title_details_moral),
    editDataMoral(titulo = Res.string.title_edit_moral),
    editDataFisica(titulo = Res.string.title_edit_fisica)
}
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun App(driverFactory: DatabaseDriverFactory) {
    val database = remember { Database(driverFactory) }
    val satViewModel = remember { SatViewModel(database) }
    val dataViewModel = remember { DataViewModel(database) }

    // 1. Primero el controller
    val navController = rememberNavController()
    val backStackEntryState = navController.currentBackStackEntryAsState()
    val backStackEntry = backStackEntryState.value
    val currentRoute = backStackEntry?.destination?.route
    val pantallaActual = pantallas.values().find { it.name == currentRoute } ?: pantallas.inicio
    val canNavigateBack = navController.previousBackStackEntry != null

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(pantallaActual.titulo)) },
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
                startDestination = pantallas.inicio.name,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(route = pantallas.inicio.name) {
                    homeScreen(navController = navController, dataViewModel)
                }
                composable(route = pantallas.listaF.name) {
                    ListaFisicasScreen(navController = navController, dataViewModel, satViewModel)
                }
                composable(route = pantallas.listaM.name){
                    ListaMoralesScreen(navController = navController, dataViewModel, satViewModel)
                }
                composable(route = pantallas.addF.name) {
                    AddFisicaScreen(dataViewModel = dataViewModel, satViewModel = satViewModel, onNavigateBack = {
                        navController.popBackStack()
                    }
                    )
                }
                composable(route = pantallas.addM.name) {
                    AddMoralesScreen(dataViewModel = dataViewModel, satViewModel = satViewModel, onNavigateBack = {
                        navController.popBackStack()
                    }
                    )
                }
                composable(route = pantallas.detailsF.name) {
                    DetallesFisicaScreen(viewModel = satViewModel, dataViewModel = dataViewModel, navController = navController)
                }
                composable(route = pantallas.detailsM.name) {
                    DetallesMoralScreen(dataViewModel = dataViewModel, satViewModel = satViewModel, navController = navController)
                }

                composable(route = pantallas.editDataFisica.name) {
                    EditFisicaScreen(dataViewModel = dataViewModel, satViewModel = satViewModel, navController = navController)
                }
                composable(route = pantallas.editDataMoral.name) {
                    EditMoralesScreen(dataViewModel = dataViewModel, satViewModel = satViewModel, navController = navController)
                }

            } //FIN navHost


            }
    }
}