package pe.bibliotec.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EstadoPrestamoConverter
        implements AttributeConverter<EstadoPrestamo, String> {

    @Override
    public String convertToDatabaseColumn(EstadoPrestamo estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoPrestamo convertToEntityAttribute(String valor) {
        return valor == null ? null : EstadoPrestamo.desdeValor(valor);
    }
}
