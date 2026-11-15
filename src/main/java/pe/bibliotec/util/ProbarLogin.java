package pe.bibliotec.util;

import pe.bibliotec.service.AuthService;

public class ProbarLogin {
    public static void main(String[] args) {
        AuthService auth = new AuthService();
        try {
            probar(auth, "2024001234", "Clave2026");
            probar(auth, "2024001234", "claveMala");
            probar(auth, "D-0001", "Clave2026");
            probar(auth, "NOEXISTE", "Clave2026");
        } finally {
            JpaUtil.cerrar();
        }
    }

    private static void probar(AuthService auth, String codigo, String clave) {
        System.out.println(">>> " + codigo + " -> " + auth.autenticar(codigo, clave)
                .map(u -> "OK " + u.getNombreCompleto() + " (" + u.getRol() + ", " + u.getEstado() + ")")
                .orElse("RECHAZADO"));
    }
}