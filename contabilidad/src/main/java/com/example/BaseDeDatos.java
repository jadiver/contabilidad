package com.example;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseDeDatos {
    private static Connection conexion = null;
    private final String host = "127.0.0.1";
    private final String port = "5432";
    private final static String dbName = "contabilidad"; // Base de datos a usar

    public BaseDeDatos(String usuario, String contrasenya) {
        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        try {
            conexion = DriverManager.getConnection(url, usuario, contrasenya);
            System.out.println("Conexión exitosa!");
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
        }
    }

    // Crear base de datos
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

    // Listar bases de datos
    public List<String> listarBasesDatos() {
        List<String> basesDatos = new ArrayList<>();
        String sql = "SELECT datname FROM pg_database";

        try (Statement stmt = conexion.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                basesDatos.add(rs.getString("datname"));
            }
        } catch (SQLException e) {
            System.err.println("Error al listar BDs: " + e.getMessage());
        }
        return basesDatos;
    }

    // Crear tabla
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

    // Listar tablas
    public List<String> listarTablas(String esquema) {
        List<String> tablas = new ArrayList<>();
        String sql = "SELECT table_name FROM information_schema.tables WHERE table_schema = ?";

        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setString(1, esquema);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tablas.add(rs.getString("table_name"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tablas: " + e.getMessage());
        }
        return tablas;
    }

    // Ejecutar cualquier operación SQL (INSERT/UPDATE/DELETE)
    public int ejecutar(String sql) {
        System.out.println(sql);
        try (Statement stmt = conexion.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.err.println("Error en operación: " + e.getMessage());
            return -1;
        }
    }

    // Consultar resultados
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

    // Verificar si la conexión está activa
    public static boolean conecta() {
        if (conexion != null)
            return true;
        try {
            String url = "jdbc:postgresql://127.0.0.1:5432/" + dbName;
            conexion = DriverManager.getConnection(url, "postgres", "lolirosa123"); // pon tus datos
            return true;
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return false;
        }
    }

    // Verificar si está conectado
    public boolean estaConectado() {
        return conexion != null;
    }

    // Insertar datos con parámetros
    public static int insertar(String sql, List<Object> parametros) {
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            for (int i = 0; i < parametros.size(); i++) {
                pstmt.setObject(i + 1, parametros.get(i));
            }
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error al insertar: " + e.getMessage());
            return -1;
        }
    }

    // Listar asientos por fecha desde la tabla DIARIO
    public List<Map<String, Object>> listarAsientosPorFecha(LocalDate fecha) {
        String sql = "SELECT asiento, descripcion, fecha FROM diario WHERE fecha = ? ORDER BY fecha";

        List<Map<String, Object>> resultados = new ArrayList<>();
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(fecha)); // Convertir LocalDate a Date
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    fila.put("asiento", rs.getInt("asiento"));
                    fila.put("descripcion", rs.getString("descripcion"));
                    fila.put("fecha", rs.getDate("fecha")); // Devuelve un java.sql.Date
                    resultados.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta de asientos: " + e.getMessage());
        }
        return resultados;
    }

    public List<Map<String, Object>> consultar(String sql, String parametro) {
        List<Map<String, Object>> resultados = new ArrayList<>();
        try (PreparedStatement pstmt = conexion.prepareStatement(sql)) {
            // Establecemos el parámetro para la consulta
            pstmt.setString(1, parametro);

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                while (rs.next()) {
                    Map<String, Object> fila = new HashMap<>();
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        fila.put(metaData.getColumnName(i), rs.getObject(i));
                    }
                    resultados.add(fila);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error en consulta: " + e.getMessage());
        }
        return resultados;
    }

    public List<Map<String, Object>> listarAsientosPorRangoFecha(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Map<String, Object>> asientos = new ArrayList<>();
        String sql = "SELECT * FROM diario WHERE fecha BETWEEN ? AND ? ORDER BY fecha";

        try (PreparedStatement stmt = conexion.prepareStatement(sql)) {
            // Establecemos los parámetros para la consulta
            stmt.setDate(1, Date.valueOf(fechaInicio));
            stmt.setDate(2, Date.valueOf(fechaFin));

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> asiento = new HashMap<>();
                asiento.put("asiento", rs.getInt("asiento"));
                asiento.put("descripcion", rs.getString("descripcion"));
                asiento.put("fecha", rs.getDate("fecha"));
                asientos.add(asiento);
            }
        } catch (SQLException e) {
            System.out.println("❌ Error al ejecutar la consulta: " + e.getMessage());
        }
        return asientos;
    }

}
