package pe.bibliotec.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuario")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @Column(name = "codigo", nullable = false, length = 15)
    private String codigo;

    @Column(name = "nombres", nullable = false, length = 60)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 60)
    private String apellidos;

    @Column(name = "correo", nullable = false, length = 100)
    private String correo;

    @Column(name = "clave_hash", nullable = false)
    private String claveHash;

    @Convert(converter = RolConverter.class)
    @Column(name = "rol", nullable = false)
    private Rol rol;

    @Convert(converter = EstadoUsuarioConverter.class)
    @Column(name = "estado", nullable = false)
    private EstadoUsuario estado = EstadoUsuario.ACTIVO;

    public Integer getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCorreo() { return correo; }
    public String getClaveHash() { return claveHash; }
    public Rol getRol() { return rol; }
    public EstadoUsuario getEstado() { return estado; }

    /** Rol en formato legible: Estudiante, Docente, Bibliotecario... */
    public String getRolEtiqueta() {
        String nombre = rol.name();
        return nombre.charAt(0) + nombre.substring(1).toLowerCase();
    }
    public void setEstado(EstadoUsuario estado) { this.estado = estado; }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    public boolean isActivo() {
        return estado == EstadoUsuario.ACTIVO;
    }
}
