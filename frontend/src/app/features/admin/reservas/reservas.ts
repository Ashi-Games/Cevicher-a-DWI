import { DatePipe, TitleCasePipe } from '@angular/common';
import { Component, DestroyRef, OnInit, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Subject, debounceTime, distinctUntilChanged, switchMap } from 'rxjs';
import { EstadoReserva, Reserva } from '../../../core/models';
import { AuthService } from '../../../core/services/auth.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { ToastService } from '../../../core/services/toast.service';
import { EstadoBadge } from '../../../shared/components/estado-badge/estado-badge';

@Component({
  selector: 'app-reservas',
  imports: [EstadoBadge, RouterLink, DatePipe, TitleCasePipe],
  templateUrl: './reservas.html',
})
export class Reservas implements OnInit {
  private readonly reservaService = inject(ReservaService);
  private readonly toast = inject(ToastService);
  private readonly destroyRef = inject(DestroyRef);
  protected readonly auth = inject(AuthService);

  protected readonly filtros: (EstadoReserva | 'TODAS')[] = ['TODAS', 'PENDIENTE', 'CONFIRMADO', 'RECHAZADO', 'EXPIRADO'];
  protected readonly filtro = signal<EstadoReserva | 'TODAS'>('TODAS');
  protected readonly reservas = signal<Reserva[]>([]);
  protected readonly cargando = signal(true);
  private readonly busqueda$ = new Subject<string>();

  protected readonly visibles = computed(() =>
    this.filtro() === 'TODAS' ? this.reservas() : this.reservas().filter((r) => r.estado === this.filtro()),
  );

  ngOnInit(): void {
    this.busqueda$
      .pipe(
        debounceTime(350),
        distinctUntilChanged(),
        switchMap((q) => {
          this.cargando.set(true);
          return this.reservaService.listar(q);
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (lista) => {
          this.reservas.set(lista);
          this.cargando.set(false);
        },
        error: () => this.cargando.set(false),
      });
    this.recargar();
  }

  protected buscar(texto: string): void {
    this.busqueda$.next(texto);
  }

  protected recargar(q = ''): void {
    this.cargando.set(true);
    this.reservaService.listar(q).subscribe({
      next: (lista) => {
        this.reservas.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  protected cambiarEstado(r: Reserva, estado: EstadoReserva): void {
    this.reservaService.cambiarEstado(r.id, estado).subscribe((act) => {
      this.reservas.update((lista) => lista.map((x) => (x.id === act.id ? act : x)));
      this.toast.exito(`Reserva #${r.id} marcada como ${estado.toLowerCase()}`);
    });
  }

  protected eliminar(r: Reserva): void {
    if (!confirm(`¿Eliminar la reserva #${r.id} de ${r.nombres}?`)) return;
    this.reservaService.eliminar(r.id).subscribe(() => {
      this.reservas.update((lista) => lista.filter((x) => x.id !== r.id));
      this.toast.exito('Reserva eliminada');
    });
  }

  protected contar(estado: EstadoReserva | 'TODAS'): number {
    return estado === 'TODAS' ? this.reservas().length : this.reservas().filter((r) => r.estado === estado).length;
  }
}
