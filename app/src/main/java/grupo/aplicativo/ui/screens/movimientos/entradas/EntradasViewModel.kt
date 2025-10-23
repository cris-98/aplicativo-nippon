package grupo.aplicativo.ui.screens.movimientos.entradas

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import grupo.aplicativo.data.local.database.AppDatabase
import grupo.aplicativo.data.local.entity.Movimiento
import grupo.aplicativo.data.local.entity.Producto
import grupo.aplicativo.data.local.entity.TipoMovimiento
import grupo.aplicativo.data.repository.MovimientoRepository
import grupo.aplicativo.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EntradasViewModel(application: Application) : AndroidViewModel(application) {
    private val movimientoRepository: MovimientoRepository
    private val productoRepository: ProductoRepository

    // Estados de UI
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _productoSeleccionado = MutableStateFlow<Producto?>(null)
    val productoSeleccionado: StateFlow<Producto?> = _productoSeleccionado.asStateFlow()

    // Campos del formulario
    private val _cantidad = MutableStateFlow("")
    val cantidad: StateFlow<String> = _cantidad.asStateFlow()

    private val _fecha = MutableStateFlow("")
    val fecha: StateFlow<String> = _fecha.asStateFlow()

    private val _observaciones = MutableStateFlow("")
    val observaciones: StateFlow<String> = _observaciones.asStateFlow()

    // Estados de mensajes
    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _mensajeExito = MutableStateFlow<String?>(null)
    val mensajeExito: StateFlow<String?> = _mensajeExito.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Búsqueda de productos
    private val _busquedaProducto = MutableStateFlow("")
    val busquedaProducto: StateFlow<String> = _busquedaProducto.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val productoDao = database.productoDao()
        val movimientoDao = database.movimientoDao()

        productoRepository = ProductoRepository(productoDao)
        movimientoRepository = MovimientoRepository(movimientoDao, productoDao)

        // Cargar productos activos
        viewModelScope.launch {
            productoRepository.productosActivos.collect { lista ->
                _productos.value = lista
            }
        }

        // Establecer fecha actual por defecto
        _fecha.value = obtenerFechaActual()
    }

    // Funciones para actualizar campos
    fun actualizarCantidad(valor: String) {
        _cantidad.value = valor
    }

    fun actualizarFecha(valor: String) {
        _fecha.value = valor
    }

    fun actualizarObservaciones(valor: String) {
        _observaciones.value = valor
    }

    fun actualizarBusquedaProducto(valor: String) {
        _busquedaProducto.value = valor
    }

    fun seleccionarProducto(producto: Producto) {
        _productoSeleccionado.value = producto
    }

    /**
     * Registra una nueva entrada de producto
     */
    fun registrarEntrada() = viewModelScope.launch {
        _cargando.value = true
        _mensajeError.value = null

        try {
            // Validaciones
            val producto = _productoSeleccionado.value
            if (producto == null) {
                _mensajeError.value = "Por favor, selecciona un producto"
                _cargando.value = false
                return@launch
            }

            val cantidadInt = _cantidad.value.toIntOrNull()
            if (cantidadInt == null || cantidadInt <= 0) {
                _mensajeError.value = "La cantidad debe ser un número mayor a 0"
                _cargando.value = false
                return@launch
            }

            if (_fecha.value.isBlank()) {
                _mensajeError.value = "Por favor, ingresa una fecha válida"
                _cargando.value = false
                return@launch
            }

            // Validar formato de fecha
            val fechaTimestamp = parsearFecha(_fecha.value)
            if (fechaTimestamp == null) {
                _mensajeError.value = "Formato de fecha inválido. Usa dd/MM/yyyy"
                _cargando.value = false
                return@launch
            }

            // Crear el movimiento
            val movimiento = Movimiento(
                productoId = producto.id,
                productoNombre = producto.nombre,
                productoCodigo = producto.codigo,
                tipo = TipoMovimiento.ENTRADA,
                cantidad = cantidadInt,
                fechaRegistro = fechaTimestamp,
                observaciones = _observaciones.value,
                motivo = "Entrada de inventario"
            )

            // Registrar en el repositorio (esto actualiza el stock automáticamente)
            val resultado = movimientoRepository.registrarEntrada(movimiento)

            resultado.fold(
                onSuccess = {
                    _mensajeExito.value = "✅ Entrada registrada correctamente. Stock actualizado: ${producto.cantidad + cantidadInt}"
                    limpiarCampos()
                },
                onFailure = { error ->
                    _mensajeError.value = "Error: ${error.message}"
                }
            )

        } catch (e: Exception) {
            _mensajeError.value = "Error inesperado: ${e.message}"
        } finally {
            _cargando.value = false
        }
    }

    /**
     * Limpia los campos del formulario
     */
    fun limpiarCampos() {
        _productoSeleccionado.value = null
        _cantidad.value = ""
        _fecha.value = obtenerFechaActual()
        _observaciones.value = ""
    }

    /**
     * Limpia los mensajes
     */
    fun limpiarMensajes() {
        _mensajeError.value = null
        _mensajeExito.value = null
    }

    /**
     * Parsea una fecha en formato dd/MM/yyyy a timestamp
     */
    private fun parsearFecha(fechaTexto: String): Long? {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            formato.parse(fechaTexto)?.time
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Obtiene la fecha actual en formato dd/MM/yyyy
     */
    private fun obtenerFechaActual(): String {
        val formato = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return formato.format(Date())
    }

    /**
     * Busca productos por nombre o código
     */
    fun buscarProductos(): StateFlow<List<Producto>> {
        return if (_busquedaProducto.value.isEmpty()) {
            productos
        } else {
            productoRepository.buscar(_busquedaProducto.value)
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5000),
                    initialValue = emptyList()
                )
        }
    }
}