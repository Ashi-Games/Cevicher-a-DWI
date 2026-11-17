import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { Opinion } from '../../../core/models';
import { AuthService } from '../../../core/services/auth.service';
import { OpinionService } from '../../../core/services/opinion.service';
import { ToastService } from '../../../core/services/toast.service';

@Component({
  selector: 'app-opiniones',
  imports: [DatePipe],
  templateUrl: './opiniones.html',
})
export class Opiniones implements OnInit {
  private readonly opinionService = inject(OpinionService);
  private readonly toast = inject(ToastService);
  protected readonly auth = inject(AuthService);

  protected readonly opiniones = signal<Opinion[]>([]);
  protected readonly cargando = signal(true);

  ngOnInit(): void {
    this.buscar('');
  }

  protected buscar(q: string): void {
    this.cargando.set(true);
    this.opinionService.listar(q).subscribe({
      next: (lista) => {
        this.opiniones.set(lista);
        this.cargando.set(false);
      },
      error: () => this.cargando.set(false),
    });
  }

  protected eliminar(o: Opinion): void {
    if (!confirm(`¿Eliminar la opinión de ${o.nombres}?`)) return;
    this.opinionService.eliminar(o.id).subscribe(() => {
      this.opiniones.update((l) => l.filter((x) => x.id !== o.id));
      this.toast.exito('Opinión eliminada');
    });
  }
}
