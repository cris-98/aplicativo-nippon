package grupo.aplicativo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import grupo.aplicativo.data.local.database.AppDatabase
import grupo.aplicativo.data.local.entity.Producto
import grupo.aplicativo.data.repository.ProductoRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProductoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProductoRepository

    // Estados de la UI usando StateFlow
    val productos: StateFlow<List<Producto>>
    val productosActivos: StateFlow<List<Producto>>
    val productosStockBajo: StateFlow<List<Producto>>
    val totalProductos: StateFlow<Int>

    private val _mensajeError = MutableStateFlow<String?>(null)
    val mensajeError: StateFlow<String?> = _mensajeError.asStateFlow()

    private val _mensajeExito = MutableStateFlow<String?>(null)
    val mensajeExito: StateFlow<String?> = _mensajeExito.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    init {
        val productoDao = AppDatabase.getDatabase(application).productoDao()
        repository = ProductoRepository(productoDao)

        // Convertir Flow a StateFlow para Compose
        productos = repository.todosLosProductos
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        productosActivos = repository.productosActivos
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        productosStockBajo = repository.productosStockBajo
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )

        totalProductos = repository.totalProductosActivos
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = 0
            )
    }

    fun buscarProductos(query: String): StateFlow<List<Producto>> {
        _busqueda.value = query
        return repository.buscar(query)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun insertarProducto(producto: Producto) = viewModelScope.launch {
        _cargando.value = true
        try {
            // Validar datos
            val validacion = validarProducto(producto)
            if (!validacion.first) {
                _mensajeError.value = validacion.second
                _cargando.value = false
                return@launch
            }

            // Verificar que el código no exista
            if (repository.existeCodigo(producto.codigo)) {
                _mensajeError.value = "El código ${producto.codigo} ya existe"
                _cargando.value = false
                return@launch
            }

            repository.insertar(producto)
            _mensajeExito.value = "Producto registrado exitosamente"
        } catch (e: Exception) {
            _mensajeError.value = "Error: ${e.message}"
        } finally {
            _cargando.value = false
        }
    }

    fun actualizarProducto(producto: Producto) = viewModelScope.launch {
        _cargando.value = true
        try {
            val validacion = validarProducto(producto)
            if (!validacion.first) {
                _mensajeError.value = validacion.second
                _cargando.value = false
                return@launch
            }

            repository.actualizar(producto)
            _mensajeExito.value = "Producto actualizado exitosamente"
        } catch (e: Exception) {
            _mensajeError.value = "Error: ${e.message}"
        } finally {
            _cargando.value = false
        }
    }
    fun eliminarProducto(producto: Producto) = viewModelScope.launch{
        _cargando.value = true
        try {
            repository.eliminar(producto)
            _mensajeExito.value = "Producto eliminado exitosamente"
        } catch (e: Exception){
            _mensajeError.value= "Error: ${e.message}"
        }finally{
            _cargando.value = false
        }
    }
    suspend fun obtenerProductoPorId(id: Int): Producto?{
        return repository.obtenerPorId(id)
    }

    private fun validarProducto(producto: Producto): Pair <Boolean, String>{
        return when{
            producto.codigo.isBlank() -> Pair(false, "El código es obligatorio")
            producto.nombre.isBlank() -> Pair (false, "El nombre es obligatorio")
            producto.categoria.isBlank() -> Pair (false, "La categoría es obligatoria")
            producto.cantidad < 0 -> Pair(false, "La cantidad no puede ser negativa")
            producto.cantidadMinima < 0 -> Pair(false, "La cantidad mínima no puede ser negativa")
            producto.precioUnitario < 0 -> Pair(false,"El precio no puede ser negativo")
            producto.ubicacion.isBlank() ->Pair(false,"La ubicación es obligatoria")
            producto.proveedor.isBlank() ->Pair(false,"El proveedor es obligatorio")
            else -> Pair(true, "Válido")
        }
    }
    fun limpiarMensajes(){
        _mensajeError.value = null
        _mensajeExito.value = null
    }

    fun generarProductosEjemplo() = viewModelScope.launch{
        val productosEjemplo = listOf(
            Producto(
                codigo = "REP001",
                nombre ="Filtro de aceite Toyota",
                descripcion ="Filtro de aceite original para Toyota Corolla",
                categoria ="Filtros",
                cantidad =  25,
                cantidadMinima = 10,
                precioUnitario = 45.00,
                ubicacion = "A1-01",
                proveedor ="Toyota Peru",
                estado = "ACTIVO"

            ),
            Producto(
                codigo = "REP002",
                nombre ="Pastilla de Freno Nissan",
                descripcion = "Juego de pastillas delanteras Nissan Sentra",
                categoria = "Frenos",
                cantidad = 8,
                cantidadMinima = 15,
                precioUnitario = 180.00,
                ubicacion = "B2-05",
                proveedor ="Nissan Parts",
                estado = "ACTIVO"

            ),
            Producto(
                codigo =" ACC001",
                nombre ="Tapiz de Auto Universal",
                descripcion ="Tapiz universal de goma para proteccion",
                categoria = "Accesorios",
                cantidad = 50,
                cantidadMinima = 20,
                precioUnitario = 65.00,
                ubicacion = "C3-10",
                proveedor ="Auto Accesorios SAC",
                estado = "ACTIVO"

            ),
            Producto(
                codigo ="LUB001",
                nombre ="Aceite Motor 5W-30",
                descripcion ="Aceite sintético premium 5w-30 -4 litros",
                categoria ="Lubricantes",
                cantidad = 40,
                cantidadMinima = 25,
                precioUnitario = 120.00,
                ubicacion = "D1-03",
                proveedor ="Castrol Perú",
                estado ="ACTIVO"

            ),
            Producto(
                codigo ="NEU001",
                nombre ="Neumatico Bridgestone 185/65R15",
                descripcion = "Neumatico radial para auto",
                categoria = "Neumaticos",
                cantidad = 12,
                cantidadMinima = 8,
                precioUnitario = 280.00,
                ubicacion = "E1-01",
                proveedor ="Bridgestone Perú",
                estado = "ACTIVO"

            )
        )

        productosEjemplo.forEach{ producto ->
            if(!repository.existeCodigo(producto.codigo)){
                repository.insertar(producto)
            }

        }
        _mensajeExito.value = "Productos de ejemplo generados exitosamente"

    }
}