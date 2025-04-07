package com.example;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Asiento {
    private int id;
    private String descripcion;
    private LocalDate fecha;
    private List<Apunte> apuntes;

    public Asiento(int id, String descripcion, LocalDate fecha) {
        this.id = id;
        this.descripcion = descripcion;
        this.fecha = fecha;
        apuntes = new ArrayList<>();
    }

    public static Asiento getAsientoBD(int id, BaseDeDatos bd) {
        List<Map<String, Object>> datos = bd.consultar("SELECT * FROM Diario WHERE Asiento=" + id);
        if (datos.isEmpty()) {
            System.err.println("No se encontró ningún asiento con ID: " + id);
            return null;
        }
    
        String descripcion = (String) datos.get(0).get("descripcion");
        Date fechaBD = (Date) datos.get(0).get("fecha");
        LocalDate fecha = fechaBD.toLocalDate();
        Asiento asiento = new Asiento(id, descripcion, fecha);
    
        // Insertar detalle de Diario
        List<Map<String, Object>> filas = bd.consultar("SELECT * FROM detalle_diario WHERE Asiento=" + id);
        for (Map<String, Object> fila : filas) {
            String cuentaCodigo = (String) fila.get("cuenta");
            String tipoMovimiento = (String) fila.get("tipo_movimiento");
            Movimientos movimiento = Movimientos.fromCodigo(tipoMovimiento.charAt(0));
    
            BigDecimal importe = (BigDecimal) fila.get("importe");
            Apunte apunte = new Apunte(id, cuentaCodigo, movimiento, importe);
            asiento.addApunte(apunte);
        }
        return asiento;
    }
    

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void addApunte(Apunte apunte) {
        apuntes.add(apunte);
    }

    public List<Apunte> getApuntes() {
        return apuntes;
    }

    public void insertaAsientoBD() {
    }

    @Override
    public String toString() {
        String apuntesString = "";
        for (Apunte apunte : apuntes) {
            apuntesString += apunte.toString() + "\n";
        }

        return "Asiento{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", fecha='" + fecha + '\'' +
                "\n" + apuntesString +
                '}';
    }

    public static void main(String[] args) {
        boolean conecta = BaseDeDatos.conecta();
        System.out.println(conecta);
        Asiento asiento = Asiento.getAsientoBD(1, null);
        System.out.println(asiento);
    }

    public boolean insertarAsiento() {
        boolean exito = true;
    
        // 1. Insertar en la tabla 'diario'
        String sqlDiario = String.format(
            "INSERT INTO diario (asiento, descripcion, fecha) VALUES (%d, '%s', '%s')",
            this.id, this.descripcion, this.fecha.toString()
        );
    
        if (BaseDeDatos.insertar(sqlDiario, new ArrayList<>()) <= 0) {
            System.err.println("❌ Error al insertar en 'diario'");
            exito = false;
        }
        
    
        // 2. Insertar cada apunte en 'detalle_diario'
        for (Apunte apunte : apuntes) {
            String sqlDetalle = String.format(
                "INSERT INTO detalle_diario (asiento, cuenta, tipo_movimiento, importe) VALUES (?, ?, ?, ?)",
                this.id,
                apunte.getCuentaCodigo(),
                apunte.getTipoMovimiento(),
                apunte.getImporte()
            );
    
            if (BaseDeDatos.insertar(sqlDetalle, new ArrayList<>()) <= 0) {
                System.err.println("❌ Error al insertar en 'detalle_diario'");
                exito = false;
            }
            
        }
    
        return exito;
    }
    
}