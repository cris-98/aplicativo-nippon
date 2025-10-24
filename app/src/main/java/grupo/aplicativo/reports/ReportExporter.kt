package grupo.aplicativo.reports

import grupo.aplicativo.data.local.entity.Movimiento
import java.lang.StringBuilder

object ReportExporter {

    // Genera contenido CSV a partir de la entidad real Movimiento
    fun generateCsv(movements: List<Movimiento>): String {
        val sb = StringBuilder()
        // Encabezado
        sb.append("id,fecha,tipo,producto,cantidad,motivo,observaciones\n")

        fun escape(field: String?): String {
            val f = field ?: ""
            val esc = f.replace("\"", "\"\"")
            return "\"$esc\""
        }

        for (m in movements) {
            val fecha = m.obtenerFechaFormateada()
            val tipo = if (m.esEntrada()) "IN" else "OUT"
            sb.append(
                "${m.id}," +
                        "${escape(fecha)}," +
                        "${escape(tipo)}," +
                        "${escape(m.productoNombre)}," +
                        "${m.cantidad}," +
                        "${escape(m.motivo)}," +
                        "${escape(m.observaciones)}\n"
            )
        }
        return sb.toString()
    }
}
