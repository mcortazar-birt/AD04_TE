package domain;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "actividad")
public class Actividad {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String localidad;
    
    @Column(name="fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    

	@Column(name="plazas_totales", nullable = false)
    private int plazasTotales;
    
    @Column(name="plazas_disponibles", nullable = false)
    private int plazasDisponibles;
    
    @ManyToOne(optional = false) 
    @JoinColumn(name = "monitor_id", nullable = false)
    private Monitor monitor;
    
    protected Actividad() {}
    
    public Actividad(String nombre, String localidad, LocalDateTime fechaHora, int plazasTotales, Monitor monitor) {
    	setNombre(nombre);
    	setLocalidad(localidad);
        this.fechaHora = Objects.requireNonNull(fechaHora);
        setPlazasTotales(plazasTotales);
        plazasDisponibles = plazasTotales;
        this.monitor = Objects.requireNonNull(monitor);
    }
    
    public void setNombre(String nombre) {
    	String limpio = Objects.requireNonNull(nombre).trim();
        if (limpio.isEmpty() || limpio.length() > 120) {
            throw new IllegalArgumentException("Nombre no valido.");
        }
        this.nombre = limpio;
    }
    
    public void setLocalidad(String localidad) {
    	String limpio = Objects.requireNonNull(localidad).trim();
        if (limpio.isEmpty() || limpio.length() > 100) {
            throw new IllegalArgumentException("Localidad no valida.");
        }
        this.localidad = limpio;
    }
    
    public void setPlazasTotales(int plazasTotales) {
    	int limpio = Objects.requireNonNull(plazasTotales);
        if (limpio <= 0) 
            throw new IllegalArgumentException("Plazas totales no validas.");
        if (limpio < plazasTotales - plazasDisponibles)
        	throw new IllegalArgumentException("Cantidad de plazas totales menos a plazas asignadas");
        
        this.plazasTotales = limpio;
    }
    
    public void reservar(int plazas) {
    	if (plazas <= 0 || plazas > plazasDisponibles) {
    		throw new IllegalArgumentException("La reserva supera las plazas disponibles");
    	}
    	plazasDisponibles -= plazas;
    }
 
    public void liberar(int plazas) {
    	if (plazas <= 0 || plazas > plazasTotales - plazasDisponibles)
    		throw new IllegalArgumentException("Valor no válido de plazas a liberar");
    	plazasDisponibles += plazas;
    }
    
    public long getId() {return id;}
    public String getNombre() { return nombre; }
    public String getLocalidad() { return localidad; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public int getPlazasTotales() { return plazasTotales; }
    public int getPlazasDisponibles() { return plazasDisponibles; }
    public Monitor getMonitor() { return monitor; }

    
}
