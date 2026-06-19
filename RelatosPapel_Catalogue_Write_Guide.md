# RelatosPapel - Guia de pruebas de escritura para Catalogue

Esta guia resume como usar la coleccion de Postman para probar metodos de escritura (`POST`, `PUT`, `PATCH`, `DELETE`) del catalogo a traves del API Gateway.

## Archivos

- Coleccion: `RelatosPapel_Catalogue_Write.postman_collection.json`
- Environment: `RelatosPapel_Catalogue_Write_Local.postman_environment.json`

## Flujo recomendado

1. `Login admin (guardar adminOpaqueToken)` -> **200**
2. `POST crear libro - admin` -> **200** (guarda `createdBookId`)
3. `PUT actualizar libro completo - admin` -> **200**
4. `PATCH actualizar titulo - admin` -> **200**
5. `PATCH cambiar visibilidad - admin` -> **200**
6. `DELETE libro creado - admin` -> **200**
7. `Logout admin` -> **204**

## Casos de error incluidos

- `POST crear libro - sin token` -> **401**
- `POST sin targetMethod - gateway rechaza` -> **400**
- `PUT libro inexistente` -> **404**

## Importante: wrapper obligatorio para POST

En este proyecto, el gateway intercepta todos los `POST` no-auth y espera el body con formato `GatewayRequest`.

### Formato correcto de POST a `/api/v1/books`

```json
{
  "targetMethod": "POST",
  "queryParams": {},
  "body": {
    "title": "El Quijote - Postman Test",
    "author": "Miguel de Cervantes",
    "publicationDate": "1605-01-16",
    "category": "Novela",
    "isbn": "978-TEST-123456789",
    "rating": 4,
    "visible": true,
    "stock": 10,
    "price": 19.99
  }
}
```

Si envias el `POST` con `BookDTO` directo (sin `targetMethod`), el gateway responde **400**.

## PUT / PATCH / DELETE

- `PUT`, `PATCH` y `DELETE` se envian de forma directa (sin wrapper `GatewayRequest`).
- Para `PUT` y `PATCH`, el body es tipo `BookDTO`.

## Variables de entorno clave

- `baseUrl` (por defecto: `http://localhost:8090`)
- `adminOpaqueToken`
- `createdBookId`
- `bookIsbn` (se genera automaticamente en pre-request)
- `bookTitle`, `bookAuthor`, `bookPublicationDate`, `bookCategory`
- `bookRating`, `bookVisible`, `bookStock`, `bookPrice`
- `updatedBookTitle`, `patchTitle`

## Uso rapido en Postman

1. Importa ambos archivos (`collection` + `environment`).
2. Selecciona environment `RelatosPapel Catalogue Write Local`.
3. Ejecuta en orden las carpetas `00` a `05`.
4. Verifica codigos esperados en cada request.

## Notas

- El ISBN se genera automaticamente para evitar conflictos por la restriccion `UNIQUE` en base de datos.
- Esta guia asume gateway en `http://localhost:8090` y servicios levantados/registrados en Eureka.

