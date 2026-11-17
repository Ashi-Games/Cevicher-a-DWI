import { Component, input } from '@angular/core';
import { AbstractControl } from '@angular/forms';

const MENSAJES: Record<string, (e: any) => string> = {
  required: () => 'Este campo es obligatorio',
  email: () => 'Ingresa un correo válido',
  min: (e) => `El valor mínimo es ${e.min}`,
  max: (e) => `El valor máximo es ${e.max}`,
  minlength: (e) => `Debe tener al menos ${e.requiredLength} caracteres`,
  maxlength: (e) => `No debe superar ${e.requiredLength} caracteres`,
  pattern: () => 'Formato no válido',
  servidor: (e) => e,
};

/** Muestra el primer error de validacion de un campo (del front o del backend). */
@Component({
  selector: 'app-field-error',
  template: `@if (mensaje; as m) { <div class="invalid-feedback d-block">{{ m }}</div> }`,
})
export class FieldError {
  readonly control = input.required<AbstractControl | null>();
  /** Mensajes propios por tipo de error, ej. { pattern: 'El DNI tiene 8 digitos' }. */
  readonly mensajes = input<Record<string, string>>({});

  get mensaje(): string | null {
    const c = this.control();
    if (!c || !c.errors || !(c.touched || c.dirty)) return null;
    const [clave, valor] = Object.entries(c.errors)[0];
    return this.mensajes()[clave] ?? MENSAJES[clave]?.(valor) ?? 'Valor no válido';
  }
}
