package domain;
import jakarta.persistence.*;
import java.util.Locale;
import java.util.Objects;

@Entity
@Table(name = "cliente", uniqueConstraints =
@UniqueConstraint(name = "uk_socio_email", columnNames = "email"))
public class Cliente {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 150)
    private String email;
    
    protected Cliente() { }

    public Cliente(String nombre, String email) {
        cambiarNombre(nombre);
        this.email = Objects.requireNonNull(email).trim().toLowerCase(Locale.ROOT);
        // Comprobacion introductoria, no un validador completo de correo.
        if (this.email.length() > 150 || !this.email.matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("Correo no valido.");
        }
    }

    public void cambiarNombre(String nombre) {
        String limpio = Objects.requireNonNull(nombre).trim();
        if (limpio.isEmpty() || limpio.length() > 100) {
            throw new IllegalArgumentException("Nombre no valido.");
        }
        this.nombre = limpio;
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
}

