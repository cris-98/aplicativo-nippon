package grupo.aplicativo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import grupo.aplicativo.ui.screens.menu.MenuPrincipalScreen
import grupo.aplicativo.ui.screens.movimientos.MovimientosScreen
import grupo.aplicativo.ui.screens.movimientos.entradas.EntradasScreen
import grupo.aplicativo.ui.screens.movimientos.salidas.SalidasScreen
import grupo.aplicativo.ui.screens.productos.AgregarProductoScreen
import grupo.aplicativo.ui.screens.productos.ProductosScreen


sealed class Screen(val route: String) {
    object Menu : Screen("menu")  //  RUTA MenuPrincipal
    object Productos : Screen("productos")
    object AgregarProducto : Screen("agregar_producto")
    object DetalleProducto : Screen("detalle_producto/{productoId}") {
        fun createRoute(productoId: Int) = "detalle_producto/$productoId"
    }
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
        // ==================== MENÚ PRINCIPAL ====================
        composable(Screen.Menu.route) {
            MenuPrincipalScreen(
                onProductosClick = {
                    navController.navigate(Screen.Productos.route)
                },
                onMovimientosClick = {
                    navController.navigate(Screen.Movimientos.route)
                }
            )
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
        ) { backStackEntry ->
            val productoId = backStackEntry.arguments?.getInt("productoId") ?: 0
            // TODO: Implementar DetalleProductoScreen
            // Por ahora, volvemos atrás
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
        ) { backStackEntry ->
            val movimientoId = backStackEntry.arguments?.getInt("movimientoId") ?: 0
            // TODO: Implementar DetalleMovimientoScreen
            // Por ahora, volvemos atrás
            navController.popBackStack()
        }
    }
}