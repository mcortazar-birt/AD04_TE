package main;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Scanner;

import config.JpaUtil;
import domain.Actividad;
import jakarta.persistence.EntityManagerFactory;
import service.TEService;

public class MainApp {
    public static void main(String[] args) {
        try (EntityManagerFactory emf = JpaUtil.crearFactory();
             Scanner teclado = new Scanner(System.in)) {
            TEService servicio = new TEService(emf);
            boolean seguir = true;
            while (seguir) {
                System.out.println("""
                    === ACTIVA Gestión de actividades ===
                    1. Crear monitor
                    2. Crear actividad
                    3. Buscar actividades disponibles
                    4. Crear cliente
                    5. Crear reserva
                    6. Cancelar reserva
                    7. Listar actividades
                    8. Salir
                    """);
                if (!teclado.hasNextLine()) break;
                String opcion = texto(teclado, "Opcion: ");
                try {
                    switch (opcion) {
                        case "1" -> System.out.println("Monitor: " + servicio.crearMonitor(
                            texto(teclado, "Nombre: "), texto(teclado, "Especialidad: ")));
                        case "2" -> System.out.println("Actividad: " + servicio.crearActividad(
                            texto(teclado, "Nombre: "), texto(teclado, "Localidad: "), fechaHora(teclado, "Fecha y hora(dd/MM/yyyy HH:mm): "),
                            entero(teclado, "Plazas totales: "), id(teclado, "Id Monitor: ")));
                        case "3" -> mostrarActividades(servicio.buscarActividades(
                            texto(teclado, "Localidad: "), fecha(teclado, "Fecha(dd/MM/yyy): ") ,entero(teclado, "Plazas disponibles: ")));
                        case "4" -> System.out.println("Cliente: " + servicio.crearCliente(
                            texto(teclado, "Nombre: "), texto(teclado, "Email: ")));
                        case "5" -> System.out.println("Reserva: " + servicio.crearReserva(
                            id(teclado, "Id Cliente: "), id(teclado, "Id Actividad: "),
                            entero(teclado, "Unidades: ")));
                        case "6" -> {
                            int[] plazas = servicio.cancelarReserva(id(teclado, "Id reserva: "));
                            System.out.println("Reserva cancelada. Liberadas " + plazas[0] + " plazas, para un total de " + plazas[1] + " libres.");
                        }
                        case "7" -> 
                            mostrarActividades(servicio.listaActividades());
                        case "8" -> seguir = false;
                        default -> System.out.println("Opcion no valida.");
                    }
                } catch (RuntimeException ex) {
                    // Entorno de aprendizaje: mensaje y traza para localizar la causa.
                    System.err.println("No se completo la operacion: " + ex.getMessage());
                    ex.printStackTrace(System.err);
                }
            }
        } catch (RuntimeException ex) {
            System.err.println("No se pudo iniciar la aplicacion: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }

    

    private static void mostrarActividades(List<Actividad> buscarActividades) {
    	if (buscarActividades == null || buscarActividades.size() == 0) {
    		System.out.println("No hay actividades para los parámetros dados");
    		return;
    	}
    	System.out.printf("%-3s | %-40s | %-40s | %-40s | %s \n", "ID", "Nombre Actividad", "Fecha", "Monitor/a", "Plazas Disponibles");
    	for (Actividad a : buscarActividades) {
    		System.out.printf("%-3d | %-40s | %-40s | %-40s | %d plazas libres\n", a.getId(), a.getNombre(),
                    a.getFechaHora().toString(), a.getMonitor().getNombre() ,a.getPlazasDisponibles());
        }
    	

	}

	private static LocalDate fecha(Scanner teclado, String string) {
		LocalDate fecha = LocalDate.parse(texto(teclado, string).trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
		return fecha;
    }

	private static LocalDateTime fechaHora(Scanner teclado, String string) {
		LocalDateTime fecha = LocalDateTime.parse(texto(teclado, string).trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
		return fecha;
	}

    private static String texto(Scanner teclado, String mensaje) {
        System.out.print(mensaje);
        return teclado.nextLine().trim();
    }

    private static int entero(Scanner teclado, String mensaje) {
        return Integer.parseInt(texto(teclado, mensaje));
    }

    private static int enteroPositivo(Scanner teclado, String mensaje) {
    	int candidato = 0;
    	while (candidato <= 0) {
    		entero(teclado, mensaje);
    	}
    	return candidato;
    }
    
    private static int enteroEntre(Scanner teclado, String mensaje, int suelo, int techo) {
    	int candidato = Integer.MIN_VALUE;
    	while (candidato > techo || candidato < suelo) {
    		entero(teclado, mensaje);
    	}
    	return candidato;
    }
    
    private static Long id(Scanner teclado, String mensaje) {
        return Long.parseLong(texto(teclado, mensaje));
    }
}