import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Rol } from '../models';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

/** Protege las rutas del panel. Si la ruta define data.roles, valida tambien el rol. */
export const authGuard: CanActivateFn = (route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!auth.token()) {
    return router.createUrlTree(['/admin/login'], { queryParams: { volver: state.url } });
  }
  const roles = route.data?.['roles'] as Rol[] | undefined;
  if (roles && !auth.tieneRol(roles)) {
    inject(ToastService).error('Esta sección es solo para administradores.');
    return router.createUrlTree(['/admin']);
  }
  return true;
};

/** Evita volver al login si ya hay sesion activa. */
export const invitadoGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  return auth.token() ? inject(Router).createUrlTree(['/admin']) : true;
};
