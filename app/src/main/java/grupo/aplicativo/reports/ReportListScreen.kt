package grupo.aplicativo.reports

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(viewModel: ReportViewModel) {
    val movements by viewModel.movements.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val coroutine = rememberCoroutineScope()

    val showCsv = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMovements()
    }

    SideEffect {
        Log.d("ReportListScreen", "Composición mostrada: ${movements.size} movimientos")
    }

    Surface(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Encabezado visible para identificar la pantalla en el dispositivo
            Text(text = "REPORTES", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { viewModel.loadMovements() }) {
                    Text("Refrescar")
                }
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {
                    coroutine.launch {
                        val csv = viewModel.generateCsvContent()
                        showCsv.value = csv
                    }
                }) {
                    Text("Exportar CSV")
                }
            }

            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
            }

            if (error != null) {
                Text(text = "Error: $error", color = MaterialTheme.colorScheme.error)
            }

            LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                items(movements) { m ->
                    ReportRow(m) {
                        // Para el ejemplo no navegamos; se podría abrir detalle
                    }
                }
            }

            if (showCsv.value != null) {
                AlertDialog(
                    onDismissRequest = { showCsv.value = null },
                    confirmButton = {
                        Button(onClick = { showCsv.value = null }) { Text("Cerrar") }
                    },
                    text = { Text(showCsv.value ?: "") },
                )
            }
        }
    }
}

@Composable
fun ReportRow(m: Movement, onClick: () -> Unit) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = m.productName, style = MaterialTheme.typography.titleMedium)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                Text(text = m.type)
            }
            Text(text = "Cantidad: ${m.quantity} ${m.unit}")
            Text(text = "Fecha: ${m.dateIso}")
        }
    }
}
