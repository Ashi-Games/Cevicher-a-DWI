import { DatePipe } from '@angular/common';
import { Component, input, output } from '@angular/core';
import { ReservaCreada } from '../../../core/models';

/** Componente hijo: muestra la reserva creada y avisa al padre cuando se quiere otra (@Output). */
@Component({
  selector: 'app-resumen-reserva',
  imports: [DatePipe],
  template: `
    <div class="card card-soft p-4 p-md-5 text-center page-enter">
      <i class="bi bi-check-circle-fill text-success display-4"></i>
      <h2 class="h3 mt-3">¡Reserva registrada!</h2>
      <p class="text-secondary">
        Tu reserva <strong>#{{ datos().reserva.id }}</strong> está <strong>pendiente</strong>.
        Envía el adelanto de <strong>S/ 20</strong> por WhatsApp en los próximos 30 minutos para confirmarla.
      </p>
      <ul class="list-group list-group-flush text-start my-3">
        <li class="list-group-item d-flex justify-content-between"><span>Nombre</span>
          <strong>{{ datos().reserva.nombres }} {{ datos().reserva.apellidos }}</strong></li>
        <li class="list-group-item d-flex justify-content-between"><span>Fecha</span>
          <strong>{{ datos().reserva.fecha + 'T00:00:00' | date: 'EEEE d MMM y' }}</strong></li>
        <li class="list-group-item d-flex justify-content-between"><span>Hora</span>
          <strong>{{ datos().reserva.hora.slice(0, 5) }}</strong></li>
        <li class="list-group-item d-flex justify-content-between"><span>Personas</span>
          <strong>{{ datos().reserva.personas }}</strong></li>
      </ul>
      <div class="d-grid gap-2 d-sm-flex justify-content-sm-center">
        <a class="btn btn-success btn-lg" [href]="datos().whatsappUrl" target="_blank" rel="noopener">
          <i class="bi bi-whatsapp me-2"></i>Confirmar por WhatsApp
        </a>
        <button type="button" class="btn btn-outline-secondary btn-lg" (click)="nueva.emit()">Hacer otra reserva</button>
      </div>
    </div>
  `,
})
export class ResumenReserva {
  readonly datos = input.required<ReservaCreada>();
  readonly nueva = output<void>();
}
