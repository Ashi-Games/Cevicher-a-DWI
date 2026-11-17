import { HttpErrorResponse } from '@angular/common/http';
import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ApiError } from '../../core/models';

/** true si el campo es invalido y el usuario ya lo toco. */
export function esInvalido(form: FormGroup, campo: string): boolean {
  const c = form.get(campo);
  return !!c && c.invalid && (c.touched || c.dirty);
}

/** Copia los errores de validacion del backend (ApiError.errores) a los campos del formulario. */
export function aplicarErroresServidor(form: FormGroup, err: unknown): void {
  if (!(err instanceof HttpErrorResponse) || err.status !== 400) return;
  const errores = (err.error as Partial<ApiError> | null)?.errores ?? {};
  for (const [campo, mensaje] of Object.entries(errores)) {
    const control = form.get(campo);
    if (control) {
      control.setErrors({ ...(control.errors ?? {}), servidor: mensaje });
      control.markAsTouched();
    }
  }
}

/** Valida que fecha + hora no esten en el pasado. */
export const fechaHoraFutura: ValidatorFn = (grupo: AbstractControl): ValidationErrors | null => {
  const fecha = grupo.get('fecha')?.value as string;
  const hora = grupo.get('hora')?.value as string;
  if (!fecha || !hora) return null;
  return new Date(`${fecha}T${hora}:00`) < new Date() ? { fechaPasada: true } : null;
};

/** Fecha local en formato yyyy-MM-dd (sin desfase de zona horaria). */
export function fechaISO(d = new Date()): string {
  const mm = String(d.getMonth() + 1).padStart(2, '0');
  const dd = String(d.getDate()).padStart(2, '0');
  return `${d.getFullYear()}-${mm}-${dd}`;
}
