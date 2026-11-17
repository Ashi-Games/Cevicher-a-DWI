import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { ReservaCreada } from '../../../core/models';
import { ReservaService } from '../../../core/services/reserva.service';
import { FieldError } from '../../../shared/components/field-error/field-error';
import { aplicarErroresServidor, esInvalido, fechaHoraFutura, fechaISO } from '../../../shared/utils/formularios';
import { ResumenReserva } from './resumen-reserva';

@Component({
  selector: 'app-reservar',
  imports: [ReactiveFormsModule, FieldError, ResumenReserva],
  templateUrl: './reservar.html',
})
export class Reservar {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly reservaService = inject(ReservaService);

  protected readonly hoy = fechaISO();
  protected readonly horas = this.generarHoras(11, 17);
  protected readonly enviando = signal(false);
  protected readonly creada = signal<ReservaCreada | null>(null);

  protected readonly form = this.fb.group(
    {
      nombres: ['', [Validators.required, Validators.maxLength(80)]],
      apellidos: ['', [Validators.required, Validators.maxLength(80)]],
      dni: ['', [Validators.required, Validators.pattern(/^\d{8}$/)]],
      edad: [18, [Validators.required, Validators.min(18), Validators.max(120)]],
      email: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
      telefono: ['', [Validators.required, Validators.pattern(/^\d{9}$/)]],
      personas: [2, [Validators.required, Validators.min(1), Validators.max(20)]],
      fecha: [this.hoy, Validators.required],
      hora: ['', Validators.required],
    },
    { validators: fechaHoraFutura },
  );

  protected invalido(campo: string): boolean {
    return esInvalido(this.form, campo);
  }

  protected enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.reservaService
      .crear(this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: (r) => {
          this.creada.set(r);
          window.scrollTo({ top: 0, behavior: 'smooth' });
        },
        error: (e) => aplicarErroresServidor(this.form, e),
      });
  }

  protected nuevaReserva(): void {
    this.creada.set(null);
    this.form.reset();
  }

  private generarHoras(desde: number, hasta: number): string[] {
    const horas: string[] = [];
    for (let h = desde; h <= hasta; h++) {
      horas.push(`${String(h).padStart(2, '0')}:00`);
      if (h < hasta) horas.push(`${String(h).padStart(2, '0')}:30`);
    }
    return horas;
  }
}
