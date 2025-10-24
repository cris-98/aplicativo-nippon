package grupo.aplicativo.reports

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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

    var showCsv by remember { mutableStateOf<String?>(null) }
    var showConfirmDelete by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.loadMovements() }

    SideEffect { Log.d("ReportListScreen", "Composición mostrada: ${movements.size} movimientos") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reportes") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showConfirmDelete = true }, enabled = !loading) {
                        Icon(Icons.Filled.DeleteSweep, contentDescription = "Eliminar todo")
                    }
                }
            )
        }
    ) { padding ->
        Surface(modifier = Modifier.fillMaxWidth().padding(padding)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = { viewModel.loadMovements() }) { Text("Refrescar") }
                    Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = {
                        coroutine.launch {
                            val csv = viewModel.generateCsvContent()
                            showCsv = csv
                        }
                    }) { Text("Exportar CSV") }
                }

                if (loading) { CircularProgressIndicator(modifier = Modifier.padding(8.dp)) }

                error?.let { Text(text = "Error: $it", color = MaterialTheme.colorScheme.error) }
                message?.let { msg ->
                    AlertDialog(
                        onDismissRequest = { viewModel.consumeMessage() },
                        confirmButton = { Button(onClick = { viewModel.consumeMessage() }) { Text("Ok") } },
                        text = { Text(msg) }
                    )
                }

                LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                    items(movements) { m ->
                        ReportRow(m) { /* abrir detalle si se requiere */ }
                    }
                }

                if (showCsv != null) {
                    AlertDialog(
                        onDismissRequest = { showCsv = null },
                        confirmButton = { Button(onClick = { showCsv = null }) { Text("Cerrar") } },
                        text = { Text(showCsv ?: "") },
                    )
                }
            }
        }
    }

    if (showConfirmDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmDelete = false },
            title = { Text("Eliminar reportes") },
            text = { Text("Esto eliminará todos los movimientos del historial. ¿Deseas continuar?") },
            confirmButton = {
                Button(onClick = {
                    showConfirmDelete = false
                    viewModel.clearAllReports()
                }) { Text("Eliminar") }
            },
            dismissButton = { TextButton(onClick = { showConfirmDelete = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun ReportRow(m: Movimiento, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = m.productoNombre, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.weight(1f))
                Text(text = if (m.esEntrada()) "ENTRADA" else "SALIDA")
            }
            Text(text = "Cantidad: ${m.cantidad}")
            Text(text = "Fecha: ${m.obtenerFechaFormateada()}")
            if (m.motivo.isNotBlank()) Text(text = "Motivo: ${m.motivo}")
            if (m.observaciones.isNotBlank()) Text(text = "Obs.: ${m.observaciones}")
        }
    }
}
