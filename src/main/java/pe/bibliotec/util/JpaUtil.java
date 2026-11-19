package pe.bibliotec.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Fabrica unica de EntityManager (patron Singleton).
 *
 * La conexion a la base de datos se toma, en este orden:
 *   1. Variables de entorno DB_URL, DB_USER y DB_PASSWORD (nube / Docker).
 *   2. Archivo config.properties del classpath (desarrollo local, no se sube a Git).
 */
public final class JpaUtil {

    private static final String UNIDAD = "bibliotecPU";
    private static EntityManagerFactory emf;

    private JpaUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (emf == null || !emf.isOpen()) {
            emf = Persistence.createEntityManagerFactory(UNIDAD, configuracion());
        }
        return emf;
    }

    public static EntityManager crearEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }

    public static synchronized void cerrar() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
        emf = null;
    }

    private static Map<String, Object> configuracion() {
        Properties archivo = new Properties();
        try (InputStream in = JpaUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in != null) {
                archivo.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer config.properties", e);
        }

        String url = valor("DB_URL", archivo.getProperty("db.url"));
        String usuario = valor("DB_USER", archivo.getProperty("db.user"));
        String clave = valor("DB_PASSWORD", archivo.getProperty("db.password"));

        if (url == null || usuario == null || clave == null) {
            throw new IllegalStateException(
                    "Falta configurar la base de datos: defina DB_URL, DB_USER y DB_PASSWORD "
                            + "o cree config.properties.");
        }

        Map<String, Object> propiedades = new HashMap<>();
        propiedades.put("jakarta.persistence.jdbc.url", url);
        propiedades.put("jakarta.persistence.jdbc.user", usuario);
        propiedades.put("jakarta.persistence.jdbc.password", clave);
        return propiedades;
    }

    private static String valor(String variableEntorno, String respaldo) {
        String v = System.getenv(variableEntorno);
        return (v != null && !v.isBlank()) ? v : respaldo;
    }
}
