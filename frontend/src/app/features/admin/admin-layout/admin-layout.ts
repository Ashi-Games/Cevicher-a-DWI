import { Component, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-admin-layout',
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './admin-layout.html',
  styleUrl: './admin-layout.scss',
})
export class AdminLayout {
  protected readonly auth = inject(AuthService);
  protected readonly menuAbierto = signal(false);

  protected readonly enlaces = [
    { ruta: '/admin', icono: 'bi-speedometer2', texto: 'Resumen', exacto: true, soloAdmin: false },
    { ruta: '/admin/reservas', icono: 'bi-calendar-week', texto: 'Reservas', exacto: false, soloAdmin: false },
    { ruta: '/admin/mesas', icono: 'bi-grid-3x3-gap', texto: 'Mesas', exacto: false, soloAdmin: true },
    { ruta: '/admin/opiniones', icono: 'bi-chat-square-text', texto: 'Opiniones', exacto: false, soloAdmin: false },
  ];
}
