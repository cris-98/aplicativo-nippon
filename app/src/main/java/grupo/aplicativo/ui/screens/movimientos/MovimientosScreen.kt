package grupo.aplicativo.ui.screens.movimientos

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import grupo.aplicativo.data.local.entity.TipoMovimiento
import grupo.aplicativo.viewmodel.MovimientosViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import grupo.aplicativo.data.local.entity.Movimiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(
    viewModel: MovimientosViewModel = viewModel(),
    onRegistrarEntrada: () -> Unit,  // Navegar a EntradasScreen
    onRegistrarSalida: () -> Unit,   // Navegar a SalidasScreen
    onMovimientoClick: (Int) -> Unit, // Ver detalle (opcional)
    onNavigateBack: () -> Unit
) {
    val movimientos by viewModel.movimientos.collectAsState()
    val entradas by viewModel.entradas.collectAsState()
    val salidas by viewModel.salidas.collectAsState()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()

    var busqueda by remember { mutableStateOf("") }
    var mostrarFiltros by remember { mutableStateOf(false) }
    var filtroSeleccionado by remember { mutableStateOf("Todos") }

    // Filtrar movimientos según el filtro seleccionado
    val movimientosFiltrados = remember(movimientos, filtroSeleccionado, busqueda) {
        var lista = when (filtroSeleccionado) {
            "Todos" -> movimientos
            "Entradas" -> movimientos.filter { it.tipo == TipoMovimiento.ENTRADA }
            "Salidas" -> movimientos.filter { it.tipo == TipoMovimiento.SALIDA }
            else -> movimientos
        }

        if (busqueda.isNotEmpty()) {
            lista = lista.filter {
                it.productoNombre.contains(busqueda, ignoreCase = true) ||
                        it.productoCodigo.contains(busqueda, ignoreCase = true) ||
                        it.motivo.contains(busqueda, ignoreCase = true)
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
                        "Historial de Movimientos",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            // Barra de búsqueda
            SearchBarMovimientos(
                busqueda = busqueda,
                onBusquedaChange = { busqueda = it }
            )

            // Chips de filtros
            if (mostrarFiltros) {
                FiltrosChipsMovimientos(
                    filtroSeleccionado = filtroSeleccionado,
                    onFiltroChange = { filtroSeleccionado = it }
                )
            }

            // Estadísticas CON NAVEGACIÓN (botones)
            EstadisticasCardMovimientos(
                totalEntradas = entradas.size,
                totalSalidas = salidas.size,
                onEntradasClick = onRegistrarEntrada,  // 👈 Navega a Entradas
                onSalidasClick = onRegistrarSalida     // 👈 Navega a Salidas
            )

            // Mensajes
            mensajeExito?.let {
                SnackbarMessageMovimientos(mensaje = it, esError = false)
            }
            mensajeError?.let {
                SnackbarMessageMovimientos(mensaje = it, esError = true)
            }

            // Lista de movimientos
            if (cargando) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (movimientosFiltrados.isEmpty()) {
                EmptyStateMovimientos()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(movimientosFiltrados) { movimiento ->
                        MovimientoCard(
                            movimiento = movimiento,
                            onClick = { onMovimientoClick(movimiento.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBarMovimientos(
    busqueda: String,
    onBusquedaChange: (String) -> Unit
) {
    OutlinedTextField(
        value = busqueda,
        onValueChange = onBusquedaChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Buscar movimiento...") },
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
fun FiltrosChipsMovimientos(
    filtroSeleccionado: String,
    onFiltroChange: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val filtros = listOf("Todos", "Entradas", "Salidas")
        filtros.forEach { filtro ->
            FilterChip(
                selected = filtroSeleccionado == filtro,
                onClick = { onFiltroChange(filtro) },
                label = { Text(filtro) }
            )
        }
    }
}

// 🎯 ESTADÍSTICAS CONVERTIDAS EN BOTONES NAVEGABLES
@Composable
fun EstadisticasCardMovimientos(
    totalEntradas: Int,
    totalSalidas: Int,
    onEntradasClick: () -> Unit,
    onSalidasClick: () -> Unit
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
            // 📥 BOTÓN ENTRADAS
            EstadisticaItemClickable(
                icono = Icons.Default.AddCircle,
                valor = totalEntradas.toString(),
                etiqueta = "Registrar Entrada",
                color = Color(0xFF4CAF50),
                onClick = onEntradasClick  // 👈 Navega a EntradasScreen
            )

            VerticalDivider(
                modifier = Modifier
                    .height(50.dp)
                    .width(1.dp)
            )

            // 📤 BOTÓN SALIDAS
            EstadisticaItemClickable(
                icono = Icons.Default.RemoveCircle,
                valor = totalSalidas.toString(),
                etiqueta = "Registrar Salida",
                color = Color(0xFFFF5722),
                onClick = onSalidasClick  // 👈 Navega a SalidasScreen
            )
        }
    }
}

// 🎯 COMPONENTE CLICKABLE PARA NAVEGACIÓN
@Composable
fun EstadisticaItemClickable(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    valor: String,
    etiqueta: String,
    color: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            icono,
            contentDescription = etiqueta,
            tint = color,
            modifier = Modifier.size(40.dp)
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
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun MovimientoCard(
    movimiento: Movimiento,
    onClick: () -> Unit
) {
    val esEntrada = movimiento.tipo == TipoMovimiento.ENTRADA
    val colorFondo = if (esEntrada) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
    val colorAccent = if (esEntrada) Color(0xFF4CAF50) else Color(0xFFFF5722)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tipo y Fecha
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        if (esEntrada) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = movimiento.tipo.name,
                        tint = colorAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (esEntrada) "ENTRADA" else "SALIDA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = colorAccent
                    )
                }

                Text(
                    text = movimiento.obtenerFechaFormateada(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Producto
            Text(
                text = movimiento.productoNombre,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF212121)
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Código
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.QrCode,
                    contentDescription = "Código",
                    modifier = Modifier.size(16.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Código: ${movimiento.productoCodigo}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(12.dp))

            // Información adicional
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Cantidad
                Column {
                    Text(
                        text = "Cantidad",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "${movimiento.cantidad}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = colorAccent
                    )
                }

                // Motivo (si existe)
                if (movimiento.motivo.isNotEmpty()) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Motivo",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = movimiento.motivo,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF212121)
                        )
                    }
                }
            }

            // Observaciones (si existen)
            if (movimiento.observaciones.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Obs: ${movimiento.observaciones}",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }
        }
    }
}

@Composable
fun EmptyStateMovimientos() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.SwapVert,
                contentDescription = "Sin movimientos",
                modifier = Modifier.size(80.dp),
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay movimientos registrados",
                fontSize = 18.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Los movimientos aparecerán aquí",
                fontSize = 14.sp,
                color = Color.LightGray
            )
        }
    }
}

@Composable
fun SnackbarMessageMovimientos(mensaje: String, esError: Boolean) {
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
