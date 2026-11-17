import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { finalize } from 'rxjs';
import { OpinionService } from '../../../core/services/opinion.service';
import { ToastService } from '../../../core/services/toast.service';
import { FieldError } from '../../../shared/components/field-error/field-error';
import { aplicarErroresServidor, esInvalido } from '../../../shared/utils/formularios';

@Component({
  selector: 'app-opinar',
  imports: [ReactiveFormsModule, FieldError],
  templateUrl: './opinar.html',
})
export class Opinar {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly opinionService = inject(OpinionService);
  private readonly toast = inject(ToastService);

  protected readonly enviando = signal(false);
  protected readonly enviado = signal(false);

  protected readonly form = this.fb.group({
    nombres: ['', [Validators.required, Validators.maxLength(100)]],
    correo: ['', [Validators.required, Validators.email, Validators.maxLength(120)]],
    comentario: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(600)]],
  });

  protected invalido(campo: string): boolean {
    return esInvalido(this.form, campo);
  }

  protected enviar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.enviando.set(true);
    this.opinionService
      .crear(this.form.getRawValue())
      .pipe(finalize(() => this.enviando.set(false)))
      .subscribe({
        next: () => {
          this.toast.exito('¡Gracias por tu opinión!');
          this.enviado.set(true);
          this.form.reset();
        },
        error: (e) => aplicarErroresServidor(this.form, e),
      });
  }
}
