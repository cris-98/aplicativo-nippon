package grupo.aplicativo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import grupo.aplicativo.reports.ReportListScreen
import grupo.aplicativo.ui.theme.AplicativonipponTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AplicativonipponTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Crear ViewModel manual para demo con remember (evita dependencia a lifecycle-viewmodel-compose)
                    val vm = remember { grupo.aplicativo.reports.ReportViewModel() }
                    Box(modifier = Modifier.padding(innerPadding)) {
                        ReportListScreen(viewModel = vm)
                    }
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