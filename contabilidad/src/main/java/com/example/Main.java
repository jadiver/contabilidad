package com.example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BaseDeDatos bd = new BaseDeDatos("postgres", "lolirosa123");

        if (!BaseDeDatos.conecta()) {
            System.out.println("❌ No se pudo conectar a la base de datos");
            return;
        }

        System.out.println("✅ Conexión exitosa!");
        PGC pgc = new PGC(bd);

        while (true) {
            System.out.println("\n¿Qué deseas hacer?");
            System.out.println("1. Listar cuentas");
            System.out.println("2. Insertar nuevo asiento");
            System.out.println("3. Leer un asiento por ID");
            System.out.println("0. Salir");

            System.out.print("Elige una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    System.out.print("¿Deseas filtrar por grupo (ej: 4, 5, 6...)? (Deja vacío para mostrar todas): ");
                    String grupo = scanner.nextLine();
                    pgc.listar(grupo);
                    break;

                case "2":
                    System.out.print("Introduce el número de asiento: ");
                    int id = Integer.parseInt(scanner.nextLine());

                    System.out.print("Introduce la descripción: ");
                    String descripcion = scanner.nextLine();

                    Asiento asiento = new Asiento(id, descripcion, LocalDate.now());

                    System.out.print("¿Cuántos apuntes deseas añadir?: ");
                    int cantidadApuntes = Integer.parseInt(scanner.nextLine());

                    for (int i = 0; i < cantidadApuntes; i++) {
                        System.out.println("Apunte " + (i + 1));
                        System.out.print("Código de cuenta: ");
                        String cuenta = scanner.nextLine();

                        System.out.print("Tipo de movimiento (D/H): ");
                        String tipo = scanner.nextLine().toUpperCase();

                        Movimientos movimiento;
                        if (tipo.equals("D")) {
                            movimiento = Movimientos.DEBE;
                        } else if (tipo.equals("H")) {
                            movimiento = Movimientos.HABER;
                        } else {
                            System.out.println("❌ Tipo inválido. Usa D o H.");
                            continue;
                        }

                        System.out.print("Importe: ");
                        BigDecimal importe = new BigDecimal(scanner.nextLine());

                        asiento.addApunte(new Apunte(id, cuenta, movimiento, importe));
                    }

                    if (asiento.insertarAsiento()) {
                        System.out.println("✅ Asiento insertado correctamente.");
                    } else {
                        System.out.println("❌ Error al insertar el asiento.");
                    }
                    break;

                case "3":
                    System.out.print("Introduce el ID del asiento a buscar: ");
                    int buscarId = Integer.parseInt(scanner.nextLine());
                    Asiento resultado = Asiento.getAsientoBD(buscarId, bd);

                    if (resultado != null) {
                        System.out.println("📝 Asiento encontrado:");
                        System.out.println(resultado);
                    } else {
                        System.out.println("❌ No se encontró el asiento.");
                    }
                    break;

                case "0":
                    System.out.println("👋 ¡Hasta luego!");
                    return;

                default:
                    System.out.println("⚠️ Opción no válida. Intenta de nuevo.");
            }
        }
    }
}
