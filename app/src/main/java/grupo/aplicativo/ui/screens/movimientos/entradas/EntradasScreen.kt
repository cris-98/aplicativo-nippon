package grupo.aplicativo.ui.screens.movimientos.entradas

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import grupo.aplicativo.data.local.entity.Producto
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntradasScreen(
    viewModel: EntradasViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val productoSeleccionado by viewModel.productoSeleccionado.collectAsState()
    val cantidad by viewModel.cantidad.collectAsState()
    val fecha by viewModel.fecha.collectAsState()
    val observaciones by viewModel.observaciones.collectAsState()

    val mensajeError by viewModel.mensajeError.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()
    val cargando by viewModel.cargando.collectAsState()

    var mostrarDialogoProductos by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    // Auto-cerrar después de éxito
    LaunchedEffect(mensajeExito) {
        if (mensajeExito != null) {
            delay(2000)
            viewModel.limpiarMensajes()
            // Opcional: onNavigateBack() si quieres cerrar la pantalla
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Entrada") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
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
            // Encabezado con ícono
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Inventory2,
                    contentDescription = "Inventario",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Nueva Entrada al Almacén",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary
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
                        MaterialTheme.colorScheme.primaryContainer
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
                        Icons.Filled.ShoppingBag,
                        contentDescription = "Producto",
                        modifier = Modifier.size(24.dp)
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
                                text = "Código: ${productoSeleccionado!!.codigo} | Stock actual: ${productoSeleccionado!!.cantidad}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = "Expandir")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campo: Cantidad
            OutlinedTextField(
                value = cantidad,
                onValueChange = { viewModel.actualizarCantidad(it) },
                label = { Text("Cantidad ingresada") },
                leadingIcon = {
                    Icon(Icons.Filled.Add, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = productoSeleccionado != null
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Campo: Fecha
            OutlinedTextField(
                value = fecha,
                onValueChange = { viewModel.actualizarFecha(it) },
                label = { Text("Fecha (dd/MM/yyyy)") },
                leadingIcon = {
                    Icon(Icons.Filled.CalendarToday, contentDescription = null)
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
                    Icon(Icons.Filled.Comment, contentDescription = null)
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón Registrar
            Button(
                onClick = { viewModel.registrarEntrada() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !cargando && productoSeleccionado != null
            ) {
                if (cargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = "Registrar",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Entrada", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
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
                            Icons.Filled.Error,
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
                            Icons.Filled.CheckCircle,
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
            DialogoSeleccionProducto(
                viewModel = viewModel,
                onDismiss = { mostrarDialogoProductos = false },
                onProductoSeleccionado = { producto ->
                    viewModel.seleccionarProducto(producto)
                    mostrarDialogoProductos = false
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogoSeleccionProducto(
    viewModel: EntradasViewModel,
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
                        Icon(Icons.Filled.Search, contentDescription = "Buscar")
                    },
                    trailingIcon = {
                        if (busqueda.isNotEmpty()) {
                            IconButton(onClick = { viewModel.actualizarBusquedaProducto("") }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Limpiar")
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
                            text = "No se encontraron productos",
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
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                                    Text(
                                        text = "Stock actual: ${producto.cantidad} | Categoría: ${producto.categoria}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.Gray
                                    )
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