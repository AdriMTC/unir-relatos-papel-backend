# RelatosPapel - Flujo de uso de Token (Phantom Token)

Esta guia explica el flujo real de autenticacion/autorizacion del proyecto usando **token opaco** hacia el cliente y **JWT interno** dentro de la plataforma.

## Resumen corto

1. El cliente hace login en `ms-users`.
2. `ms-users` devuelve un token opaco (`rp_opaque_...`).
3. El cliente envia ese token opaco al gateway en `Authorization: Bearer ...`.
4. El gateway busca el token opaco en Redis.
5. Si existe, el gateway lo intercambia por el JWT interno y reenvia la peticion.
6. Si no existe (expirado o logout), responde `401`.

---

## Endpoints clave

- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout`
- Cualquier endpoint protegido via gateway, por ejemplo:
  - `POST /api/v1/books`
  - `POST /api/v1/orders`

---

## Flujo detallado

## 1) Login (creacion de token opaco)

### Request

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "lector@unir.com",
  "password": "password123"
}
```

### Que pasa internamente

- `AuthService` valida credenciales.
- `TokenService` genera:
  - un JWT interno (con `sub`, `username`, `roles`, `exp`),
  - un token opaco `rp_opaque_<random>`.
- Guarda en Redis: `opaqueToken -> internalJwt` con TTL (`security.jwt.ttl-seconds`, por defecto 300s).

### Response

```json
{
  "accessToken": "rp_opaque_xxxxxxxxxxxxxxxxx",
  "tokenType": "Bearer",
  "expiresIn": 300
}
```

> El cliente **nunca** recibe el JWT interno, solo el token opaco.

---

## 2) Uso del token en peticiones al gateway

### Header que debe enviar el cliente

```http
Authorization: Bearer rp_opaque_xxxxxxxxxxxxxxxxx
```

### Comportamiento del `PhantomTokenFilter` en gateway

- Deja pasar sin token:
  - rutas `/auth/**`
  - `GET` sobre rutas que contienen `/books` (catalogo publico)
- Para el resto:
  1. lee `Authorization` (case-insensitive),
  2. valida prefijo `Bearer `,
  3. extrae token opaco,
  4. consulta Redis,
  5. si existe, reemplaza header por `Bearer <jwtInterno>` y continua,
  6. si no existe, devuelve `401 Unauthorized`.

---

## 3) Logout (revocacion)

### Request

```http
POST /api/v1/auth/logout
Authorization: Bearer rp_opaque_xxxxxxxxxxxxxxxxx
```

### Resultado

- `AuthService` llama `TokenService.revokeOpaqueToken(...)`.
- Se elimina la clave en Redis.
- Respuesta: `204 No Content`.

---

## 4) Token expirado o revocado

Si el cliente reutiliza el token opaco despues de expirar o tras logout:

- El gateway no encuentra la clave en Redis.
- Respuesta: `401 Unauthorized`.

---

## Diagrama rapido

```text
Cliente
  -> POST /api/v1/auth/login (email/password)
ms-users
  -> genera JWT interno
  -> guarda en Redis: opaque -> jwt (TTL)
  -> responde opaque token al cliente
Cliente
  -> Authorization: Bearer <opaque> a /api/v1/... via gateway
Gateway (PhantomTokenFilter)
  -> busca <opaque> en Redis
  -> si existe: reemplaza por Bearer <jwt interno> y reenvia
  -> si no existe: 401
Cliente
  -> POST /api/v1/auth/logout con <opaque>
ms-users
  -> elimina <opaque> de Redis
Cliente
  -> reusa <opaque> => 401
```

---

## Prueba rapida en Postman

1. Ejecuta `Login lector` o `Login admin`.
2. Verifica que `accessToken` empieza por `rp_opaque_`.
3. Usa ese token en una ruta protegida (`POST /api/v1/orders` o `POST /api/v1/books`).
4. Ejecuta `Logout`.
5. Repite la misma peticion con el mismo token: debe devolver `401`.

