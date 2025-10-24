package grupo.aplicativo.reports

import kotlinx.coroutines.flow.MutableSharedFlow

// Evento simple para notificar cambios en movimientos (entradas/salidas)
object ReportEvents {
    // No replay; los listeners activos recibirán notificaciones en tiempo real
    val movementUpdates = MutableSharedFlow<Unit>(replay = 0)

    // Emitir de forma no suspendible (tryEmit) desde ViewModels
    fun notifyMovementChanged() {
        movementUpdates.tryEmit(Unit)
    }
}

