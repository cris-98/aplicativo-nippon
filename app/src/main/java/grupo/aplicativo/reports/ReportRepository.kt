package grupo.aplicativo.reports

import grupo.aplicativo.data.local.dao.MovimientoDao
import grupo.aplicativo.data.local.entity.Movimiento
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

class ReportRepository {
    companion object {
        @Volatile private var movimientoDaoRef: MovimientoDao? = null
        fun setDao(dao: MovimientoDao) { movimientoDaoRef = dao }
    }

    // Ahora devolvemos directamente la entidad Movimiento (datos reales)
    suspend fun getMovements(startIso: String?, endIso: String?): List<Movimiento> {
        val daoLocal = movimientoDaoRef
        if (daoLocal == null) {
            // Sin DAO: devolvemos lista vacía (sin datos simulados para evitar incompatibilidades)
            delay(50)
            return emptyList()
        }
        return try {
            daoLocal.obtenerTodos().first()
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun clearAll(): Result<Unit> {
        val daoLocal = movimientoDaoRef ?: return Result.failure(IllegalStateException("DAO no inicializado"))
        return try { daoLocal.eliminarTodos(); Result.success(Unit) } catch (e: Exception) { Result.failure(e) }
    }
}
