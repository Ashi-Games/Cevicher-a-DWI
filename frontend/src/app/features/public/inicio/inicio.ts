import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-inicio',
  imports: [RouterLink],
  templateUrl: './inicio.html',
  styleUrl: './inicio.scss',
})
export class Inicio {
  protected readonly beneficios = [
    { icono: 'bi-calendar2-check', titulo: 'Reserva en linea', texto: 'Elige fecha, hora y numero de personas en menos de un minuto.' },
    { icono: 'bi-whatsapp', titulo: 'Confirmacion por WhatsApp', texto: 'Coordina el adelanto de S/ 20 y tu mesa queda asegurada.' },
    { icono: 'bi-basket', titulo: 'Pesca del dia', texto: 'Trabajamos con producto fresco que llega cada manana.' },
  ];

  protected readonly pasos = [
    'Completa el formulario de reserva.',
    'Envia el adelanto por Yape, Plin o transferencia en 30 minutos.',
    'Recibe la confirmacion y te esperamos con tu mesa lista.',
  ];
}
