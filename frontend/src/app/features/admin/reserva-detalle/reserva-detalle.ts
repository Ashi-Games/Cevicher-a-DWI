import { CurrencyPipe, DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable } from 'rxjs';
import { EstadoPago, EstadoReserva, Mesa, MetodoPago, ReservaDetalle as Detalle } from '../../../core/models';
import { MesaService } from '../../../core/services/mesa.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { ToastService } from '../../../core/services/toast.service';
import { EstadoBadge } from '../../../shared/components/estado-badge/estado-badge';

@Component({
  selector: 'app-reserva-detalle',
  imports: [ReactiveFormsModule, RouterLink, DatePipe, CurrencyPipe, EstadoBadge],
  templateUrl: './reserva-detalle.html',
})
export class ReservaDetalle implements OnInit {
  private readonly reservaService = inject(ReservaService);
  private readonly mesaService = inject(MesaService);
  private readonly toast = inject(ToastService);
  private readonly fb = inject(NonNullableFormBuilder);

  /** Parametro :id de la ruta (withComponentInputBinding). */
  readonly id = input.required<string>();

  protected readonly detalle = signal<Detalle | null>(null);
  protected readonly mesas = signal<Mesa[]>([]);
  protected readonly seleccion = signal<Set<number>>(new Set());
  protected readonly ocupado = signal(false);
  protected readonly metodos: MetodoPago[] = ['YAPE', 'PLIN', 'TRANSFERENCIA', 'EFECTIVO'];

  protected readonly capacidadElegida = computed(() =>
    this.mesas().filter((m) => this.seleccion().has(m.id)).reduce((t, m) => t + m.capacidad, 0),
  );
  protected readonly activa = computed(() => {
    const e = this.detalle()?.reserva.estado;
    return e === 'PENDIENTE' || e === 'CONFIRMADO';
  });

  protected readonly pagoForm = this.fb.group({
    metodo: ['YAPE' as MetodoPago, Validators.required],
    monto: [20, [Validators.required, Validators.min(0.01)]],
    referencia: ['', Validators.maxLength(60)],
  });

  protected readonly atencionForm = this.fb.group({
    personasAtendidas: [0, Validators.min(0)],
    observaciones: ['', Validators.maxLength(300)],
  });

  private get reservaId(): number {
    return Number(this.id());
  }

  ngOnInit(): void {
    this.cargar();
    this.mesaService.listar().subscribe((m) => this.mesas.set(m.filter((x) => x.activa)));
  }

  protected cargar(): void {
    this.reservaService.detalle(this.reservaId).subscribe((d) => {
      this.detalle.set(d);
      this.seleccion.set(new Set(d.mesas.map((m) => m.id)));
      this.atencionForm.patchValue({ personasAtendidas: d.reserva.personas });
    });
  }

  protected alternarMesa(id: number): void {
    this.seleccion.update((s) => {
      const nuevo = new Set(s);
      nuevo.has(id) ? nuevo.delete(id) : nuevo.add(id);
      return nuevo;
    });
  }

  protected guardarMesas(): void {
    this.ejecutar(this.reservaService.asignarMesas(this.reservaId, [...this.seleccion()]), 'Mesas asignadas');
  }

  protected cambiarEstado(estado: EstadoReserva): void {
    this.ejecutar(this.reservaService.cambiarEstado(this.reservaId, estado), 'Estado actualizado');
  }

  protected registrarPago(): void {
    if (this.pagoForm.invalid) {
      this.pagoForm.markAllAsTouched();
      return;
    }
    const v = this.pagoForm.getRawValue();
    this.ejecutar(
      this.reservaService.registrarPago(this.reservaId, { metodo: v.metodo, monto: v.monto, referencia: v.referencia || null }),
      'Adelanto registrado',
    );
  }

  protected cambiarEstadoPago(estado: EstadoPago): void {
    const msg = estado === 'PAGADO' ? 'Adelanto pagado: reserva confirmada' : 'Pago rechazado';
    this.ejecutar(this.reservaService.cambiarEstadoPago(this.reservaId, estado), msg);
  }

  protected registrarLlegada(): void {
    const v = this.atencionForm.getRawValue();
    this.ejecutar(
      this.reservaService.registrarLlegada(this.reservaId, v.personasAtendidas, v.observaciones || null),
      'Llegada registrada',
    );
  }

  protected registrarSalida(): void {
    this.ejecutar(this.reservaService.registrarSalida(this.reservaId), 'Salida registrada');
  }

  /** Ejecuta una accion, muestra el aviso y recarga el detalle. */
  private ejecutar(accion: Observable<unknown>, mensaje: string): void {
    this.ocupado.set(true);
    accion.subscribe({
      next: () => {
        this.toast.exito(mensaje);
        this.ocupado.set(false);
        this.cargar();
      },
      error: () => this.ocupado.set(false),
    });
  }
}
