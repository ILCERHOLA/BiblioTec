package pe.bibliotec.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EstadoReservaConverter
        implements AttributeConverter<EstadoReserva, String> {

    @Override
    public String convertToDatabaseColumn(EstadoReserva estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoReserva convertToEntityAttribute(String valor) {
        return valor == null ? null : EstadoReserva.desdeValor(valor);
    }
}
