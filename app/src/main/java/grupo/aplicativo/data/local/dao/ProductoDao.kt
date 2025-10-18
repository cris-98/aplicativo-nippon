package grupo.aplicativo.data.local.dao

import androidx.room.*
import grupo.aplicativo.data.local.entity.Producto
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {
    @Insert(onConflict = onConflictStrategy.REPLACE)
    suspend fun insertar(producto: Producto): Long

    @Update
    suspend fun  actualizar(producto: Producto)

    @Delete
    suspend fun eleminar (producto: Producto)

    @Query("SELECT * FROM productos ORDER BY nombre ASC")
    fun obtenerTodos()Flow<List<Producto>>

    @
}