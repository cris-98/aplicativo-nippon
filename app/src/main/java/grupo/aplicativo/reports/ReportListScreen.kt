package grupo.aplicativo.reports

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import grupo.aplicativo.ui.theme.AccentGreen
import grupo.aplicativo.ui.theme.CardBackground
import grupo.aplicativo.ui.theme.Green600
import grupo.aplicativo.ui.theme.MutedText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportListScreen(viewModel: ReportViewModel) {
    val movements by viewModel.movements.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    val showCsv = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadMovements()
    }

    SideEffect {
        Log.d("ReportListScreen", "Composición mostrada: ${'$'}{movements.size} movimientos")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Mis Reportes") },
                navigationIcon = {
                    IconButton(onClick = { /* si tienes navegación, regresar */ }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* acción: crear nuevo reporte o filtros */ },
                containerColor = Green600
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar")
            }
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Encabezado y acciones
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "REPORTES", style = MaterialTheme.typography.titleLarge)
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                    Button(onClick = { viewModel.loadMovements() }) {
                        Text("Refrescar")
                    }
                }

                // Indicador de carga y errores
                if (loading) {
                    CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                }

                if (error != null) {
                    Text(text = "Error: ${'$'}error", color = MaterialTheme.colorScheme.error)
                }

                // Lista de movimientos - tarjetas oscuras
                LazyColumn(modifier = Modifier.padding(top = 12.dp)) {
                    items(movements) { m ->
                        ReportRow(m) {
                            // para demo: nada
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
}

@Composable
fun ReportRow(m: Movement, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = m.productName, style = MaterialTheme.typography.titleMedium, color = AccentGreen)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                Text(text = if (m.type == "IN") "+" else "-" + " ${'$'}{m.type}")
            }
            Text(text = "Cantidad: ${'$'}{m.quantity} ${'$'}{m.unit}", color = MutedText)
            Text(text = "Fecha: ${'$'}{m.dateIso}", color = MutedText)
        }
    }
}
