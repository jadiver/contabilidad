package com.example;

public class Cuenta {
    public String codigo;
    public String nombre;

    public Cuenta() {
    }

    public Cuenta(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre;
    }
}
