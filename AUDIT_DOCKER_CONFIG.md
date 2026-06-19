# Auditoría de Configuración Docker - Relatos de Papel

## 🔍 Problemas Identificados

### 1. GATEWAY (`gateway/src/main/resources/application.yaml`)

#### Problema 1a: Redis hardcodeado a localhost
- **Línea 50**: `redis.host: localhost`
- **Esperado en docker.yml**: `SPRING_REDIS_HOST=relatos-redis`
- **Impacto**: En Docker, intentará conectarse a `localhost:6379` dentro del contenedor (falla)
- **Solución**: Parametrizar con variable de entorno

#### Problema 1b: Eureka hardcodeado a localhost
- **Línea 57**: `eureka.client.service-url.defaultZone: http://localhost:8761/eureka`
- **Esperado en docker.yml**: `EUREKA_URL=http://eureka-server:8761/eureka/`
- **Impacto**: No encontrará el servidor Eureka en Docker
- **Solución**: Parametrizar con variable

---

### 2. MS-USERS (`ms-users/src/main/resources/application.yml`)

#### Problema 2a: Variable de BD incorrecta en docker.yml
- **application.yml línea 9**: Espera `${DB_URL:...}`
- **docker.yml línea 35**: Define `SPRING_DATASOURCE_URL` (nombre incorrecto)
- **Impacto**: Variable no se injecta, usa hardcoded localhost
- **Solución**: Cambiar docker.yml a `DB_URL`

#### Problema 2b: No hay contenedor MySQL en docker.yml
- **application.yml**: Espera MySQL `3308/db_users`
- **docker.yml**: Define conexión a `dwfs-users-db:3306` (contenedor inexistente)
- **Impacto**: Conexión fallará, no existe ese contenedor
- **Solución**: Eliminar o crear contenedor MySQL

#### Problema 2c: Redis sin contraseña en docker.yml
- **application.yml línea 34**: `REDIS_PASSWORD:` (vacío por defecto)
- **docker.yml**: No define `REDIS_PASSWORD`
- **Impacto**: No hay problema si Redis no requiere autenticación
- **Nota**: Revisar si Redis en `Infra/docker-compose.yml` requiere contraseña

---

### 3. CATALOGUE (`catalogue/src/main/resources/application.yaml`)

#### Problema 3a: Puerto de BD incorrecto
- **application.yaml línea 9**: Hardcoded `localhost:5434`
- **docker.yml línea 49**: Define `catalogue-db:5432` (puerto interno correcto)
- **Impacto**: Por defecto intenta conectar a puerto externo, en Docker no funciona
- **Solución**: Parametrizar o cambiar a `5432`

#### Problema 3b: Redis no parametrizado
- **docker.yml línea 50**: Define `SPRING_REDIS_HOST=relatos-redis`
- **application.yaml**: No existe `spring.data.redis.host` (está hardcoded en gateway, no en éste)
- **Impacto**: Menor (catalogue usa PostgreSQL, no Redis para datos)
- **Nota**: Revisar si catalogue necesita Redis

---

### 4. ORDERS (`orders/src/main/resources/application.yaml`)

#### Problema 4a: Puerto de BD incorrecto
- **application.yaml línea 10**: Hardcoded `localhost:5435`
- **docker.yml línea 64**: Define `orders-db:5432`
- **Impacto**: Igual que catalogue, intenta puerto externo
- **Solución**: Parametrizar o cambiar a `5432`

#### Problema 4b: RabbitMQ no parametrizado
- **docker.yml línea 65**: Define `SPRING_RABBITMQ_HOST=relatos-rabbitmq`
- **application.yaml linea 25**: Espera variable `${RABBITMQ_HOST:localhost}`
- **Impacto**: ✅ Está bien, solo falta la variable en entorno
- **Solución**: Agregar variable en docker.yml (ya está definida, revisar si se pasa)

---

### 5. MS-COMMS (`ms-comms/src/main/resources/application.yaml`)

#### Problema 5a: Credenciales Gmail hardcodeadas (CRÍTICO)
- **Línea 19**: `username: ${GMAIL_USERNAME:hguajardoc@gmail.com}`
- **Línea 20**: `password: ${GMAIL_APP_PASSWORD:yxyd jyoc ssbw mlwv}`
- **Impacto**: Credenciales en código fuente (INSEGURO)
- **Solución**: Mover a variables de entorno SIN defaults en código

#### Problema 5b: Gemini API key vacía
- **Línea 37**: `key: ${GEMINI_API_KEY:}`
- **Impacto**: No funcionará sin key
- **Solución**: Requerir variable de entorno, sin default vacío

---

## 📋 Resumen por Severidad

| Severidad | Cantidad | Componentes |
|-----------|----------|-------------|
| 🔴 CRÍTICO | 2 | ms-comms (credenciales), gateway (conectividad) |
| 🟠 ALTO | 3 | ms-users (BD fallará), catalogue (BD fallará), orders (BD fallará) |
| 🟡 MEDIO | 2 | gateway (Eureka), ms-users (MySQL inexistente) |
| 🟢 BAJO | 2 | Configuraciones menores |

---

## ✅ Correcciones Necesarias

### 1. gateway/src/main/resources/application.yaml
- Cambiar `redis.host: localhost` a usar variable de entorno
- Cambiar Eureka URL a variable

### 2. ms-users/src/main/resources/application.yml
- Sin cambios (ya está bien parametrizado)

### 3. docker.yml
- Cambiar `SPRING_DATASOURCE_URL` a `DB_URL` (línea 35)
- Agregar variables faltantes en ms-users (REDIS_PASSWORD)
- Opción A: Agregar contenedor MySQL para ms-users
- Opción B: Eliminar la variable de ms-users si no lo necesita

### 4. catalogue/src/main/resources/application.yaml
- Cambiar puerto 5434 a parametrizable o a 5432

### 5. orders/src/main/resources/application.yaml
- Cambiar puerto 5435 a parametrizable o a 5432

### 6. ms-comms/src/main/resources/application.yaml
- Remover defaults de credenciales Gmail
- Remover default vacío de GEMINI_API_KEY

---

## 🚀 Plan de Acción

1. **Fase 1 (Crítica)**: Corregir connectivity en aplicaciones.yaml
2. **Fase 2 (Alta)**: Corregir BD URLs y puertos
3. **Fase 3 (Seguridad)**: Remover credenciales hardcodeadas
4. **Fase 4 (Validación)**: Probar docker compose up --build

