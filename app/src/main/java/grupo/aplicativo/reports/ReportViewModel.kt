package grupo.aplicativo.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel(private val repository: ReportRepository = ReportRepository()) : ViewModel() {

    private val _movements = MutableStateFlow<List<Movement>>(emptyList())
    val movements: StateFlow<List<Movement>> = _movements

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadMovements(startIso: String? = null, endIso: String? = null) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val list = repository.getMovements(startIso, endIso)
                _movements.value = list
            } catch (e: Exception) {
                _error.value = e.message ?: "Error desconocido"
            } finally {
                _loading.value = false
            }
        }
    }

    suspend fun generateCsvContent(): String {
        // uso calificado para evitar un posible problema de resolución
        return grupo.aplicativo.reports.ReportExporter.generateCsv(_movements.value)
    }
}
