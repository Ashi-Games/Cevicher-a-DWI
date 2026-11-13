package com.rinconcito.marino.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "mesas")
@Getter
@Setter
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El numero de mesa es obligatorio")
    @Min(value = 1, message = "El numero de mesa debe ser mayor que cero")
    @Column(nullable = false, unique = true)
    private Integer numero;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que cero")
    @Column(nullable = false)
    private Integer capacidad;

    @Size(max = 30, message = "La zona no debe superar 30 caracteres")
    @Column(length = 30)
    private String zona;

    @Column(nullable = false)
    private boolean activa = true;
}