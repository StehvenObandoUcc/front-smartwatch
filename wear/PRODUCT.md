# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

> El reloj de **Recuerda**. Hereda la verdad del producto de `../PRODUCT.md` (usuarios, voz,
> principios, accesibilidad); aquí solo lo propio del reloj.

## Users

El paciente: adulto mayor o de mediana edad, con poca alfabetización digital. Mira el reloj de un
vistazo, a menudo con la vista cansada y la letra grande del sistema; responde a la alarma con un
toque.

## Product Purpose

Que suene cada dosis a su hora y que registrarla sea un solo toque, con o sin conexión.

## Operating Context

- Wear OS independiente: habla directo con el backend por Wi-Fi, LTE o Bluetooth; sin app de
  teléfono ni Data Layer.
- Dos formas de uso: vinculado al panel web (el plan llega del backend) o solo en el reloj (los
  medicamentos se crean en el reloj y sus dosis no se suben).
- Páginas que se deslizan: Próxima toma, Hoy, Adherencia (solo vinculado) y Reloj (vincular,
  desvincular, medicamentos locales). La alerta abre a pantalla completa.

## Capabilities and Constraints

- Wear OS 4 o superior (API 33); pantallas redondas y cuadradas; corona giratoria.
- Componentes propios (tema `Bw`) sobre el andamiaje de Wear Compose; sin componentes visuales de
  Material 3.
- Alarmas con `setAlarmClock()`; requiere los permisos de avisos y de alarmas exactas.

## Accessibility & Inclusion

Nada por debajo de 14 sp, el dato principal a 40 sp, botones de 48 dp o más, compatibilidad con
TalkBack y con la letra grande del sistema sin cortes.
