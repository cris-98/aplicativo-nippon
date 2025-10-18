package grupo.aplicativo.ui.screens.productos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import grupo.aplicativo.data.local.entity.CategoriasProducto
import grupo.aplicativo.data.local.entity.Producto
import grupo.aplicativo.viewmodel.ProductoViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgregarProductoScreen(
    viewModel: ProductoViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val cargando by viewModel.cargando.collectAsState()
    val mensajeError by viewModel.mensajeError.collectAsState()
    val mensajeExito by viewModel.mensajeExito.collectAsState()

    var codigo by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(CategoriasProducto.categorias.first()) }
    var cantidad by remember { mutableStateOf("") }
    var cantidadMinima by remember { mutableStateOf("") }
    var precioUnitario by remember { mutableStateOf("") }
    var ubicacion by remember { mutableStateOf("") }
    var proveedor by remember { mutableStateOf("") }
    var estado by remember { mutableStateOf("ACTIVO") }
    
    var categoriaExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(mensajeExito) {
        if (mensajeExito != null) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agregar Producto") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            mensajeError?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            OutlinedTextField(
                value = codigo,
                onValueChange = { codigo = it },
                label = { Text("Código") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )
            
            ExposedDropdownMenuBox(
                expanded = categoriaExpanded,
                onExpandedChange = { categoriaExpanded = !categoriaExpanded }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    label = { Text("Categoría") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = categoriaExpanded,
                    onDismissRequest = { categoriaExpanded = false }
                ) {
                    CategoriasProducto.categorias.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                categoria = selectionOption
                                categoriaExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = cantidad,
                onValueChange = { cantidad = it },
                label = { Text("Cantidad") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = cantidadMinima,
                onValueChange = { cantidadMinima = it },
                label = { Text("Cantidad Mínima") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = precioUnitario,
                onValueChange = { precioUnitario = it },
                label = { Text("Precio Unitario") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = ubicacion,
                onValueChange = { ubicacion = it },
                label = { Text("Ubicación") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = proveedor,
                onValueChange = { proveedor = it },
                label = { Text("Proveedor") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val producto = Producto(
                        codigo = codigo,
                        nombre = nombre,
                        descripcion = descripcion,
                        categoria = categoria,
                        cantidad = cantidad.toIntOrNull() ?: 0,
                        cantidadMinima = cantidadMinima.toIntOrNull() ?: 0,
                        precioUnitario = precioUnitario.toDoubleOrNull() ?: 0.0,
                        ubicacion = ubicacion,
                        proveedor = proveedor,
                        estado = estado
                    )
                    coroutineScope.launch {
                        viewModel.insertarProducto(producto)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !cargando
            ) {
                if (cargando) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Guardar Producto")
                }
            }
        }
    }
}