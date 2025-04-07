package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PGC {
    private List<Cuenta> cuentas = new ArrayList<>();
    private BaseDeDatos bd;

    public PGC(BaseDeDatos bd) {
        this.bd = bd;
    }

    public void listar(String grupo) {
        String SQL = "SELECT codigo, nombre FROM public.pgc";

        if (grupo != null && !grupo.trim().isEmpty()) {
            SQL += " WHERE codigo LIKE '" + grupo + "%'";
        }

        List<Map<String, Object>> resultados = bd.consultar(SQL); // <- ahora usamos la instancia bd

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

    public void cargar() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'cargar'");
    }
}
