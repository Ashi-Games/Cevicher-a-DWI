package com.rinconcito.marino.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "opiniones")
@Getter
@Setter
public class Opinion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no debe superar 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombres;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "Ingresa un correo valido")
    @Size(max = 120, message = "El correo no debe superar 120 caracteres")
    @Column(nullable = false, length = 120)
    private String correo;

    @NotBlank(message = "El comentario es obligatorio")
    @Size(min = 5, max = 600, message = "El comentario debe tener entre 5 y 600 caracteres")
    @Column(nullable = false, length = 600)
    private String comentario;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    @PrePersist
    void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }
}