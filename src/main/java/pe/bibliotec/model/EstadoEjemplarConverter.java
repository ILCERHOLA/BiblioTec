package pe.bibliotec.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EstadoEjemplarConverter
        implements AttributeConverter<EstadoEjemplar, String> {

    @Override
    public String convertToDatabaseColumn(EstadoEjemplar estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoEjemplar convertToEntityAttribute(String valor) {
        return valor == null ? null : EstadoEjemplar.desdeValor(valor);
    }
}