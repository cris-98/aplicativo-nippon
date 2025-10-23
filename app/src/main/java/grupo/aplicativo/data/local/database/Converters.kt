package grupo.aplicativo.data.local.database

import androidx.room.TypeConverter
import grupo.aplicativo.data.local.entity.TipoMovimiento

class Converters {
    @TypeConverter
    fun fromTipoMovimiento(tipo: TipoMovimiento): String {
        return tipo.name
    }

    /**
     * Convierte String a TipoMovimiento al leer de BD
     */
    @TypeConverter
    fun toTipoMovimiento(tipo: String): TipoMovimiento {
        return TipoMovimiento.valueOf(tipo)
    }
}