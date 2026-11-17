import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthToken, LoginRequest, Rol, Sesion } from '../models';

const CLAVE = 'rm_sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly sesion = signal<Sesion | null>(this.leer());
  readonly logueado = computed(() => this.sesion() !== null);
  readonly esAdmin = computed(() => this.sesion()?.rol === 'ADMIN');

  login(datos: LoginRequest): Observable<AuthToken> {
    return this.http.post<AuthToken>(`${environment.apiUrl}/auth/login`, datos).pipe(
      tap((r) => {
        const sesion: Sesion = { token: r.token, usuario: r.usuario, rol: r.rol, expiraEn: r.expiraEn };
        localStorage.setItem(CLAVE, JSON.stringify(sesion));
        this.sesion.set(sesion);
      }),
    );
  }

  logout(): void {
    this.limpiar();
    this.router.navigate(['/admin/login']);
  }

  /** Token vigente o null si no hay sesion o ya expiro. */
  token(): string | null {
    const s = this.sesion();
    if (!s) return null;
    if (new Date(s.expiraEn).getTime() <= Date.now()) {
      this.limpiar();
      return null;
    }
    return s.token;
  }

  tieneRol(roles: Rol[]): boolean {
    const rol = this.sesion()?.rol;
    return !!rol && roles.includes(rol);
  }

  private limpiar(): void {
    localStorage.removeItem(CLAVE);
    this.sesion.set(null);
  }

  private leer(): Sesion | null {
    try {
      const texto = localStorage.getItem(CLAVE);
      return texto ? (JSON.parse(texto) as Sesion) : null;
    } catch {
      return null;
    }
  }
}
