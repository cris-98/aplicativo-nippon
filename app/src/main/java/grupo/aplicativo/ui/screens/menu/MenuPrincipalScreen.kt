package grupo.aplicativo.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.grupo.aplicativo.R
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.lazy.grid.items
import android.app.Activity
import android.content.Intent
import androidx.compose.ui.platform.LocalContext


@Composable
fun MenuPrincipalScreen(onNavigate: (String) -> Unit) {
    val user = FirebaseAuth.getInstance().currentUser
    val nombreUsuario = user?.email ?: "Usuario"

    val opciones = listOf(
        MenuOpcion("Productos", Icons.Default.ShoppingCart, "productos"),
        MenuOpcion("Entradas y Salidas", Icons.Default.Inventory, "movimientos"),
        MenuOpcion("Reporte", Icons.Default.Assessment, "reportes"),
        MenuOpcion("Cerrar Sesión", Icons.Default.ExitToApp, "logout")
    )

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Text(
                text = "Menu Principal",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(R.drawable.logo_app),
                contentDescription = "Logo App",
                modifier = Modifier
                    .fillMaxWidth()          // que ocupe todo el ancho
                    .height(120.dp)          // banner horizontal
                    .padding(top = 24.dp),
                contentScale = ContentScale.Crop // recorta proporcionalmente
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bienvenido, $nombreUsuario",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(opciones) { opcion ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .clickable {
                                if (opcion.ruta == "logout") {
                                    FirebaseAuth.getInstance().signOut()
                                    val intent = Intent(context, _root_ide_package_.com.grupo.aplicativo.LoginActivity::class.java)
                                    context.startActivity(intent)
                                    if (context is Activity) {
                                        context.finish() // cerrar MainActivity
                                    }
                                    //onNavigate("login")
                                } else {
                                    onNavigate(opcion.ruta)
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = opcion.icono,
                                contentDescription = opcion.nombre,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = opcion.nombre,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

data class MenuOpcion(val nombre: String, val icono: androidx.compose.ui.graphics.vector.ImageVector, val ruta: String)
