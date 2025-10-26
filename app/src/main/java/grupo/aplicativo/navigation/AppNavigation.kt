package grupo.aplicativo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import grupo.aplicativo.reports.ReportListScreen
import grupo.aplicativo.reports.ReportViewModel
import grupo.aplicativo.reports.ReportRepository
import grupo.aplicativo.data.local.database.AppDatabase
import grupo.aplicativo.ui.screens.MenuPrincipalScreen
import grupo.aplicativo.ui.screens.movimientos.MovimientosScreen
import grupo.aplicativo.ui.screens.movimientos.entradas.EntradasScreen
import grupo.aplicativo.ui.screens.movimientos.salidas.SalidasScreen
import grupo.aplicativo.ui.screens.productos.AgregarProductoScreen
import grupo.aplicativo.ui.screens.productos.ProductosScreen


sealed class Screen(val route: String) {
    object Menu : Screen("menu")
    object Productos : Screen("productos")
    object AgregarProducto : Screen("agregar_producto")
    object DetalleProducto : Screen("detalle_producto/{productoId}") {
        fun createRoute(productoId: Int) = "detalle_producto/$productoId"
    }
    object Reportes : Screen("reportes")

    // RUTAS PARA MOVIMIENTOS
    object Movimientos : Screen("movimientos")
    object RegistrarEntrada : Screen("registrar_entrada")
    object RegistrarSalida : Screen("registrar_salida")
    object DetalleMovimiento : Screen("detalle_movimiento/{movimientoId}") {
        fun createRoute(movimientoId: Int) = "detalle_movimiento/$movimientoId"
    }
}


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Menu.route
    ) {
        // Menu principal
        composable(Screen.Menu.route) {
            MenuPrincipalScreen(onNavigate = { ruta ->
                navController.navigate(ruta) {
                    popUpTo(Screen.Menu.route) { inclusive = false }
                }
            })
        }

        // Pantalla principal de productos
        composable(Screen.Productos.route) {
            ProductosScreen(
                onAgregarProducto = {
                    navController.navigate(Screen.AgregarProducto.route)
                },
                onProductoClick = { productoId ->
                    navController.navigate(Screen.DetalleProducto.createRoute(productoId))
                }
            )
        }

        // Pantalla de agregar producto
        composable(Screen.AgregarProducto.route) {
            AgregarProductoScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de detalle de producto
        composable(
            route = Screen.DetalleProducto.route,
            arguments = listOf(
                navArgument("productoId") { type = NavType.IntType }
            )
        ) { _ ->
            // TODO: Implementar DetalleProductoScreen
            navController.popBackStack()
        }


        // ==================== PANTALLAS DE MOVIMIENTOS ====================

        // Pantalla principal de movimientos (historial)
        composable(Screen.Movimientos.route) {
            MovimientosScreen(
                onRegistrarEntrada = {
                    navController.navigate(Screen.RegistrarEntrada.route)
                },
                onRegistrarSalida = {
                    navController.navigate(Screen.RegistrarSalida.route)
                },
                onMovimientoClick = { movimientoId ->
                    navController.navigate(Screen.DetalleMovimiento.createRoute(movimientoId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de registrar entrada
        composable(Screen.RegistrarEntrada.route) {
            EntradasScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de registrar salida
        composable(Screen.RegistrarSalida.route) {
            SalidasScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de detalle de movimiento
        composable(
            route = Screen.DetalleMovimiento.route,
            arguments = listOf(
                navArgument("movimientoId") { type = NavType.IntType }
            )
        ) { _ ->
            // TODO: Implementar DetalleMovimientoScreen
            navController.popBackStack()
        }

        // Reporte
        composable(Screen.Reportes.route) {
            val context = LocalContext.current
            val movimientoDao = remember { AppDatabase.getDatabase(context).movimientoDao() }
            // Inyectamos el DAO globalmente al repositorio de reportes
            ReportRepository.setDao(movimientoDao)
            val reportRepository = remember { ReportRepository() }
            val viewModel = remember { ReportViewModel(reportRepository) }
            ReportListScreen(viewModel, onNavigateBack = { navController.popBackStack() })
        }
    }
}