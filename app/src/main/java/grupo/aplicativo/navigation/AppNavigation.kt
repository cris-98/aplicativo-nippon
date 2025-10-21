package grupo.aplicativo.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import grupo.aplicativo.ui.screens.productos.AgregarProductoScreen
import grupo.aplicativo.ui.screens.productos.ProductosScreen


sealed class Screen(val route: String) {
    object Productos : Screen("productos")
    object AgregarProducto : Screen("agregar_producto")
    object DetalleProducto : Screen("detalle_producto/{productoId}") {
        fun createRoute(productoId: Int) = "detalle_producto/$productoId"
    }
}


@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Productos.route
    ) {
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
    }
}