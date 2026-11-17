import { Component, computed, input } from '@angular/core';
import { ETIQUETA_ESTADO, EstadoPago, EstadoReserva } from '../../../core/models';

/** Etiqueta de color para el estado de una reserva o pago. */
@Component({
  selector: 'app-estado-badge',
  template: `<span class="estado {{ estado() }}">{{ etiqueta() }}</span>`,
})
export class EstadoBadge {
  readonly estado = input.required<EstadoReserva | EstadoPago>();
  protected readonly etiqueta = computed(() => ETIQUETA_ESTADO[this.estado()]);
}
