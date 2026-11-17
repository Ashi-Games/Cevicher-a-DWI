import { Component, inject, input, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';
import { AuthService } from '../../../core/services/auth.service';
import { FieldError } from '../../../shared/components/field-error/field-error';
import { esInvalido } from '../../../shared/utils/formularios';

@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, FieldError, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.scss',
})
export class Login {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  /** Ruta a la que se vuelve despues del login (query param ?volver=). */
  readonly volver = input<string>();

  protected readonly cargando = signal(false);
  protected readonly verPassword = signal(false);
  protected readonly form = this.fb.group({
    usuario: ['', Validators.required],
    password: ['', Validators.required],
  });

  protected invalido(campo: string): boolean {
    return esInvalido(this.form, campo);
  }

  protected ingresar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.cargando.set(true);
    this.auth
      .login(this.form.getRawValue())
      .pipe(finalize(() => this.cargando.set(false)))
      .subscribe(() => this.router.navigateByUrl(this.volver() || '/admin'));
  }
}
