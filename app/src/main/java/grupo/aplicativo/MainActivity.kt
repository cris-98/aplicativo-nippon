package grupo.aplicativo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import grupo.aplicativo.navigation.AppNavigation
import grupo.aplicativo.reports.ReportListScreen
import grupo.aplicativo.ui.theme.AplicativonipponTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicativonipponTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Si quieres usar navegación principal:
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)

                    // Si quieres mostrar el listado de reportes directamente:
                    // val vm = remember { grupo.aplicativo.reports.ReportViewModel() }
                    // Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    //     Box(modifier = Modifier.padding(innerPadding)) {
                    //         ReportListScreen(viewModel = vm)
                    //     }
                    // }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AplicativonipponTheme {
        // No usar viewModel() en previews: crear instancia directa para UI de preview
        ReportListScreen(viewModel = grupo.aplicativo.reports.ReportViewModel())
    }
}
