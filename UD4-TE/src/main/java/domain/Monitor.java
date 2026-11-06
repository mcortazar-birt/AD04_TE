package domain;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "monitor")
public class Monitor {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;
    
    @Column(nullable = false, length = 100)
    private String especialidad;
    
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "monitor_id")
    private List<Actividad> actividades = new ArrayList<Actividad>();
    
    protected Monitor() { }

    public Monitor(String nombre, String especialidad) {
        cambiarNombre(nombre);
        cambiarEspecialidad(especialidad);
    }

    public void cambiarNombre(String nombre) {
        String limpio = Objects.requireNonNull(nombre).trim();
        if (limpio.isEmpty() || limpio.length() > 100) {
            throw new IllegalArgumentException("Nombre no valido.");
        }
        this.nombre = limpio;
    }
    
    public void cambiarEspecialidad(String especialidad) {
        String limpio = Objects.requireNonNull(especialidad).trim();
        if (limpio.isEmpty() || limpio.length() > 150) {
            throw new IllegalArgumentException("Especialidad no valida.");
        }
        this.especialidad = limpio;
    }

    public void addActividad(Actividad actividad) {
    	Objects.requireNonNull(actividad);
    	if (actividad.getMonitor() != this) {
    		throw new IllegalArgumentException("La actividad pertenece a otro monitor");
    	}
    	if (!actividades.contains(actividad)) {
    		actividades.add(actividad);
    	}
    }
    
    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecialidad() { return especialidad; }
    public List<Actividad> getActividades() { return actividades; }
}

