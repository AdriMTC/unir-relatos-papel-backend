Observaciones importantes
1) Inconsistencia del Gateway
   Encontré una discrepancia:
   En gateway/src/main/resources/application.yaml el puerto es 8090
   Pero en documentación y Postman aparecen referencias a http://localhost:8080
   Eso significa que:
   la configuración actual del código usa 8090
   la documentación parece estar desactualizada o venir de una versión anterior
2) ms-users usa MySQL en 3308
   En ms-users/src/main/resources/application.yml la URL por defecto es:
   jdbc:mysql://localhost:3308/db_users?...



## 🗺️ Tabla Maestra de Puertos (Ecosistema Completo)

Esta tabla consolida todos los componentes del sistema "Relatos de Papel", divididos por su entorno (Microservicios, Infraestructura local en Docker y APIs de Terceros).

| Componente / Servicio | Puerto | Tipo | Entorno / Categoría | Descripción / Uso |
| :--- | :---: | :---: | :--- | :--- |
| **eureka** | `8761` | HTTP | Microservicios | Servidor de descubrimiento (Spring Cloud) |
| **gateway** | `8090` | HTTP | Microservicios | API Gateway (Enrutador central del sistema) |
| **catalogue** | `8081` | HTTP | Microservicios | Microservicio de catálogo de libros |
| **orders** | `8082` | HTTP | Microservicios | Microservicio de gestión de pedidos |
| **ms-users** | `8083` | HTTP | Microservicios | Microservicio de usuarios y autenticación |
| **ms-comms** | `8084` | HTTP | Microservicios | Microservicio de notificaciones / comunicaciones |
| **MySQL users** | `3308` | TCP | Infraestructura (Docker) | Base de datos asignada a `ms-users` |
| **PostgreSQL catalogue** | `5434` | TCP | Infraestructura (Docker) | Base de datos asignada a `catalogue` |
| **PostgreSQL orders** | `5435` | TCP | Infraestructura (Docker) | Base de datos asignada a `orders` |
| **Redis** | `6379` | TCP | Infraestructura (Docker) | Almacenamiento en caché y sesiones para `ms-users` |
| **RabbitMQ** | `5672` | AMQP | Infraestructura (Docker) | Broker de mensajería para comunicación asíncrona |
| **RabbitMQ UI** | `15672` | HTTP | Infraestructura (Docker) | Interfaz gráfica web de administración de RabbitMQ |
| **SMTP (Gmail)** | `587` | SMTP TLS | Dependencias Externas | Salida de correos electrónicos para `ms-comms` |
| **Gemini API** | `443` | HTTPS | Dependencias Externas | Conectividad con la IA de Google para funciones del sistema |


## 🗺️ Mapa de Puertos del Proyecto

| Componente | Puerto | Tipo | Observación |
| :--- | :---: | :---: | :--- |
| **eureka** | `8761` | HTTP | Servidor de descubrimiento |
| **gateway** | `8090` | HTTP | API Gateway según `gateway/src/main/resources/application.yaml` |
| **catalogue** | `8081` | HTTP | Microservicio de catálogo |
| **orders** | `8082` | HTTP | Microservicio de pedidos |
| **ms-users** | `8083` | HTTP | Microservicio de usuarios/autenticación |
| **ms-comms** | `8084` | HTTP | Microservicio de comunicaciones |



## 🐳 Infraestructura Local (Docker)

| Servicio | Puerto Local | Puerto Interno | Uso |
| :--- | :---: | :---: | :--- |
| **PostgreSQL catalogue** | `5434` | `5432` | Base de datos del catálogo |
| **PostgreSQL orders** | `5435` | `5432` | Base de datos de pedidos |
| **RabbitMQ** | `5672` | `5672` | AMQP para comunicación entre servicios |
| **RabbitMQ Management UI** | `15672` | `15672` | Panel web de administración |
| **Redis** | `6379` | `6379` | Caché/sesiones para `ms-users` |



## 🌐 Dependencias Externas usadas por el Código

| Componente | Puerto | Tipo | Observación |
| :--- | :---: | :---: | :--- |
| **SMTP Gmail** | `587` | SMTP TLS | Usado por `ms-comms` |
| **Gemini API** | `443` | HTTPS | Acceso a `generativelanguage.googleapis.com` |


## ⚙️ Puertos Definidos en la Configuración Interna

| Módulo / Microservicio | Puerto Configurado | Archivo de Referencia (Típico) |
| :--- | :---: | :--- |
| **catalogue** | `8081` | `catalogue/src/main/resources/application.yml` |
| **orders** | `8082` | `orders/src/main/resources/application.yml` |
| **ms-users** | `8083` | `ms-users/src/main/resources/application.yml` |
| **ms-comms** | `8084` | `ms-comms/src/main/resources/application.yml` |
| **gateway** | `8090` | `gateway/src/main/resources/application.yml` |
| **eureka** | `8761` | `eureka/src/main/resources/application.yml` |




