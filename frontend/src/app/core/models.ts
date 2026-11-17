export type EstadoReserva = 'PENDIENTE' | 'CONFIRMADO' | 'RECHAZADO' | 'EXPIRADO';
export type Rol = 'ADMIN' | 'EMPLEADO';
export type MetodoPago = 'YAPE' | 'PLIN' | 'TRANSFERENCIA' | 'EFECTIVO';
export type EstadoPago = 'PENDIENTE' | 'PAGADO' | 'RECHAZADO';

export interface ReservaRequest {
  nombres: string;
  apellidos: string;
  dni: string;
  edad: number;
  email: string;
  telefono: string;
  personas: number;
  fecha: string; // yyyy-MM-dd
  hora: string; // HH:mm
}

export interface Reserva extends ReservaRequest {
  id: number;
  estado: EstadoReserva;
  fechaRegistro: string;
}

export interface ReservaCreada {
  reserva: Reserva;
  whatsappUrl: string;
}

export interface ReservaStats {
  pendientes: number;
  confirmadasHoy: number;
  opiniones: number;
}

export interface Mesa {
  id: number;
  numero: number;
  capacidad: number;
  zona: string | null;
  activa: boolean;
}

export type MesaRequest = Omit<Mesa, 'id'>;

export interface Pago {
  id: number;
  monto: number;
  metodo: MetodoPago;
  estado: EstadoPago;
  referencia: string | null;
  fechaPago: string | null;
}

export interface PagoRequest {
  metodo: MetodoPago;
  monto: number | null;
  referencia: string | null;
}

export interface Atencion {
  id: number;
  atendidoPor: string;
  horaLlegada: string;
  horaSalida: string | null;
  personasAtendidas: number | null;
  observaciones: string | null;
}

export interface Historial {
  estadoAnterior: EstadoReserva | null;
  estadoNuevo: EstadoReserva;
  usuario: string;
  motivo: string | null;
  fechaCambio: string;
}

export interface ReservaDetalle {
  reserva: Reserva;
  mesas: Mesa[];
  pago: Pago | null;
  atencion: Atencion | null;
  historial: Historial[];
}

export interface OpinionRequest {
  nombres: string;
  correo: string;
  comentario: string;
}

export interface Opinion extends OpinionRequest {
  id: number;
  fechaRegistro: string;
}

export interface LoginRequest {
  usuario: string;
  password: string;
}

export interface AuthToken {
  token: string;
  tipo: string;
  usuario: string;
  rol: Rol;
  expiraEn: string;
}

export interface Sesion {
  token: string;
  usuario: string;
  rol: Rol;
  expiraEn: string;
}

/** Respuesta de error del GlobalExceptionHandler del backend. */
export interface ApiError {
  status: number;
  error: string;
  mensaje: string;
  path: string;
  errores: Record<string, string>;
}

export const ETIQUETA_ESTADO: Record<EstadoReserva | EstadoPago, string> = {
  PENDIENTE: 'Pendiente',
  CONFIRMADO: 'Confirmado',
  RECHAZADO: 'Rechazado',
  EXPIRADO: 'Expirado',
  PAGADO: 'Pagado',
};
