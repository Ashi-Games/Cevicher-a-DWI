import { Routes } from '@angular/router';
import { authGuard, invitadoGuard } from './core/guards/auth.guard';
import { PublicLayout } from './features/public/public-layout/public-layout';
import { Inicio } from './features/public/inicio/inicio';

export const routes: Routes = [
  // ----- Sitio publico -----
  {
    path: '',
    component: PublicLayout,
    children: [
      { path: '', component: Inicio, title: 'Rinconcito Marino | Cevicheria' },
      {
        path: 'carta',
        loadComponent: () => import('./features/public/carta/carta').then((m) => m.Carta),
        title: 'Carta | Rinconcito Marino',
      },
      {
        path: 'reservar',
        loadComponent: () => import('./features/public/reservar/reservar').then((m) => m.Reservar),
        title: 'Reservar | Rinconcito Marino',
      },
      {
        path: 'opinar',
        loadComponent: () => import('./features/public/opinar/opinar').then((m) => m.Opinar),
        title: 'Tu opinion | Rinconcito Marino',
      },
    ],
  },

  // ----- Panel de administracion -----
  {
    path: 'admin/login',
    canActivate: [invitadoGuard],
    loadComponent: () => import('./features/admin/login/login').then((m) => m.Login),
    title: 'Ingresar | Panel',
  },
  {
    path: 'admin',
    canActivate: [authGuard],
    loadComponent: () => import('./features/admin/admin-layout/admin-layout').then((m) => m.AdminLayout),
    children: [
      {
        path: '',
        loadComponent: () => import('./features/admin/dashboard/dashboard').then((m) => m.Dashboard),
        title: 'Resumen | Panel',
      },
      {
        path: 'reservas',
        loadComponent: () => import('./features/admin/reservas/reservas').then((m) => m.Reservas),
        title: 'Reservas | Panel',
      },
      {
        path: 'reservas/:id',
        loadComponent: () =>
          import('./features/admin/reserva-detalle/reserva-detalle').then((m) => m.ReservaDetalle),
        title: 'Detalle de reserva | Panel',
      },
      {
        path: 'mesas',
        canActivate: [authGuard],
        data: { roles: ['ADMIN'] },
        loadComponent: () => import('./features/admin/mesas/mesas').then((m) => m.Mesas),
        title: 'Mesas | Panel',
      },
      {
        path: 'opiniones',
        loadComponent: () => import('./features/admin/opiniones/opiniones').then((m) => m.Opiniones),
        title: 'Opiniones | Panel',
      },
    ],
  },
  { path: '**', redirectTo: '' },
];
