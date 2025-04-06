package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDeDatos {
    private static Connection conexion = null;
    private String host = "127.0.0.1";
    String port = "5432";
    String dbName = ""; // Poner la base de datos que toca

    public BaseDeDatos(String usuario, String contrasenya) {
        String url = "jdbc:postgresql://" + host + ":" + port + "/contabilidad";

        try {
            conexion = DriverManager.getConnection(url, usuario, contrasenya);
            System.out.println("Conexión exitosa!");
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    // 1. Crear base de datos
    public void crearBaseDatos(String nombreBD) {
        String sql = "CREATE DATABASE " + nombreBD;
        try (Statement stmt = conexion.createStatement()) {
            conexion.setAutoCommit(true); // Requerido para CREATE DATABASE
            stmt.execute(sql);
            System.out.println("Base de datos '" + nombreBD + "' creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear BD: " + e.getMessage());
        }
    }

    // 2. Listar bases de datos
    public List<String> listarBasesDatos() {
        List<String> basesDatos = new ArrayList<>();
        try (Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT datname FROM pg_database")) {
            while (rs.next()) {
                basesDatos.add(rs.getString("datname"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar BDs: " + e.getMessage());
        }
        return basesDatos;
    }

    // 3. Crear tabla
    public void crearTabla(String nombreTabla, String... columnas) {
        StringBuilder sql = new StringBuilder("CREATE TABLE " + nombreTabla + " (");
        for (int i = 0; i < columnas.length; i++) {
            sql.append(columnas[i]);
            if (i < columnas.length - 1)
                sql.append(", ");
        }

        sql.append(")");
        try (Statement stmt = conexion.createStatement()) {
            stmt.execute(sql.toString());
            System.out.println("Tabla '" + nombreTabla + "' creada.");
        } catch (SQLException e) {
            System.err.println("Error al crear tabla: " + e.getMessage());
        }
    }

    // 4. Listar tablas
    public List<String> listarTablas(String esquema) {
        List<String> tablas = new ArrayList<>();
        String sql = "SELECT table_name FROM information_schema.tables " +
                "WHERE table_schema = '" + esquema + "'";
        try (Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tablas.add(rs.getString("table_name"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tablas: " + e.getMessage());
        }
        return tablas;
    }

    /**
     * Ejecuta cualquier operación SQL directamente (INSERT/UPDATE/DELETE)
     * 
     * @param sql Consulta SQL completa y formada
     * @return Número de filas afectadas
     */
    public int ejecutar(String sql) {

        System.out.println(sql);
        try (Statement stmt = conexion.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error en operación: " + e.getMessage());
            return -1;
        }
    }

    public List<Map<String, Object>> consultar(String sql) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        try (Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            while (rs.next()) {
                Map<String, Object> fila = new HashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    fila.put(metaData.getColumnName(i), rs.getObject(i));
                }
                resultados.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta: " + e.getMessage());
        }
        return resultados;
    }

    // Cerrar conexión
    public void cerrarConexion() {

        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }
}