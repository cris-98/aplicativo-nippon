package grupo.aplicativo.ui.screens.productos

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import grupo.aplicativo.data.local.entity.Producto
import grupo.aplicativo.viewmodel.ProductoViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductosScreen(
    viewModel: ProductoViewModel = viewModel(),
    onAgregarProducto: () -> Unit,
    onProductoClick: (Int) -> Unit
) {
    val productos by viewModel.productos.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var mostrarFiltros by remember { mutableStateOf(false) }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    // Filtrar productos según el filtro seleccionado
    val productosFiltrados = remember(productos, filtroSeleccionado, busqueda) {
        //Quiero modifiar el filtro para que muestre:
        var lista = when (filtroSeleccionado) {
            //Todos los movimientos de entrada y salida
            "Todos" -> productos
            //Entrada de productos
            "Stock Bajo" -> productos.filter { it.esBajoStock() }
            //Salida de productos
            "Activos" -> productos.filter { it.estaActivo() }
            else -> productos
        }

        if (busqueda.isNotEmpty()) {
            lista = lista.filter {
                it.nombre.contains(busqueda, ignoreCase = true) ||
                        it.codigo.contains(busqueda, ignoreCase = true) ||
                        it.categoria.contains(busqueda, ignoreCase = true)
            }
        }
        lista
    }

    // Mostrar mensajes
    LaunchedEffect(mensajeExito, mensajeError) {
        if (mensajeExito != null || mensajeError != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.limpiarMensajes()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Gestión de Productos",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { mostrarFiltros = !mostrarFiltros }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filtros",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        // Eliminar ese boton para que crees otro que te especificare mas adelante
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAgregarProducto,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Agregar Producto",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Barra de búsqueda
            SearchBar(
                busqueda = busqueda,
                onBusquedaChange = { busqueda = it }
            )

            // Chips de filtros
            if (mostrarFiltros) {
                FiltrosChips(
                    filtroSeleccionado = filtroSeleccionado,
                    onFiltroChange = { filtroSeleccionado = it }
                )
            }

            // Estadísticas rápidas
            EstadisticasCard(
                totalProductos = productos.size,
                stockBajo = productos.count { it.esBajoStock() }
            )

            // Mensajes
            mensajeExito?.let {
                SnackbarMessage(mensaje = it, esError = false)
            }
            mensajeError?.let {
                SnackbarMessage(mensaje = it, esError = true)
            }

            // Lista de productos
            if (cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (productosFiltrados.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(productosFiltrados) { producto ->
                        ProductoCard(
                            producto = producto,
                            onClick = { onProductoClick(producto.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    busqueda: String,
    onBusquedaChange: (String) -> Unit
) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = onBusquedaChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Buscar producto...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Buscar")
        },
        trailingIcon = {
            if (busqueda.isNotEmpty()) {
                IconButton(onClick = { onBusquedaChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        )
    )
}

@Composable
fun FiltrosChips(
    filtroSeleccionado: String,
    onFiltroChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val filtros = listOf("Todos", "Stock Bajo", "Activos")
        filtros.forEach { filtro ->
            FilterChip(
                selected = filtroSeleccionado == filtro,
                onClick = { onFiltroChange(filtro) },
                label = { Text(filtro) }
            )
        }
    }
}

@Composable
fun EstadisticasCard(
    totalProductos: Int,
    stockBajo: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            //Convertir Boton de navegacion que lleve a ..
            EstadisticaItem(
                icono = Icons.Default.Inventory,
                valor = totalProductos.toString(),
                //LLeve a EntradasActivity
                etiqueta = "Total Productos",
                color = Color(0xFF1976D2)
            )

            VerticalDivider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp)
            )
            //Convertir Boton de navegacion que lleve a ..
            EstadisticaItem(
                icono = Icons.Default.Warning,
                valor = stockBajo.toString(),
                //LLeve a SalidasActivity
                etiqueta = "Stock Bajo",
                color = Color(0xFFFF5722)
            )
        }
    }
}

@Composable
fun EstadisticaItem(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    valor: String,
    etiqueta: String,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icono,
            contentDescription = etiqueta,
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = valor,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = etiqueta,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun ProductoCard(
    producto: Producto,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Código y Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = producto.codigo,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF1976D2)
                )

                Surface(
                    color = if (producto.estaActivo()) Color(0xFF4CAF50) else Color(0xFFF44336),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = producto.estado,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Nombre del producto
            Text(
                text = producto.nombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Categoría
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Category,
                    contentDescription = "Categoría",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = producto.categoria,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Información en grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cantidad
                Column {
                    Text(
                        text = "Stock: ${producto.cantidad}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (producto.esBajoStock()) Color(0xFFFF5722) else Color(0xFF4CAF50)
                    )
                    if (producto.esBajoStock()) {
                        Text(
                            text = "⚠️ Stock Bajo",
                            fontSize = 11.sp,
                            color = Color(0xFFFF5722)
                        )
                    }
                }

                // Ubicación
                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            modifier = Modifier.size(16.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = producto.ubicacion,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "S/ ${String.format(Locale.getDefault(), "%.2f", producto.precioUnitario)}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1976D2)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.Inventory,
                contentDescription = "Sin productos",
                modifier = Modifier.size(80.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay productos registrados",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Presiona el botón + para agregar",
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun SnackbarMessage(mensaje: String, esError: Boolean) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = if (esError) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                if (esError) Icons.Default.Error else Icons.Default.CheckCircle,
                contentDescription = null,
                tint = if (esError) Color(0xFFC62828) else Color(0xFF2E7D32)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = mensaje,
                color = if (esError) Color(0xFFC62828) else Color(0xFF2E7D32)
            )
        }
    }
}