package grupo.aplicativo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import grupo.aplicativo.data.local.entity.Movimiento
import grupo.aplicativo.data.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.Flow

@Dao
interface MovimientoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(movimiento: Movimiento): Long

    @Update
    suspend fun actualizar(movimiento: Movimiento)

    @Delete
    suspend fun eliminar(movimiento: Movimiento)

    @Query("SELECT * FROM movimientos ORDER BY fechaRegistro DESC")
    fun obtenerTodos(): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Movimiento?

    @Query("SELECT * FROM movimientos WHERE tipo = :tipo ORDER BY fechaRegistro DESC")
    fun obtenerPorTipo(tipo: TipoMovimiento): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos WHERE productoId = :productoId ORDER BY fechaRegistro DESC")
    fun obtenerPorProducto(productoId: Int): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos WHERE productoNombre LIKE '%' || :query || '%' OR productoCodigo LIKE '%' || :query || '%' ORDER BY fechaRegistro DESC")
    fun buscar(query: String): Flow<List<Movimiento>>

    @Query("SELECT * FROM movimientos WHERE fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY fechaRegistro DESC")
    fun obtenerPorRangoFechas(fechaInicio: Long, fechaFin: Long): Flow<List<Movimiento>>

    @Query("SELECT COUNT(*) FROM movimientos WHERE tipo = :tipo")
    fun contarPorTipo(tipo: TipoMovimiento): Flow<Int>

    @Query("SELECT SUM(cantidad) FROM movimientos WHERE productoId = :productoId AND tipo = :tipo")
    suspend fun sumarCantidadPorProducto(productoId: Int, tipo: TipoMovimiento): Int?

    @Query("DELETE FROM movimientos")
    suspend fun eliminarTodos()

    // Obtener últimos movimientos
    @Query("SELECT * FROM movimientos ORDER BY fechaRegistro DESC LIMIT :limite")
    fun obtenerUltimos(limite: Int = 10): Flow<List<Movimiento>>
}