package grupo.aplicativo.data.repository

import grupo.aplicativo.data.local.dao.MovimientoDao
import grupo.aplicativo.data.local.dao.ProductoDao
import grupo.aplicativo.data.local.entity.Movimiento
import grupo.aplicativo.data.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.Flow

class MovimientoRepository (
    private val movimientoDao: MovimientoDao,
    private val productoDao: ProductoDao
){
    val todosLosMovimientos: Flow<List<Movimiento>> = movimientoDao.obtenerTodos()
    val entradas: Flow<List<Movimiento>> = movimientoDao.obtenerPorTipo(TipoMovimiento.ENTRADA)
    val salidas: Flow<List<Movimiento>> = movimientoDao.obtenerPorTipo(TipoMovimiento.SALIDA)

    /**
     * Registra una ENTRADA de producto y actualiza el stock automáticamente
     */
    suspend fun registrarEntrada(movimiento: Movimiento): Result<Long> {
        return try {
            // Validar que sea una entrada
            if (movimiento.tipo != TipoMovimiento.ENTRADA) {
                return Result.failure(Exception("El movimiento debe ser de tipo ENTRADA"))
            }

            // Obtener el producto
            val producto = productoDao.obtenerPorId(movimiento.productoId)
                ?: return Result.failure(Exception("Producto no encontrado"))

            // Registrar el movimiento
            val movimientoId = movimientoDao.insertar(movimiento)

            // Actualizar el stock del producto (AUMENTAR)
            val productoActualizado = producto.copy(
                cantidad = producto.cantidad + movimiento.cantidad
            )
            productoDao.actualizar(productoActualizado)

            Result.success(movimientoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Registra una SALIDA de producto y actualiza el stock automáticamente
     * Valida que haya stock suficiente antes de procesar
     */
    suspend fun registrarSalida(movimiento: Movimiento): Result<Long> {
        return try {
            // Validar que sea una salida
            if (movimiento.tipo != TipoMovimiento.SALIDA) {
                return Result.failure(Exception("El movimiento debe ser de tipo SALIDA"))
            }

            // Obtener el producto
            val producto = productoDao.obtenerPorId(movimiento.productoId)
                ?: return Result.failure(Exception("Producto no encontrado"))

            // VALIDACIÓN CRÍTICA: Verificar stock suficiente
            if (producto.cantidad < movimiento.cantidad) {
                return Result.failure(
                    Exception("Stock insuficiente. Disponible: ${producto.cantidad}, Solicitado: ${movimiento.cantidad}")
                )
            }

            // Registrar el movimiento
            val movimientoId = movimientoDao.insertar(movimiento)

            // Actualizar el stock del producto (DISMINUIR)
            val productoActualizado = producto.copy(
                cantidad = producto.cantidad - movimiento.cantidad
            )
            productoDao.actualizar(productoActualizado)

            Result.success(movimientoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtiene el historial de movimientos de un producto específico
     */
    fun obtenerHistorialProducto(productoId: Int): Flow<List<Movimiento>> {
        return movimientoDao.obtenerPorProducto(productoId)
    }

    /**
     * Busca movimientos por nombre o código de producto
     */
    fun buscar(query: String): Flow<List<Movimiento>> {
        return movimientoDao.buscar(query)
    }

    /**
     * Obtiene movimientos en un rango de fechas
     */
    fun obtenerPorRangoFechas(fechaInicio: Long, fechaFin: Long): Flow<List<Movimiento>> {
        return movimientoDao.obtenerPorRangoFechas(fechaInicio, fechaFin)
    }

    /**
     * Elimina un movimiento (requiere actualizar el stock manualmente)
     */
    suspend fun eliminar(movimiento: Movimiento) {
        movimientoDao.eliminar(movimiento)
        // NOTA: Deberías revertir el cambio de stock aquí si es necesario
    }

    /**
     * Obtiene últimos movimientos para dashboard
     */
    fun obtenerUltimosMovimientos(limite: Int = 10): Flow<List<Movimiento>> {
        return movimientoDao.obtenerUltimos(limite)
    }
}