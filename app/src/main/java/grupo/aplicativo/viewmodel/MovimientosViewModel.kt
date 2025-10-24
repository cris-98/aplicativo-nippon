package grupo.aplicativo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import grupo.aplicativo.data.local.database.AppDatabase
import grupo.aplicativo.data.local.entity.Movimiento
import grupo.aplicativo.data.repository.MovimientoRepository
import grupo.aplicativo.data.repository.ProductoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovimientosViewModel(application: Application): AndroidViewModel(application) {
    private val movimientoRepository: MovimientoRepository
    private val productoRepository: ProductoRepository

    // Estados de UI
    val movimientos: StateFlow<List<Movimiento>>
    val entradas: StateFlow<List<Movimiento>>
    val salidas: StateFlow<List<Movimiento>>

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _mensajeExito = MutableStateFlow<String?>(null)
    val mensajeExito: StateFlow<String?> = _mensajeExito.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    // Búsqueda
    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        val productoDao = database.productoDao()
        val movimientoDao = database.movimientoDao()

        productoRepository = ProductoRepository(productoDao)
        movimientoRepository = MovimientoRepository(movimientoDao, productoDao)

        // Convertir Flow a StateFlow
        movimientos = movimientoRepository.todosLosMovimientos
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        entradas = movimientoRepository.entradas
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        salidas = movimientoRepository.salidas
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    /**
     * Busca movimientos por nombre o código de producto
     */
    fun buscarMovimientos(query: String): StateFlow<List<Movimiento>> {
        _busqueda.value = query
        return movimientoRepository.buscar(query)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    /**
     * Elimina un movimiento
     */
    fun eliminarMovimiento(movimiento: Movimiento) = viewModelScope.launch {
        _cargando.value = true
        try {
            movimientoRepository.eliminar(movimiento)
            _mensajeExito.value = "Movimiento eliminado correctamente"
        } catch (e: Exception) {
            _mensajeError.value = "Error: ${e.message}"
        } finally {
            _cargando.value = false
        }
    }

    /**
     * Obtiene últimos movimientos para dashboard
     */
    fun obtenerUltimosMovimientos(limite: Int = 10): StateFlow<List<Movimiento>> {
        return movimientoRepository.obtenerUltimosMovimientos(limite)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun limpiarMensajes() {
        _mensajeError.value = null
        _mensajeExito.value = null
    }
}