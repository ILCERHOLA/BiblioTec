package pe.bibliotec.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JpaUtil {

    private static final EntityManagerFactory EMF = crearFactory();

    private JpaUtil() { }

    private static EntityManagerFactory crearFactory() {
        Properties p = new Properties();
        try (InputStream in = JpaUtil.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                    "Falta config.properties (copia config.properties.example)");
            }
            p.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer config.properties", e);
        }
        return Persistence.createEntityManagerFactory("bibliotecPU", Map.of(
            "jakarta.persistence.jdbc.url", p.getProperty("db.url"),
            "jakarta.persistence.jdbc.user", p.getProperty("db.user"),
            "jakarta.persistence.jdbc.password", p.getProperty("db.password")));
    }

    public static EntityManager crearEntityManager() {
        return EMF.createEntityManager();
    }

    public static void cerrar() {
        EMF.close();
    }
}