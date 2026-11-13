package com.rinconcito.marino.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "atenciones")
@Getter
@Setter
public class Atencion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reserva_id", nullable = false, unique = true)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private UsuarioAdmin usuario;

    @NotNull(message = "La hora de llegada es obligatoria")
    @Column(name = "hora_llegada", nullable = false)
    private LocalDateTime horaLlegada;

    @Column(name = "hora_salida")
    private LocalDateTime horaSalida;

    @Min(value = 0, message = "Las personas atendidas no pueden ser negativas")
    @Column(name = "personas_atendidas")
    private Integer personasAtendidas;

    @Size(max = 300, message = "Las observaciones no deben superar 300 caracteres")
    @Column(length = 300)
    private String observaciones;
}