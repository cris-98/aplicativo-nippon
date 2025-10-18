package grupo.aplicativo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.times

@Entity(tableName ="productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val codigo: String,
    val nombre: String,
    val description: String,
    val categoria: String,
    val cantidad: Int,
    val cantidadMinima: Int,
    val precioUnitario: Double,
    val ubicacion: String,
    val proveedor: String,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val estado: String = "ACTIVO"



){
    fun esBajoStock(): Boolean = cantidad <= cantidadMinima
    fun valorInventario(): Double = cantidad * precioUnitario
    fun estaActivo(): Boolean = estado == "ACTIVO"
}
object CategoriasProducto{
    val categorias = listOf(
        "Respuestos",
        "Accesorios",
        "Herramienta",
        "Lubricantes",
        "Neumáticos",
        "Baterias",
        "Filtros",
        "Frenos",
        "Suspensión",
        "Eléctrico",
        "Motor",
        "Transmisión",
        "Otros"
    )
}