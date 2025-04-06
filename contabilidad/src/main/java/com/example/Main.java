package com.example;

public class Main {
    public static void main(String[] args) {
        // Inicializa la conexión (aquí es donde se debe poner el nombre correcto de la base)
        BaseDeDatos conexion = new BaseDeDatos("postgres", "lolirosa123");
        conexion.dbName = "contabilidad"; // Asegurate de setear el nombre correcto

        PGC pgc = new PGC();
        pgc.listar(""); // Pasá "1" o "6", etc. para filtrar por grupo, o "" para todo

        conexion.cerrarConexion();
    }
}
