package grupo.aplicativo.reports

import java.io.Serializable

// Modelo mínimo para un movimiento en inventario
data class Movement(
    val id: Long,
    val dateIso: String, // ISO date string
    val type: String, // "IN" or "OUT"
    val productName: String,
    val quantity: Int,
    val unit: String,
    val provider: String?,
    val user: String?,
    val reference: String?
) : Serializable

