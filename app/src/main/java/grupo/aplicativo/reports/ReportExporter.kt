package grupo.aplicativo.reports

import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.Locale

object ReportExporter {

    // Genera contenido CSV simple a partir de una lista de movimientos
    fun generateCsv(movements: List<Movement>): String {
        val sb = StringBuilder()
        // Encabezado
        sb.append("id,fecha,tipo,producto,cantidad,unidad,proveedor,usuario,referencia\n")
        val dateFormatIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val dateFormatOut = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        for (m in movements) {
            // intentar formatear fecha; si falla usar raw
            val fecha = try {
                val d = dateFormatIn.parse(m.dateIso)
                if (d != null) dateFormatOut.format(d) else m.dateIso
            } catch (_: Exception) {
                m.dateIso
            }
            val provider = m.provider ?: ""
            val user = m.user ?: ""
            val ref = m.reference ?: ""
            // escapar comillas dobles en el nombre del producto
            val productEsc = m.productName.replace("\"", "\\\"")
            sb.append("${m.id},\"$fecha\",${m.type},\"$productEsc\",${m.quantity},${m.unit},\"$provider\",\"$user\",\"$ref\"\n")
        }
        return sb.toString()
    }
}
