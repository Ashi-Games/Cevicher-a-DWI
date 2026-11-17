import { Injectable, signal } from '@angular/core';

export type TipoToast = 'success' | 'danger' | 'warning' | 'info';

export interface Toast {
  id: number;
  mensaje: string;
  tipo: TipoToast;
}

/** Notificaciones flotantes compartidas por toda la aplicacion. */
@Injectable({ providedIn: 'root' })
export class ToastService {
  private siguienteId = 1;
  readonly toasts = signal<Toast[]>([]);

  mostrar(mensaje: string, tipo: TipoToast = 'info', duracionMs = 4000): void {
    const id = this.siguienteId++;
    this.toasts.update((lista) => [...lista, { id, mensaje, tipo }]);
    setTimeout(() => this.cerrar(id), duracionMs);
  }

  exito(mensaje: string): void {
    this.mostrar(mensaje, 'success');
  }

  error(mensaje: string): void {
    this.mostrar(mensaje, 'danger', 6000);
  }

  cerrar(id: number): void {
    this.toasts.update((lista) => lista.filter((t) => t.id !== id));
  }
}
