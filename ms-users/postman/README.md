# Postman - RelatosPapel Act3

Archivos incluidos:

| Coleccion | Environment recomendado | Proposito |
|---|---|---|
| `RelatosPapel_Act3.postman_collection.json` | `RelatosPapel_Local.postman_environment.json` | Flujo completo de auth + catalogue + orders |
| `RelatosPapel_Orders.postman_collection.json` | `RelatosPapel_Orders_Local.postman_environment.json` | Solo operaciones de orders |
| `RelatosPapel_Catalogue_Write.postman_collection.json` | `RelatosPapel_Catalogue_Write_Local.postman_environment.json` | Operaciones de escritura del catalogo (POST/PUT/PATCH/DELETE) |

Guias:

- `RelatosPapel_Flujo_Token.md` (flujo de uso de token opaco/JWT interno)

## Importar en Postman

1. Abrir Postman.
2. `Import` -> seleccionar ambos archivos JSON.
3. Activar el environment `RelatosPapel Local`.

## Variables nuevas para alta manual

En la coleccion se agregaron estas variables para registrar un usuario manualmente:

- `newUserEmail`
- `newUserPassword`
- `newUserRole` (`ROLE_LECTOR` o `ROLE_ADMIN`)
- `newUserEnabled` (`true` o `false`)

## Coleccion "Catalogue Write Operations"

La coleccion `RelatosPapel_Catalogue_Write.postman_collection.json` cubre todas las operaciones de escritura:

1. Login admin
2. `POST /api/v1/books` — Crear libro (requiere wrapper GatewayRequest)
3. `PUT /api/v1/books/{id}` — Reemplazar libro completo
4. `PATCH /api/v1/books/{id}` — Actualizacion parcial (titulo, visibilidad, etc.)
5. `DELETE /api/v1/books/{id}` — Eliminar libro
6. Logout admin

> **⚠️ Patron GatewayRequest para POST**:
> El gateway intercepta todos los `POST` no-auth y espera el body envuelto:
> ```json
> { "targetMethod": "POST", "queryParams": {}, "body": { ...BookDTO... } }
> ```
> Los metodos `PUT`, `PATCH` y `DELETE` se envian con el body de `BookDTO` directamente.

Variables de entorno relevantes: `bookTitle`, `bookAuthor`, `bookCategory`, `bookRating`, `bookVisible`, `bookStock`, `bookPrice`, `bookPublicationDate`.
El `bookIsbn` se genera automaticamente en el pre-request script para evitar conflictos por la restriccion `UNIQUE`.

---

## Coleccion "Orders Only"

La coleccion `RelatosPapel_Orders.postman_collection.json` incluye un flujo corto para validar solo `orders`:

1. login lector
2. crear orden valida
3. consultar ordenes recientes por usuario
4. casos de error (`401`, `400`, `404`)
5. logout

Environment recomendado para esta coleccion:

- `RelatosPapel_Orders_Local.postman_environment.json`

## Orden sugerido de ejecucion

1. `Register usuario manual (201)` (opcional)
2. `Login lector (guardar opaqueToken)`
3. `GET catalogo con token lector (200)`
4. `POST catalogo con lector (403)`
5. `Login admin (guardar adminOpaqueToken)`
6. `POST catalogo con admin (201/200)`
7. `POST crear orden valida (200)`
8. `GET ordenes recientes por userId (200)`
9. `POST crear orden con quantity invalida (400)`
10. `POST crear orden con libro inexistente (404)`
11. `Logout con token lector (204)`
12. `Reusar token tras logout (401)`

## Nota

Esta coleccion asume que el API Gateway esta en `http://localhost:8090` y que todos los microservicios ya estan levantados y registrados en Eureka.
