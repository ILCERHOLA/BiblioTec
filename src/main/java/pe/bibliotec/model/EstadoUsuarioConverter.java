package pe.bibliotec.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EstadoUsuarioConverter implements AttributeConverter<EstadoUsuario, String> {

    @Override
    public String convertToDatabaseColumn(EstadoUsuario estado) {
        return estado == null ? null : estado.getValor();
    }

    @Override
    public EstadoUsuario convertToEntityAttribute(String valor) {
        return valor == null ? null : EstadoUsuario.desdeValor(valor);
    }
}