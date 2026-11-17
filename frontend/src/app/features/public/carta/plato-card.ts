import { CurrencyPipe } from '@angular/common';
import { Component, input } from '@angular/core';

export interface Plato {
  nombre: string;
  descripcion: string;
  precio: number;
  categoria: string;
  destacado?: boolean;
}

/** Componente hijo: recibe un plato desde la carta (@Input con signals). */
@Component({
  selector: 'app-plato-card',
  imports: [CurrencyPipe],
  template: `
    <div class="card card-soft h-100">
      <div class="card-body d-flex flex-column">
        <div class="d-flex justify-content-between align-items-start gap-2">
          <h6 class="fw-semibold mb-1">{{ plato().nombre }}</h6>
          @if (plato().destacado) {
            <span class="badge bg-warning-subtle text-warning-emphasis">Favorito</span>
          }
        </div>
        <p class="text-secondary small flex-grow-1">{{ plato().descripcion }}</p>
        <span class="fw-bold text-primary fs-5">{{ plato().precio | currency: 'PEN' : 'S/ ' }}</span>
      </div>
    </div>
  `,
})
export class PlatoCard {
  readonly plato = input.required<Plato>();
}
