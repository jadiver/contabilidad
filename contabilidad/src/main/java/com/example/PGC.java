package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PGC {
    private List<Cuenta> cuentas = new ArrayList<>();

    public void listar(String grupo) {
        String SQL = "SELECT codigo, nombre FROM public.pgc"; // Aunque podria ser SELECT * FROM pgc

        if (grupo != null && !grupo.trim().isEmpty()) {
            SQL += " WHERE codigo LIKE '" + grupo + "%'";
        }

        List<Map<String, Object>> resultados = new ArrayList<>();

        resultados = new BaseDeDatos("", "").consultar(SQL); // usa la conexión estática

        cuentas.clear();
        for (Map<String, Object> fila : resultados) {
            String codigo = (String) fila.get("codigo");
            String nombre = (String) fila.get("nombre");
            cuentas.add(new Cuenta(codigo, nombre));
        }

        // Imprimir las cuentas
        for (Cuenta c : cuentas) {
            System.out.println(c);
        }
    }
}
