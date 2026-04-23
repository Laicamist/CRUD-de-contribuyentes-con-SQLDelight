package com.laicamist.crudsqldelight.Ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallesMoralScreen(
    satViewModel: SatViewModel,
    dataViewModel: DataViewModel,
    navController: NavController
) {
    val contribuyente by satViewModel.contribuyente.collectAsState()
    var dialogoBorrado by remember { mutableStateOf(false) }

    Scaffold() {
        padding ->
        contribuyente?.let { empresa ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
            ) {

                item { SectionHeader("Identificación de la Persona Moral") }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow("Denominación o Razón Social", empresa.razon_social ?:"")
                            InfoRow("RFC", empresa.rfc ?:"")
                            InfoRow("Régimen Capital", empresa.regimen_capital ?: "N/A")
                            InfoRow("Régimen Fiscal", empresa.regimen_fiscal ?: "N/A")
                        }
                    }
                }

                item { SectionHeader("Datos Constitutivos") }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            InfoRow("Fecha de Constitución", empresa.fecha_constitucion ?: "N/A")
                            InfoRow("Número de Póliza / Escritura", empresa.poliza ?: "N/A")
                            InfoRow("Actividad Económica", empresa.actividad_economica ?: "N/A")
                            InfoRow("RFC Socios / Accionistas", empresa.rfc_socios ?: "N/A")
                        }
                    }
                }

                item { SectionHeader("Domicilio Fiscal") }
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {

                            val nombreEstado = satViewModel.getEstadoById(empresa.estado_id).nombre
                            val nombreMunicipio = satViewModel.getMunicipioById(empresa.municipio_id).nombre

                            InfoRow("Estado", nombreEstado)
                            InfoRow("Municipio", nombreMunicipio)
                            InfoRow("C.P.", empresa.cp)
                            InfoRow("Dirección", "${empresa.tipo_vialidad ?: ""} ${empresa.calle} #${empresa.numero_exterior}")

                            if (!empresa.numero_interior.isNullOrBlank()) {
                                InfoRow("Num. Interno", empresa.numero_interior)
                            }

                            InfoRow("Colonia", empresa.colonia ?: "N/A")

                            if (!empresa.entre_calle1.isNullOrBlank() || !empresa.entre_calle2.isNullOrBlank()) {
                                InfoRow("Entre calles", "${empresa.entre_calle1 ?: ""} y ${empresa.entre_calle2 ?: ""}")
                            }

                            if (!empresa.referencia_adicional.isNullOrBlank()) {
                                InfoRow("Referencias", empresa.referencia_adicional!!)
                            }

                            if (!empresa.caracteristicas_domicilio.isNullOrBlank()) {
                                InfoRow("Características fachada", empresa.caracteristicas_domicilio!!)
                            }

                            if (!empresa.localidad.isNullOrBlank()) {
                                InfoRow("Localidad", empresa.localidad!!)
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
                                dataViewModel.prepararEdicionMoral(empresa)
                                navController.navigate(pantallas.editDataMoral.name)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Editar")
                        }
                        Button(
                            onClick = { dialogoBorrado = true },
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
        } ?: Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
    }

    if (dialogoBorrado) {
        AlertDialog(
            onDismissRequest = { dialogoBorrado = false },
            confirmButton = {
                TextButton(onClick = {
                    contribuyente?.let { satViewModel.delete(it) }
                    dialogoBorrado = false
                    navController.popBackStack()
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = { TextButton(onClick = { dialogoBorrado = false }) { Text("Cancelar") } },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Deseas eliminar permanentemente a ${contribuyente?.nombre}?") }
        )
    }
}