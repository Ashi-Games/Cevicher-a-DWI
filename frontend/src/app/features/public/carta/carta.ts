import { Component, computed, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Plato, PlatoCard } from './plato-card';

@Component({
  selector: 'app-carta',
  imports: [PlatoCard, RouterLink],
  templateUrl: './carta.html',
})
export class Carta {
  protected readonly platos: Plato[] = [
    { categoria: 'Ceviches', nombre: 'Ceviche clásico', descripcion: 'Pescado del día, limon, ají limo, cebolla, camote y choclo.', precio: 32, destacado: true },
    { categoria: 'Ceviches', nombre: 'Ceviche mixto', descripcion: 'Pescado, pulpo, calamar y langostinos en leche de tigre.', precio: 38 },
    { categoria: 'Ceviches', nombre: 'Tiradito al ají amarillo', descripcion: 'Láminas de pescado en crema de ají amarillo.', precio: 30 },
    { categoria: 'Entradas', nombre: 'Leche de tigre', descripcion: 'El clásico levantamuertos con chicharron de calamar.', precio: 18, destacado: true },
    { categoria: 'Entradas', nombre: 'Causa limeña', descripcion: 'Papa amarilla, palta y relleno de pollo o atun.', precio: 22 },
    { categoria: 'Entradas', nombre: 'Choritos a la chalaca', descripcion: 'Choros con sarza de cebolla, tomate y choclo.', precio: 24 },
    { categoria: 'Fondos', nombre: 'Chicharron de pescado', descripcion: 'Trozos crocantes con yuca frita y salsa criolla.', precio: 35 },
    { categoria: 'Fondos', nombre: 'Arroz con mariscos', descripcion: 'Arroz al ají panca con mariscos frescos.', precio: 36, destacado: true },
    { categoria: 'Fondos', nombre: 'Jalea mixta', descripcion: 'Pescado y mariscos fritos con yuca y zarza.', precio: 45 },
    { categoria: 'Fondos', nombre: 'Parihuela', descripcion: 'Sopa concentrada de pescado y mariscos.', precio: 40 },
    { categoria: 'Bebidas', nombre: 'Chicha morada (jarra)', descripcion: 'Preparada en casa con maíz morado y frutas.', precio: 15 },
    { categoria: 'Bebidas', nombre: 'Limonada frozen (jarra)', descripcion: 'Limón fresco, hielo y un toque de hierbabuena.', precio: 16 },
  ];

  protected readonly categorias = ['Todos', ...new Set(this.platos.map((p) => p.categoria))];
  protected readonly categoria = signal('Todos');
  protected readonly visibles = computed(() =>
    this.categoria() === 'Todos' ? this.platos : this.platos.filter((p) => p.categoria === this.categoria()),
  );
}
