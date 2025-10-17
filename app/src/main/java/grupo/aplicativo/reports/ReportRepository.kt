package grupo.aplicativo.reports

import kotlinx.coroutines.delay

// Repositorio mínimo que en este punto devuelve datos simulados.
class ReportRepository {

    // Simula una consulta de movimientos entre fechas
    suspend fun getMovements(startIso: String?, endIso: String?): List<Movement> {
        // Simular latencia
        delay(100)
        return listOf(
            Movement(1, "2025-10-01T09:00:00", "IN", "Producto A", 10, "pcs", "Proveedor X", "Usuario1", "REF-001"),
            Movement(2, "2025-10-02T11:30:00", "OUT", "Producto B", 3, "pcs", "Proveedor Y", "Usuario2", "REF-002"),
            Movement(3, "2025-10-03T14:15:00", "IN", "Producto A", 5, "pcs", "Proveedor X", "Usuario1", "REF-003")
        ).filter {
            // filtrado muy simple: si startIso/endIso son nulos devolvemos todo
            true
        }
    }
}

