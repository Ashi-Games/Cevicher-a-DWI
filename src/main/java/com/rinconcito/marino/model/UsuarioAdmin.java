package com.rinconcito.marino.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "usuarios_admin")
@Getter
@Setter
public class UsuarioAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 3, max = 40, message = "El usuario debe tener entre 3 y 40 caracteres")
    @Column(nullable = false, unique = true, length = 40)
    private String usuario;

    @NotBlank(message = "La contrasena es obligatoria")
    @Column(nullable = false, length = 255)
    private String password;

    @NotNull(message = "El rol es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol = RolUsuario.EMPLEADO;
}