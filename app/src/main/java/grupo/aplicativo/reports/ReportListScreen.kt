package grupo.aplicativo.reports

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import grupo.aplicativo.data.local.entity.Movimiento
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(viewModel: ReportViewModel, onNavigateBack: () -> Unit) {
    val movements by viewModel.movements.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val message by viewModel.message.collectAsState()
    val coroutine = rememberCoroutineScope()

    var search by remember { mutableStateOf("") }
    var showCsv by remember { mutableStateOf<String?>(null) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadMovements() }

    val filteredMovements = movements.filter {
        it.productoNombre.contains(search, ignoreCase = true) ||
                it.motivo.contains(search, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primaryContainer
                                )
                            )
                        )
                        .padding(vertical = 16.dp, horizontal = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            "Historial de Reportes",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(onClick = { showConfirmDelete = true }) {
                            Icon(
                                Icons.Filled.DeleteSweep,
                                contentDescription = "Eliminar todo",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }

                // Campo de búsqueda
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    label = { Text("Buscar producto o motivo...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    singleLine = true
                )
            }
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = { viewModel.loadMovements() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refrescar")
                }
                Spacer(modifier = Modifier.height(12.dp))
                FloatingActionButton(
                    onClick = {
                        coroutine.launch {
                            val csv = viewModel.generateCsvContent()
                            showCsv = csv
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Icon(Icons.Filled.Download, contentDescription = "Exportar CSV")
                }
            }
        }
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                if (loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                error?.let {
                    Text(
                        text = "⚠️ Error: $it",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(8.dp)
                    )
                }

                AnimatedVisibility(visible = filteredMovements.isEmpty() && !loading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay movimientos registrados.", color = MaterialTheme.colorScheme.outline)
                    }
                }

                LazyColumn {
                    items(filteredMovements) { m ->
                        AnimatedVisibility(visible = true, enter = fadeIn(), exit = fadeOut()) {
                            ReportRow(m)
                        }
                    }
                }

                message?.let { msg ->
                    AlertDialog(
                        onDismissRequest = { viewModel.consumeMessage() },
                        confirmButton = {
                            Button(onClick = { viewModel.consumeMessage() }) {
                                Text("Ok")
                            }
                        },
                        text = { Text(msg) }
                    )
                }

                if (showCsv != null) {
                    AlertDialog(
                        onDismissRequest = { showCsv = null },
                        confirmButton = { Button(onClick = { showCsv = null }) { Text("Cerrar") } },
                        text = {
                            // Mostrar CSV en lista para evitar dependencias de scroll que no estén disponibles
                            Box(modifier = Modifier.height(300.dp)) {
                                val lines = showCsv!!.lines()
                                LazyColumn(modifier = Modifier.fillMaxSize()) {
                                    items(lines) { line ->
                                        Text(line, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(4.dp))
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Eliminar todos los reportes") },
            text = { Text("¿Seguro que deseas eliminar todo el historial de movimientos?") },
            confirmButton = {
                Button(onClick = {
                    showConfirmDelete = false
                    viewModel.clearAllReports()
                }) { Text("Sí, eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDelete = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
fun ReportRow(m: Movimiento) {
    val colorChip = if (m.esEntrada()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
    val chipText = if (m.esEntrada()) "ENTRADA" else "SALIDA"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = m.productoNombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.weight(1f))
                AssistChip(
                    onClick = {},
                    label = { Text(chipText) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = colorChip.copy(alpha = 0.15f),
                        labelColor = colorChip
                    )
                )
            }
            Spacer(Modifier.height(4.dp))
            Text("Cantidad: ${m.cantidad}", style = MaterialTheme.typography.bodyMedium)
            Text("Fecha: ${m.obtenerFechaFormateada()}", style = MaterialTheme.typography.bodySmall)
            if (m.motivo.isNotBlank()) Text("Motivo: ${m.motivo}", style = MaterialTheme.typography.bodySmall)
            if (m.observaciones.isNotBlank()) Text("Obs.: ${m.observaciones}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
