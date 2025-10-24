package grupo.aplicativo.ui.screens.movimientos.salidas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import grupo.aplicativo.data.local.entity.MotivosSalida
import grupo.aplicativo.data.local.entity.Producto
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalidasScreen(
    viewModel: SalidasViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val productoSeleccionado by viewModel.productoSeleccionado.collectAsState()
    val cantidad by viewModel.cantidad.collectAsState()
    val motivo by viewModel.motivo.collectAsState()
    val fecha by viewModel.fecha.collectAsState()
    val observaciones by viewModel.observaciones.collectAsState()

    val mensajeError by viewModel.mensajeError.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()
    val mensajeAdvertencia by viewModel.mensajeAdvertencia.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var mostrarDialogoProductos by remember { mutableStateOf(false) }
    var mostrarDialogoMotivo by remember { mutableStateOf(false) }

    // Auto-cerrar después de éxito
    LaunchedEffect(mensajeExito) {
        if (mensajeExito != null) {
            delay(2000)
            viewModel.limpiarMensajes()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Salida") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF5722),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Filled.ExitToApp,
                    contentDescription = "Salida",
                    tint = Color(0xFFFF5722),
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nueva Salida del Almacén",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color(0xFFFF5722)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Selector de Producto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDialogoProductos = true },
                colors = CardDefaults.cardColors(
                    containerColor = if (productoSeleccionado != null)
                        Color(0xFFFFEBEE)
                    else
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Filled.Inventory2,
                        contentDescription = "Producto",
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFFFF5722)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (productoSeleccionado != null)
                                productoSeleccionado!!.nombre
                            else
                                "Seleccionar Producto",
                            fontWeight = FontWeight.Bold
                        )
                        if (productoSeleccionado != null) {
                            Text(
                                text = "Stock disponible: ${productoSeleccionado!!.cantidad}",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (productoSeleccionado!!.esBajoStock())
                                    Color(0xFFFF5722)
                                else
                                    Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Icon(Filled.ArrowDropDown, contentDescription = "Expandir")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Cantidad
            OutlinedTextField(
                value = cantidad,
                onValueChange = { viewModel.actualizarCantidad(it) },
                label = { Text("Cantidad retirada") },
                leadingIcon = {
                    Icon(Filled.Remove, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = productoSeleccionado != null,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF5722),
                    focusedLabelColor = Color(0xFFFF5722)
                )
            )

            // Advertencia de stock
            mensajeAdvertencia?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFFFF3E0)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Filled.Warning,
                            contentDescription = "Advertencia",
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE65100)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Selector de Motivo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { mostrarDialogoMotivo = true },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Filled.Edit,
                        contentDescription = "Motivo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Motivo de salida",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                        Text(
                            text = motivo,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(Filled.ArrowDropDown, contentDescription = "Expandir")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Fecha
            OutlinedTextField(
                value = fecha,
                onValueChange = { viewModel.actualizarFecha(it) },
                label = { Text("Fecha (dd/MM/yyyy)") },
                leadingIcon = {
                    Icon(Filled.CalendarToday, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Observaciones
            OutlinedTextField(
                value = observaciones,
                onValueChange = { viewModel.actualizarObservaciones(it) },
                label = { Text("Observaciones (opcional)") },
                leadingIcon = {
                    Icon(Filled.Comment, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrar
            Button(
                onClick = { viewModel.registrarSalida() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !cargando && productoSeleccionado != null,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF5722)
                )
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Filled.Save,
                        contentDescription = "Registrar",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Salida", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mensajes de error
            mensajeError?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Filled.Error,
                            contentDescription = "Error",
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            // Mensajes de éxito
            mensajeExito?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE8F5E9)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Filled.CheckCircle,
                            contentDescription = "Éxito",
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = it,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }
            }
        }

        // Diálogo de selección de productos
        if (mostrarDialogoProductos) {
            DialogoSeleccionProductoSalida(
                viewModel = viewModel,
                onDismiss = { mostrarDialogoProductos = false },
                onProductoSeleccionado = { producto ->
                    viewModel.seleccionarProducto(producto)
                    mostrarDialogoProductos = false
                }
            )
        }

        // Diálogo de selección de motivo
        if (mostrarDialogoMotivo) {
            AlertDialog(
                onDismissRequest = { mostrarDialogoMotivo = false },
                title = { Text("Seleccionar Motivo") },
                text = {
                    LazyColumn {
                        items(MotivosSalida.motivos) { motivoItem ->
                            TextButton(
                                onClick = {
                                    viewModel.actualizarMotivo(motivoItem)
                                    mostrarDialogoMotivo = false
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = motivoItem,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { mostrarDialogoMotivo = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoSeleccionProductoSalida(
    viewModel: SalidasViewModel,
    onDismiss: () -> Unit,
    onProductoSeleccionado: (Producto) -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val busqueda by viewModel.busquedaProducto.collectAsState()

    val productosFiltrados = remember(productos, busqueda) {
        if (busqueda.isEmpty()) {
            productos
        } else {
            productos.filter {
                it.nombre.contains(busqueda, ignoreCase = true) ||
                        it.codigo.contains(busqueda, ignoreCase = true)
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Seleccionar Producto")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Barra de búsqueda
                OutlinedTextField(
                    value = busqueda,
                    onValueChange = { viewModel.actualizarBusquedaProducto(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar producto...") },
                    leadingIcon = {
                        Icon(Filled.Search, contentDescription = "Buscar")
                    },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { viewModel.actualizarBusquedaProducto("") }) {
                                Icon(Filled.Clear, contentDescription = "Limpiar")
                            }
                        }
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Lista de productos
                if (productosFiltrados.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay productos con stock disponible",
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    ) {
                        items(productosFiltrados) { producto ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onProductoSeleccionado(producto) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (producto.esBajoStock())
                                        Color(0xFFFFEBEE)
                                    else
                                        MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = producto.nombre,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Código: ${producto.codigo}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Stock: ${producto.cantidad}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = if (producto.esBajoStock())
                                                Color(0xFFFF5722)
                                            else
                                                Color(0xFF4CAF50),
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = producto.categoria,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}