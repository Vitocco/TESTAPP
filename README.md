# Junta Zaror

App de la **Junta de Vecinos Población Zaror** (DUOC UC · DSY1105): reservas de sedes comunitarias
y transparencia financiera para los vecinos.

## Estructura del repositorio

```
JuntaZaror/
├── app/        App Android (Kotlin + Retrofit) — proyecto de Android Studio
└── backend/    Backend de microservicios (Java 21 + Spring Boot + Spring Cloud + MySQL)
```

| Carpeta | Qué contiene | Cómo empezar |
|---------|--------------|--------------|
| [app/](app/) | Aplicación Android | Abrir la carpeta `app/` en Android Studio. Ver [app/README.md](app/README.md) |
| [backend/](backend/) | Eureka, Gateway y 4 microservicios (auth, usuario, reserva, finanza) | Ver **[backend/README.md](backend/README.md)** |

## Conexión app ↔ backend

- La app consume el backend **solo a través del gateway**: `http://10.0.2.2:8080/api/` desde el emulador Android.
- Referencia de endpoints (cuerpos y respuestas): [backend/docs/API.md](backend/docs/API.md).
- Usuarios de prueba (contraseña `1234`): `directiva@zaror.cl`, `tesorero@zaror.cl`, `camila@zaror.cl`, `luis@zaror.cl`.

## Levantar el backend rápido

```bash
cd backend
cp .env.example .env        # y completar los valores (ver backend/README.md, sección 3)
docker compose up -d --build
```

> Los secretos van solo en `backend/.env`, que **no** se sube al repositorio (está en `.gitignore`).
> Webpay funciona en el ambiente de integración de Transbank: sin transacciones reales.
