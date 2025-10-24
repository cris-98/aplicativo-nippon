package grupo.aplicativo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Producto para Room Database
 * Representa un producto en el almacén de NIPPONAUTO
 */
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val codigo: String,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val cantidad: Int,
    val cantidadMinima: Int,
    val precioUnitario: Double,
    val ubicacion: String,
    val proveedor: String,
    val fechaRegistro: Long = System.currentTimeMillis(),
    val estado: String = "ACTIVO" // ACTIVO, INACTIVO
) {
    /**
     * Verifica si el producto está por debajo del stock mínimo
     */
    fun esBajoStock(): Boolean = cantidad <= cantidadMinima

    /**
     * Calcula el valor total del inventario de este producto
     */
    fun valorInventario(): Double = cantidad * precioUnitario

    /**
     * Verifica si el producto está activo
     */
    fun estaActivo(): Boolean = estado == "ACTIVO"
}

/**
 * Categorías predefinidas para productos
 */
object CategoriasProducto {
    val categorias = listOf(
        "Repuestos",
        "Accesorios",
        "Herramientas",
        "Lubricantes",
        "Neumáticos",
        "Baterías",
        "Filtros",
        "Frenos",
        "Suspensión",
        "Eléctrico",
        "Motor",
        "Transmisión",
        "Otros"
    )
}