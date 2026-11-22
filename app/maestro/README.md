# Recorridos de Maestro · Junta Zaror

Flujos para grabar el video de la app. Usan los **textos visibles** de la UI (sin testTags).
`appId`: `com.example.appjuntavecinos`. Contraseña de todos los usuarios de prueba: `1234`.

## Antes de grabar: reiniciar los datos

Los flujos crean datos (una reserva, una aprobación, un gasto). Si se repiten sin reiniciar,
el flujo 02 choca con la reserva que ya existe. Antes de cada sesión de grabación:

```bash
cd "C:\Users\vcort\Desktop\BACKEND APP MOVIL\appzaror"
docker compose down -v && docker compose up -d --build
```

Espera **1 minuto** a que todos los servicios queden arriba (Eureka + gateway) antes de correr los flujos.

También: emulador encendido, app instalada (`./gradlew installDebug`) y `maestro` instalado.

## Demo completa en una sola ejecución

`maestro test maestro/00_demo_completa.yaml` recorre los 4 actos seguidos (Camila solicita →
Directiva aprueba → Camila ve los métodos y el resumen de Webpay sin pagar → Tesorera registra
un gasto), con cierre de sesión entre roles (`comun/logout.yaml`). Necesita los datos recién
reiniciados. En el emulador dura **~160 s** de pantalla (cada toque de Maestro toma ~2 s);
para un video de ~65 s, acelerar la grabación ×2,5 al editar.

## Flujos (en este orden)

| # | Comando | Qué hace |
|---|---------|----------|
| 1 | `maestro test maestro/01_login_inicio_vecino.yaml` | Login Camila → Inicio → Finanzas → "Rendición octubre" |
| 2 | `maestro test maestro/02_solicitar_arriendo.yaml` | (sigue con la sesión de Camila) Solicitar arriendo → Patio y quincho → hoy+5 días → 15:00 a 19:00 → "Asado vecinal" → 25 personas → Enviar → Reservas |
| 3 | `maestro test maestro/03_directiva_aprueba.yaml` | Login Directiva → Aprobar "Asado vecinal" con observación → Calendario → Patio y quincho |
| 4 | `maestro test maestro/04_metodos_pago.yaml` | Login Camila → Reservas → Pagar → Transferencia → Copiar datos → Entendido → Pagar → Webpay → resumen (no toca "Ir a pagar") |
| 5 | `maestro test maestro/05_tesorero.yaml` | Login Tesorera → Gasto $12.990 Materiales "Ampolletas para la sede" → Guardar → Movimientos → Pagos |

El 02 depende del 01 (no borra la sesión) y el 03 aprueba la reserva que crea el 02.

Para grabar directo a MP4: `maestro record --local maestro/01_login_inicio_vecino.yaml`
(`--local` procesa el video en el PC; puede tardar un poco y agregar un marco de Maestro).

## Avisos

- **FLAG_SECURE solo en release.** `PagoWebpayScreen` protege la pantalla de pago contra capturas
  únicamente en la versión release. En debug (la que se graba) el formulario de Transbank se ve.
  Si se graba una build release, esa pantalla sale en negro.
- **"Ir a pagar":** abre Webpay directo, sin pedir huella ni PIN.
- **Textos sin tildes ni ñ:** `inputText` de Maestro en Android puede perder los caracteres no
  ASCII, por eso los flujos escriben "Asado vecinal", "Aprobada, dejar el quincho limpio" y
  "Ampolletas para la sede". El backend guarda bien UTF-8 (verificado con "Cumpleaños ñandú").
- **"Copiar datos":** Android 13+ muestra unos segundos su vista previa del portapapeles encima
  de "Entendido". El flujo 04 espera a que se vaya antes de tocar "Entendido".
