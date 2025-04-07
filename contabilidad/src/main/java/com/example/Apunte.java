package com.example;

import java.math.BigDecimal;

enum Movimientos {
    DEBE('D'), HABER('H');

    char codigo;

    Movimientos(char codigo) {
        this.codigo = codigo;
    }

    public char getCodigo() {
        return codigo;
    }

    public static Movimientos fromCodigo(char codigo) {
        for (Movimientos movimiento : Movimientos.values()) {
            if (movimiento.getCodigo() == codigo) {
                return movimiento;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }
}

public class Apunte {
    private int asientoId;
    private String cuentaCodigo;
    private Movimientos tipoMovimiento; // 'D' para débito, 'H' para haber
    private BigDecimal importe;

    public Apunte(int asientoId, String cuentaCodigo, Movimientos tipoMovimiento,
            BigDecimal importe) {
        this.asientoId = asientoId;
        this.cuentaCodigo = cuentaCodigo;
        this.tipoMovimiento = tipoMovimiento;
        this.importe = importe;

    }
    

    public int getAsientoId() {
        return asientoId;
    }

    public String getCuentaCodigo() {
        return cuentaCodigo;
    }

    public Movimientos getTipoMovimiento() {
        return tipoMovimiento;
    }

    public BigDecimal getImporte() {
        return importe;
    }
}