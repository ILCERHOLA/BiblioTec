package pe.bibliotec.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EstadoMultaConverter
        implements AttributeConverter<EstadoMulta, String> {

    @Override
    public String convertToDatabaseColumn(EstadoMulta estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoMulta convertToEntityAttribute(String valor) {
        return valor == null ? null : EstadoMulta.desdeValor(valor);
    }
}
