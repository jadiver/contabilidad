package com.example;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        BaseDeDatos bd = new BaseDeDatos("postgres", "lolirosa123");

        if (!BaseDeDatos.conecta()) {
            System.out.println("❌ No se pudo conectar a la base de datos");
            scanner.close();
            bd.cerrarConexion();
            return;
        }

        System.out.println("✅ Conexión exitosa!");
        PGC pgc = new PGC(bd);

        while (true) {
            System.out.println("\n¿Qué deseas hacer?");
            System.out.println("1. Listar cuentas");
            System.out.println("2. Insertar nuevo asiento");
            System.out.println("3. Leer un asiento por ID");
            System.out.println("4. Listar asientos por fecha");
            System.out.println("5. Listar el mayor de una cuenta");
            System.out.println("6. Listar el diario desde una fecha a otra");
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
                    try {
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
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Error: Entrada no válida. Por favor, introduce un número válido.");
                    }
                    break;

                case "3":
                    try {
                        System.out.print("Introduce el ID del asiento a buscar: ");
                        int buscarId = Integer.parseInt(scanner.nextLine());
                        Asiento resultado = Asiento.getAsientoBD(buscarId, bd);

                        if (resultado != null) {
                            System.out.println("📝 Asiento encontrado:");
                            System.out.println(resultado);
                        } else {
                            System.out.println("❌ No se encontró el asiento.");
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("❌ Error: Entrada no válida. Por favor, introduce un número válido.");
                    }
                    break;

                    case "4":
                    System.out.print("Introduce la fecha (YYYY-MM-DD) para listar los asientos: ");
                    String fechaInput = scanner.nextLine();
                
                    try {
                        // Asegúrate de que la fecha esté en el formato correcto (YYYY-MM-DD)
                        LocalDate fecha = LocalDate.parse(fechaInput);
                
                        // Usamos el método de BaseDeDatos para listar asientos por fecha
                        List<Map<String, Object>> asientosPorFecha = bd.listarAsientosPorFecha(fecha);
                
                        if (asientosPorFecha.isEmpty()) {
                            System.out.println("❌ No se encontraron asientos para la fecha especificada.");
                        } else {
                            for (Map<String, Object> asientoMap : asientosPorFecha) {
                                int idAsiento = (int) asientoMap.get("asiento");
                                String descripcionAsiento = (String) asientoMap.get("descripcion");
                
                                // Convertir la fecha de la base de datos (java.sql.Date) a LocalDate
                                LocalDate fechaAsiento = ((java.sql.Date) asientoMap.get("fecha")).toLocalDate();
                
                                System.out.println("ID: " + idAsiento);
                                System.out.println("Descripción: " + descripcionAsiento);
                                System.out.println("Fecha: " + fechaAsiento);
                                System.out.println("------------------------------");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Error: La fecha introducida no es válida.");
                        e.printStackTrace();  // Para ver el error completo en la consola
                    }
                    break;
                

                case "5":
                    System.out.print("Introduce el código de cuenta: ");
                    String codigoCuenta = scanner.nextLine();

                    // Consulta SQL para obtener el mayor importe de la cuenta
                    String sqlMayorImporte = "SELECT cuenta, MAX(importe) AS mayor_importe "
                            + "FROM detalle_diario "
                            + "WHERE cuenta = ? "
                            + "GROUP BY cuenta";

                    List<Map<String, Object>> resultado = bd.consultar(sqlMayorImporte, codigoCuenta);

                    if (resultado.isEmpty()) {
                        System.out.println("❌ No se encontraron registros para la cuenta especificada.");
                    } else {
                        // Mostrar el mayor importe encontrado
                        for (Map<String, Object> fila : resultado) {
                            String cuentaEncontrada = (String) fila.get("cuenta");
                            BigDecimal mayorImporte = (BigDecimal) fila.get("mayor_importe");

                            System.out.println("Código de cuenta: " + cuentaEncontrada);
                            System.out.println("Mayor importe: " + mayorImporte);
                            System.out.println("------------------------------");
                        }
                    }
                    break;

                case "6":
                    System.out.print("Introduce la fecha de inicio (YYYY-MM-DD): ");
                    String fechaInicioInput = scanner.nextLine();
                    System.out.print("Introduce la fecha de fin (YYYY-MM-DD): ");
                    String fechaFinInput = scanner.nextLine();

                    try {
                        LocalDate fechaInicio = LocalDate.parse(fechaInicioInput);
                        LocalDate fechaFin = LocalDate.parse(fechaFinInput);

                        // Usamos el método de BaseDeDatos para listar los asientos entre las dos fechas
                        List<Map<String, Object>> asientosRangoFecha = bd.listarAsientosPorRangoFecha(fechaInicio, fechaFin);

                        if (asientosRangoFecha.isEmpty()) {
                            System.out.println("❌ No se encontraron asientos en el rango de fechas especificado.");
                        } else {
                            for (Map<String, Object> asientoMap : asientosRangoFecha) {
                                int idAsiento = (int) asientoMap.get("asiento");
                                String descripcionAsiento = (String) asientoMap.get("descripcion");

                                LocalDate fechaAsiento = ((java.sql.Date) asientoMap.get("fecha")).toLocalDate();

                                System.out.println("ID: " + idAsiento);
                                System.out.println("Descripción: " + descripcionAsiento);
                                System.out.println("Fecha: " + fechaAsiento);
                                System.out.println("------------------------------");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("❌ Error: Las fechas introducidas no son válidas.");
                    }
                    break;

                case "0":
                    System.out.println("👋 ¡Hasta luego!");
                    scanner.close(); // Cerramos el scanner al salir
                    bd.cerrarConexion(); // Cerramos la conexión a la base de datos
                    return;

                default:
                    System.out.println("⚠️ Opción no válida. Intenta de nuevo.");
            }
        }
    }
}
