package grupo.aplicativo.data.repository

import grupo.aplicativo.data.local.dao.ProductoDao
import grupo.aplicativo.data.local.entity.Producto
import kotlinx.coroutines.flow.Flow

class ProductoRepository(private val productoDao: ProductoDao){
    val todosLosProductos: Flow <List<Producto>> = productoDao.obtenerTodos()
    val productosActivos: Flow <List<Producto>> = productoDao.obtenerActivos()
    val productosStockBajo: Flow <List<Producto>> = productoDao.obtenerStockBajo()
    val totalProductosActivos: Flow <Int> = productoDao.contarProductosActivos()

    suspend fun insertar (producto: Producto): Long{
        return productoDao.insertar(producto)
    }

    suspend fun actualizar(producto: Producto){
        productoDao.actualizar(producto)
    }

    suspend fun eliminar(producto: Producto){
        productoDao.eliminar(producto)
    }

    suspend fun obtenerPorId(id: Int): Producto?{
        return productoDao.obtenerPorId(id)
    }

    fun buscar(query: String): Flow<List<Producto>>{
        return productoDao.buscar(query)
    }

    suspend fun existeCodigo(codigo: String): Boolean{
        return productoDao.obtenerPorCodigo(codigo) != null
    }
}