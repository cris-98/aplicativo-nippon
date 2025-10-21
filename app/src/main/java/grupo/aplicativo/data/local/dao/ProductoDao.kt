package grupo.aplicativo.data.local.dao

import androidx.room.*
import grupo.aplicativo.data.local.entity.Producto
import kotlinx.coroutines.flow.Flow

/**
 * DAO para operaciones CRUD de Producto
 * Usa Flow para observar cambios en tiempo real
 */
@Dao
interface ProductoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto): Long

    @Update
    suspend fun actualizar(producto: Producto)

    @Delete
    suspend fun eliminar(producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): Producto?

    @Query("SELECT * FROM productos WHERE codigo = :codigo")
    suspend fun obtenerPorCodigo(codigo: String): Producto?

    @Query("SELECT * FROM productos WHERE nombre LIKE '%' || :query || '%' OR codigo LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun buscar(query: String): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE categoria = :categoria ORDER BY nombre ASC")
    fun obtenerPorCategoria(categoria: String): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE cantidad <= cantidadMinima AND estado = 'ACTIVO' ORDER BY cantidad ASC")
    fun obtenerStockBajo(): Flow<List<Producto>>

    @Query("SELECT * FROM productos WHERE estado = 'ACTIVO' ORDER BY nombre ASC")
    fun obtenerActivos(): Flow<List<Producto>>

    @Query("SELECT COUNT(*) FROM productos WHERE estado = 'ACTIVO'")
    fun contarProductosActivos(): Flow<Int>

    @Query("SELECT DISTINCT categoria FROM productos ORDER BY categoria ASC")
    fun obtenerCategorias(): Flow<List<String>>

    @Query("DELETE FROM productos")
    suspend fun eliminarTodos()
}