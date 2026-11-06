package domain;

import java.time.LocalDateTime;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reserva")
public class Reserva {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name="fecha_reserva", nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(name="numero_plazas", nullable = false)
    private int plazas;
    
    @ManyToOne(optional = false, cascade = CascadeType.DETACH)
    @JoinColumn(name = "actividad_id", nullable = false)
    private Actividad actividad;
    
    @ManyToOne(optional = false, cascade = CascadeType.DETACH)    
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    protected Reserva () {}
    
    public Reserva (LocalDateTime fechaHora, int plazas, Cliente cliente, Actividad actividad) {
    	if (plazas <= 0) throw new IllegalArgumentException("Las palzas deben de ser positivas");
    	this.cliente = Objects.requireNonNull(cliente);
    	this.actividad = Objects.requireNonNull(actividad);
    	this.fechaHora = Objects.requireNonNull(fechaHora);
    	this.plazas = plazas;
    }
    
    public void cancelar() {
    	this.actividad.liberar(plazas);
    }

	public Long getId() {
		return id;
	}

	public LocalDateTime getFechaHora() {
		return fechaHora;
	}

	public int getPlazas() {
		return plazas;
	}

	public Actividad getActividad() {
		return actividad;
	}

	public Cliente getCliente() {
		return cliente;
	}
    
}
