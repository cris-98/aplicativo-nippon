package grupo.aplicativo.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import grupo.aplicativo.data.local.entity.Movimiento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(private val repository: ReportRepository = ReportRepository()) : ViewModel() {

    private val _movements = MutableStateFlow<List<Movimiento>>(emptyList())
    val movements: StateFlow<List<Movimiento>> = _movements

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    // Inicializar: escuchar eventos globales de actualización de movimientos
    init {
        viewModelScope.launch {
            ReportEvents.movementUpdates.collect {
                loadMovements()
            }
        }
    }

    fun loadMovements(startIso: String? = null, endIso: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val listLocal = repository.getMovements(startIso, endIso)
                _movements.value = listLocal
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearAllReports() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val res = repository.clearAll()
                res.fold(
                    onSuccess = {
                        _message.value = "Reportes eliminados"
                        // Notificar y recargar
                        ReportEvents.notifyMovementChanged()
                    },
                    onFailure = { err ->
                        _error.value = err.message ?: "No se pudo eliminar"
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _loading.value = false
            }
        }
    }

    fun consumeMessage() { _message.value = null }

    suspend fun generateCsvContent(): String {
        return ReportExporter.generateCsv(_movements.value)
    }
}
