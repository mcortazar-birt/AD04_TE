package service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import domain.Actividad;
import domain.Cliente;
import domain.Monitor;
import domain.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Tuple;

public class TEService {

	private final EntityManagerFactory emf;

    public TEService(EntityManagerFactory emf) {
        this.emf = Objects.requireNonNull(emf);
    }
    
    public Long crearMonitor(String nombre, String especialidad) {
        Monitor monitor = new Monitor(nombre, especialidad);
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(monitor);
                tx.commit();
                return monitor.getId();
            } catch (RuntimeException ex) {
                deshacer(tx, ex);
                throw ex;
            }
        }
    }
    
    public Long crearActividad(String nombre, String localidad, LocalDateTime fechaHora, int plazasTotales, Long idMonitor) {
    	try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Monitor monitor = em.find(Monitor.class, idMonitor);
                if (monitor == null) throw new IllegalArgumentException("No existe el monitor");
                Actividad actividad = new Actividad(nombre, localidad, fechaHora, plazasTotales, monitor);
                monitor.addActividad(actividad);
                em.persist(monitor);
                tx.commit();
                return monitor.getId();
            } catch (RuntimeException ex) {
                deshacer(tx, ex);
                throw ex;
            }
        }
    }
    
    public List<Actividad> buscarActividades(String localidad, LocalDate fecha, int cantidadPlazasMinimas) {
        if (cantidadPlazasMinimas <= 0 || localidad == null || localidad.isBlank() || fecha == null) {
            throw new IllegalArgumentException("Indica localidad y plazas positivas.");
        }
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery(
                  "SELECT a FROM Actividad a "
                + "WHERE LOWER(a.localidad) = LOWER(:localidad) "
                + "		AND a.plazasDisponibles >= :plazas "
                + "		AND DATE(a.fechaHora) = :fecha "
                + "ORDER BY a.fechaHora"
                , Actividad.class)
                .setParameter("localidad", localidad.trim())
                .setParameter("plazas", cantidadPlazasMinimas)
                .setParameter("fecha", fecha)
                .getResultList();
        }
    }
    
    public Long crearCliente(String nombre, String email) {
        Cliente cliente = new Cliente(nombre, email);
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                em.persist(cliente);
                tx.commit();
                return cliente.getId();
            } catch (RuntimeException ex) {
                deshacer(tx, ex);
                throw ex;
            }
        }
    }
    
    public Long crearReserva(Long clienteId, Long actividadId, int plazas) {
        if (clienteId < 0 || clienteId == null) throw new IllegalArgumentException("Id de cliente no válida");
        if (actividadId < 0 || actividadId == null) throw new IllegalArgumentException("Id de actividad no válida");
        if (actividadId <= 0) {
            throw new IllegalArgumentException("Las plazas deben ser positivas.");
        }
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Cliente cliente = em.find(Cliente.class, clienteId);
                Actividad actividad = em.find(Actividad.class, actividadId);
                if (cliente == null || actividad == null) {
                    throw new IllegalArgumentException("Socio o equipo inexistente.");
                }
                actividad.reservar(plazas); // Modificacion de una entidad managed.
                Reserva reserva = new Reserva(LocalDateTime.now(), plazas, cliente, actividad);
                em.persist(reserva);
                tx.commit(); // Insercion y descuento se confirman juntos.
                return reserva.getId();
            } catch (RuntimeException ex) {
                deshacer(tx, ex);
                throw ex;
            }
        }
    }

    public int[] cancelarReserva(Long reservaId) {
        if (reservaId < 0 || reservaId == null) throw new IllegalArgumentException("Id de reserva no válida");
        try (EntityManager em = emf.createEntityManager()) {
            EntityTransaction tx = em.getTransaction();
            try {
                tx.begin();
                Reserva reserva = em.find(Reserva.class, reservaId);
                if (reserva == null) {
                    throw new IllegalArgumentException("No existe la reserva.");
                }
                reserva.cancelar();
                em.remove(reserva);
                tx.commit();
                return new int[]{reserva.getPlazas(), reserva.getActividad().getPlazasDisponibles()};
            } catch (RuntimeException ex) {
                deshacer(tx, ex);
                throw ex;
            }
        }
    }
    
    public List<Actividad> listaActividades() {
    	try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("""
                SELECT a FROM Actividad a
                ORDER BY a.fechaHora
                """, Actividad.class).getResultList();
        }
    }
    
    // Devuelve true si ha sucedido un rollback
    public static boolean deshacer(EntityTransaction tx, RuntimeException original) {
    	if (tx.isActive()) {
    		try {
    			tx.rollback();
    			return true;
    		} catch (RuntimeException errorRollback) {
    			original.addSuppressed(errorRollback);
    			return false;
    		}
    	}
    	
    	return false;
    } 
}
