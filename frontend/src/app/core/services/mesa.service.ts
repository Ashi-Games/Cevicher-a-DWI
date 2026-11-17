import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Mesa, MesaRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class MesaService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/mesas`;

  listar(): Observable<Mesa[]> {
    return this.http.get<Mesa[]>(this.url);
  }

  crear(datos: MesaRequest): Observable<Mesa> {
    return this.http.post<Mesa>(this.url, datos);
  }

  actualizar(id: number, datos: MesaRequest): Observable<Mesa> {
    return this.http.put<Mesa>(`${this.url}/${id}`, datos);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
