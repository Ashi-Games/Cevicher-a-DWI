import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ApiError } from '../models';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

/** Manejo centralizado de errores HTTP: muestra un aviso y cierra sesion si el token ya no vale. */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const toast = inject(ToastService);
  const auth = inject(AuthService);

  return next(req).pipe(
    catchError((err: unknown) => {
      if (err instanceof HttpErrorResponse) {
        const api = err.error as Partial<ApiError> | null;
        const esLogin = req.url.endsWith('/auth/login');

        if (err.status === 0) {
          toast.error('No se pudo conectar con el servidor. Verifica que el backend este encendido.');
        } else if (err.status === 401 && !esLogin) {
          toast.error('Tu sesion expiro. Vuelve a iniciar sesion.');
          auth.logout();
        } else if (err.status === 403) {
          toast.error('No tienes permiso para realizar esta accion.');
        } else if (err.status === 400 && api?.errores && Object.keys(api.errores).length) {
          toast.error('Revisa los campos marcados en el formulario.');
        } else {
          toast.error(api?.mensaje ?? 'Ocurrio un error inesperado.');
        }
      }
      return throwError(() => err);
    }),
  );
};
