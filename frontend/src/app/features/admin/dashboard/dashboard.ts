import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { Reserva, ReservaStats } from '../../../core/models';
import { AuthService } from '../../../core/services/auth.service';
import { ReservaService } from '../../../core/services/reserva.service';
import { EstadoBadge } from '../../../shared/components/estado-badge/estado-badge';
import { StatCard } from '../../../shared/components/stat-card/stat-card';
import { fechaISO } from '../../../shared/utils/formularios';

@Component({
  selector: 'app-dashboard',
  imports: [StatCard, EstadoBadge, RouterLink],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private readonly reservaService = inject(ReservaService);
  protected readonly auth = inject(AuthService);

  protected readonly cargando = signal(true);
  protected readonly stats = signal<ReservaStats | null>(null);
  protected readonly reservas = signal<Reserva[]>([]);

  protected readonly hoy = computed(() => {
    const hoy = fechaISO();
    return this.reservas()
      .filter((r) => r.fecha === hoy && (r.estado === 'CONFIRMADO' || r.estado === 'PENDIENTE'))
      .sort((a, b) => a.hora.localeCompare(b.hora));
  });
  protected readonly pendientes = computed(() => this.reservas().filter((r) => r.estado === 'PENDIENTE').slice(0, 5));

  ngOnInit(): void {
    forkJoin({ stats: this.reservaService.stats(), reservas: this.reservaService.listar() }).subscribe({
      next: ({ stats, reservas }) => {
        this.stats.set(stats);
        this.reservas.set(reservas);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }
}
