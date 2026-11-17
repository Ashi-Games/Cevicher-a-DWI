import { Component, input } from '@angular/core';

/** Tarjeta de indicador para el dashboard. */
@Component({
  selector: 'app-stat-card',
  template: `
    <div class="card card-soft h-100">
      <div class="card-body d-flex align-items-center gap-3">
        <div class="icono bg-{{ color() }}-subtle text-{{ color() }}">
          <i class="bi {{ icono() }}"></i>
        </div>
        <div>
          <div class="text-secondary small">{{ titulo() }}</div>
          <div class="fs-3 fw-bold">{{ valor() }}</div>
        </div>
      </div>
    </div>
  `,
  styles: `
    .icono { width: 3rem; height: 3rem; border-radius: .9rem; display: grid; place-items: center; font-size: 1.4rem; }
  `,
})
export class StatCard {
  readonly titulo = input.required<string>();
  readonly valor = input.required<number | string>();
  readonly icono = input('bi-graph-up');
  readonly color = input<'primary' | 'success' | 'warning' | 'info' | 'danger'>('primary');
}
