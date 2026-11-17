import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import {
  Atencion, EstadoPago, EstadoReserva, Pago, PagoRequest, Reserva, ReservaCreada,
  ReservaDetalle, ReservaRequest, ReservaStats,
} from '../models';

@Injectable({ providedIn: 'root' })
export class ReservaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/reservas`;

  crear(datos: ReservaRequest): Observable<ReservaCreada> {
    return this.http.post<ReservaCreada>(this.url, datos);
  }

  listar(q = ''): Observable<Reserva[]> {
    const params = q.trim() ? new HttpParams().set('q', q.trim()) : undefined;
    return this.http.get<Reserva[]>(this.url, { params });
  }

  stats(): Observable<ReservaStats> {
    return this.http.get<ReservaStats>(`${this.url}/stats`);
  }

  detalle(id: number): Observable<ReservaDetalle> {
    return this.http.get<ReservaDetalle>(`${this.url}/${id}/detalle`);
  }

  cambiarEstado(id: number, estado: EstadoReserva): Observable<Reserva> {
    return this.http.patch<Reserva>(`${this.url}/${id}/estado`, { estado });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  asignarMesas(id: number, mesaIds: number[]): Observable<ReservaDetalle> {
    return this.http.put<ReservaDetalle>(`${this.url}/${id}/mesas`, { mesaIds });
  }

  registrarPago(id: number, datos: PagoRequest): Observable<Pago> {
    return this.http.post<Pago>(`${this.url}/${id}/pago`, datos);
  }

  cambiarEstadoPago(id: number, estado: EstadoPago): Observable<Pago> {
    return this.http.patch<Pago>(`${this.url}/${id}/pago/estado`, { estado });
  }

  registrarLlegada(id: number, personasAtendidas: number | null, observaciones: string | null): Observable<Atencion> {
    return this.http.post<Atencion>(`${this.url}/${id}/atencion`, { personasAtendidas, observaciones });
  }

  registrarSalida(id: number): Observable<Atencion> {
    return this.http.patch<Atencion>(`${this.url}/${id}/atencion/salida`, {});
  }
}
