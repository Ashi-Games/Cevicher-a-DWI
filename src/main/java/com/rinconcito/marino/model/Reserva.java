package com.rinconcito.marino.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservas")
@Getter
@Setter
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Los nombres son obligatorios")
    @Size(max = 80, message = "Los nombres no deben superar 80 caracteres")
    @Column(nullable = false, length = 80)
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 80, message = "Los apellidos no deben superar 80 caracteres")
    @Column(nullable = false, length = 80)
    private String apellidos;

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener 8 digitos numericos")
    @Column(nullable = false, length = 8)
    private String dni;

    @NotNull(message = "La edad es obligatoria")
    @Min(value = 18, message = "Debes ser mayor de edad para reservar")
    @Max(value = 120, message = "La edad ingresada no es valida")
    @Column(nullable = false)
    private Integer edad;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo valido")
    @Size(max = 120, message = "El correo no debe superar 120 caracteres")
    @Column(nullable = false, length = 120)
    private String email;

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "\\d{9}", message = "El telefono debe tener 9 digitos")
    @Column(nullable = false, length = 9)
    private String telefono;

    @NotNull(message = "La cantidad de personas es obligatoria")
    @Min(value = 1, message = "Debe reservar al menos una persona")
    @Max(value = 20, message = "Para mas de 20 personas contactanos por WhatsApp")
    @Column(nullable = false)
    private Integer personas;

    @NotNull(message = "La fecha es obligatoria")
    @Column(nullable = false)
    private LocalDate fecha;

    @NotNull(message = "La hora es obligatoria")
    @Column(nullable = false)
    private LocalTime hora;

    @NotNull(message = "El estado es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoReserva.PENDIENTE;
        }
    }
}