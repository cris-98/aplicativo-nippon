package grupo.aplicativo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movimientos")
data class Movimiento(

    @PrimaryKey(autoGenerate = true)
        val id: Int = 0,
    // Relación con el producto
    val productoId: Int,  // ID del producto del inventario
    val productoNombre: String,  // Nombre para referencia rápida
    val productoCodigo: String,  // Código para referencia

    // Datos del movimiento
    val tipo: TipoMovimiento,  // ENTRADA o SALIDA
    val cantidad: Int,
    val fechaRegistro: Long = System.currentTimeMillis(),

    // Información adicional
    val motivo: String = "",  // Para salidas: venta, préstamo, baja, etc.
    val observaciones: String = "",
){
        /**
         * Retorna la fecha en formato legible
         */
        fun obtenerFechaFormateada(): String {
            val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault())
            return sdf.format(java.util.Date(fechaRegistro))
        }

        /**
         * Indica si es una entrada
         */
        fun esEntrada(): Boolean = tipo == TipoMovimiento.ENTRADA

        /**
         * Indica si es una salida
         */
        fun esSalida(): Boolean = tipo == TipoMovimiento.SALIDA
    }


    //Tipos de movimiento de inventario

    enum class TipoMovimiento {
        ENTRADA,  // Ingreso de productos
        SALIDA    // Egreso de productos
    }

    /**
     * Motivos predefinidos para salidas
     */
    object MotivosSalida {
        val motivos = listOf(
            "Venta",
            "Préstamo",
            "Mantenimiento",
            "Baja por daño",
            "Transferencia",
            "Devolución",
            "Garantía",
            "Otros"
        )
    }

