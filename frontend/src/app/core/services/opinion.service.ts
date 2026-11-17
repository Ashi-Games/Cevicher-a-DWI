import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Opinion, OpinionRequest } from '../models';

@Injectable({ providedIn: 'root' })
export class OpinionService {
  private readonly http = inject(HttpClient);
  private readonly url = `${environment.apiUrl}/opiniones`;

  crear(datos: OpinionRequest): Observable<Opinion> {
    return this.http.post<Opinion>(this.url, datos);
  }

  listar(q = ''): Observable<Opinion[]> {
    const params = q.trim() ? new HttpParams().set('q', q.trim()) : undefined;
    return this.http.get<Opinion[]>(this.url, { params });
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
