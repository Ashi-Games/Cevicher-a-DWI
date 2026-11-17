import { Component, OnInit, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Mesa } from '../../../core/models';
import { MesaService } from '../../../core/services/mesa.service';
import { ToastService } from '../../../core/services/toast.service';
import { FieldError } from '../../../shared/components/field-error/field-error';
import { aplicarErroresServidor, esInvalido } from '../../../shared/utils/formularios';

@Component({
  selector: 'app-mesas',
  imports: [ReactiveFormsModule, FieldError],
  templateUrl: './mesas.html',
})
export class Mesas implements OnInit {
  private readonly mesaService = inject(MesaService);
  private readonly toast = inject(ToastService);
  private readonly fb = inject(FormBuilder);

  protected readonly mesas = signal<Mesa[]>([]);
  protected readonly editando = signal<Mesa | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    numero: [1, [Validators.required, Validators.min(1)]],
    capacidad: [4, [Validators.required, Validators.min(1), Validators.max(20)]],
    zona: ['Salon', Validators.maxLength(30)],
    activa: [true],
  });

  ngOnInit(): void {
    this.cargar();
  }

  protected invalido(campo: string): boolean {
    return esInvalido(this.form, campo);
  }

  protected cargar(): void {
    this.mesaService.listar().subscribe((m) => {
      this.mesas.set(m);
      if (!this.editando()) this.nueva();
    });
  }

  protected nueva(): void {
    this.editando.set(null);
    const siguiente = Math.max(0, ...this.mesas().map((m) => m.numero)) + 1;
    this.form.reset({ numero: siguiente, capacidad: 4, zona: 'Salon', activa: true });
  }

  protected editar(m: Mesa): void {
    this.editando.set(m);
    this.form.reset({ numero: m.numero, capacidad: m.capacidad, zona: m.zona ?? '', activa: m.activa });
  }

  protected guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const datos = this.form.getRawValue();
    const actual = this.editando();
    const peticion = actual ? this.mesaService.actualizar(actual.id, datos) : this.mesaService.crear(datos);
    peticion.subscribe({
      next: () => {
        this.toast.exito(actual ? 'Mesa actualizada' : 'Mesa creada');
        this.editando.set(null);
        this.cargar();
      },
      error: (e) => aplicarErroresServidor(this.form, e),
    });
  }

  protected alternarActiva(m: Mesa): void {
    this.mesaService
      .actualizar(m.id, { numero: m.numero, capacidad: m.capacidad, zona: m.zona, activa: !m.activa })
      .subscribe(() => this.cargar());
  }

  protected eliminar(m: Mesa): void {
    if (!confirm(`¿Eliminar la mesa ${m.numero}?`)) return;
    this.mesaService.eliminar(m.id).subscribe(() => {
      this.toast.exito('Mesa eliminada');
      this.cargar();
    });
  }
}
