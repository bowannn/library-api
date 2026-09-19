# library-api

API **RESTful** de **Biblioteca** con Quarkus para Desarrollo Orientado a Servicios (2026-2), Semana 6.
Contexto: **libros** (`books`) y **miembros** (`members`), donde un miembro puede tomar prestados varios libros.
Base de datos **MySQL (XAMPP)**.

Incluye: capas **Resource → Service → Repository**, **DTOs** inmutables con **Bean Validation**,
**manejo de errores** con `ExceptionMapper` (400/404 en JSON) y **Lombok** en los modelos.

## Cómo crear el proyecto (extensiones en el pom)

Al crear el proyecto en [code.quarkus.io](https://code.quarkus.io) agrega estas extensiones. Si ya creaste el proyecto, añade los bloques al `pom.xml`:

| Extensión en code.quarkus.io | Dependencia en el pom | Para qué |
|---|---|---|
| REST | `quarkus-rest` | Endpoints REST (Resource) |
| REST Jackson | `quarkus-rest-jackson` | Serialización JSON |
| Hibernate ORM | `quarkus-hibernate-orm` | Entidades JPA |
| Hibernate ORM with Panache | `quarkus-hibernate-orm-panache` | Repositorios |
| JDBC Driver - MySQL | `quarkus-jdbc-mysql` | Conexión a MySQL |
| Hibernate Validator | `quarkus-hibernate-validator` | Bean Validation (`@NotBlank`, `@Email`, `@Min`...) |

Además, **Lombok** va como dependencia normal (`provided`) en el pom:

```xml
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <version>1.18.42</version>
  <scope>provided</scope>
</dependency>
```

Bootstrap **no** va en el pom: se carga por CDN en los HTML del `mvc` (semana 4). Para API REST no se usa.

## Estructura del proyecto

```
src/main/java/usta/
├── model/                        <- MODELO
│   ├── Book.java                     Entidad JPA (tabla books) + Lombok
│   ├── BookRepository.java           Repositorio con Panache
│   ├── Member.java                   Entidad JPA (tabla members) + préstamos + Lombok
│   └── MemberRepository.java         Repositorio con Panache
├── dto/                          <- DTO (records con validación)
│   ├── BookDTO.java                  @NotBlank @Size @Min @Positive
│   └── MemberDTO.java                @NotBlank @Email
├── service/                      <- SERVICE (reglas de negocio)
│   ├── BookService.java              CRUD + control de stock
│   └── MemberService.java            CRUD + prestar / devolver
├── resource/                     <- RESOURCE (endpoints REST JSON)
│   ├── BookResource.java
│   └── MemberResource.java
└── exception/                    <- MANEJO DE ERRORES
    ├── ValidationExceptionMapper.java    400 (Bean Validation)
    ├── BadRequestExceptionMapper.java    400 (reglas de negocio)
    └── NotFoundExceptionMapper.java      404 (recurso no existe)
```

| Capa | Responsabilidad |
|------|-----------------|
| **Resource** | Recibe la petición HTTP y responde JSON |
| **Service** | Lógica de negocio (stock, préstamos) y validaciones de reglas |
| **Repository** | Acceso a datos (Panache + MySQL) |
| **DTO** | Datos que entran/salen de la API, con validación |
| **Exception** | Convierte errores en JSON uniforme |

## Endpoints

### Libros - `http://localhost:8080/api/books`

| Método | Ruta | Descripción | Cuerpo (JSON) |
|--------|------|-------------|---------------|
| GET | `/api/books` | Listar libros | - |
| GET | `/api/books/{id}` | Obtener uno | - |
| POST | `/api/books` | Crear | `{"title":"Clean Code","author":"Robert Martin","isbn":"9780132350884","price":45.00,"stock":10}` |
| PUT | `/api/books/{id}` | Actualizar | `{"title":"Clean Code 2ed","author":"R. Martin","isbn":"9780132350884","price":48.00,"stock":8}` |
| DELETE | `/api/books/{id}` | Eliminar | - |

### Miembros - `http://localhost:8080/api/members`

| Método | Ruta | Descripción | Cuerpo (JSON) |
|--------|------|-------------|---------------|
| GET | `/api/members` | Listar miembros | - |
| GET | `/api/members/{id}` | Obtener uno | - |
| POST | `/api/members` | Crear | `{"name":"Ana Pérez","email":"ana@correo.com"}` |
| PUT | `/api/members/{id}` | Actualizar | `{"name":"Ana","email":"ana2@correo.com"}` |
| POST | `/api/members/{id}/books/{bookId}` | Prestar libro | - |
| DELETE | `/api/members/{id}/books/{bookId}` | Devolver libro | - |
| DELETE | `/api/members/{id}` | Eliminar | - |

## Préstamo de libros (stock)

| Acción | Ruta | Efecto en el stock | Errores posibles |
|--------|------|--------------------|------------------|
| Prestar | `POST /api/members/{id}/books/{bookId}` | **Resta 1** al stock del libro | `400` sin stock o si ya lo tiene prestado · `404` si el libro o el miembro no existe |
| Devolver | `DELETE /api/members/{id}/books/{bookId}` | **Suma 1** al stock del libro | `400` si el miembro no lo tiene prestado · `404` si el libro o el miembro no existe |

* Si se elimina un libro que está prestado, se quita primero de los préstamos de los miembros (`member_books`) y luego se borra.

## Validación y manejo de errores

**DTOs** (records) con anotaciones de Jakarta Validation:

| Campo | Validación |
|-------|-----------|
| `title`, `author`, `isbn` (Book) | `@NotBlank`, `@Size(max = ...)` |
| `stock` (Book) | `@NotNull`, `@Min(0)` |
| `price` (Book) | `@NotNull`, `@Positive` |
| `name` (Member) | `@NotBlank`, `@Size(max = 100)` |
| `email` (Member) | `@NotBlank`, `@Email` |

En los Resource se usa `@Valid` para activar la validación antes de entrar al método.

**Respuestas de error (JSON uniforme):**

```json
{"error":"El título es obligatorio"}       // 400 (Bean Validation)
{"error":"Sin stock disponible para el libro: Clean Code"}  // 400 (regla de negocio)
{"error":"Libro no encontrado"}            // 404
```

## Cómo crear y editar un libro y un miembro

En Postman usa **Body → raw → JSON** con estos cuerpos:

**Crear libro** `POST /api/books`
```json
{"title":"Clean Code","author":"Robert Martin","isbn":"9780132350884","price":45.00,"stock":10}
```

**Crear miembro** `POST /api/members`
```json
{"name":"Ana Pérez","email":"ana@correo.com"}
```

**Editar libro** `PUT /api/books/1`
```json
{"title":"Clean Code 2da Ed.","author":"Robert Martin","isbn":"9780132350884","price":48.00,"stock":8}
```

**Editar miembro** `PUT /api/members/1`
```json
{"name":"Ana García","email":"ana.garcia@correo.com"}
```

**Prestar un libro** `POST /api/members/1/books/1` (sin cuerpo JSON)
**Devolver un libro** `DELETE /api/members/1/books/1` (sin cuerpo JSON)

> Para el `id`, primero haz `GET /api/books` o `/api/members` y toma el `id` de la lista.

## Códigos HTTP usados

| Código | Cuándo |
|--------|--------|
| `200 OK` | Consulta o actualización correcta |
| `201 Created` | Recurso creado (POST) |
| `204 No Content` | Recurso eliminado (DELETE) |
| `400 Bad Request` | Datos inválidos o regla de negocio incumplida |
| `404 Not Found` | El recurso solicitado no existe |

## Cómo ejecutarlo

Requisitos: JDK 21, Maven (o `./mvnw`) y **XAMPP con MySQL corriendo**.

1. Iniciar Apache y MySQL desde el panel de XAMPP.
2. Crear la base de datos (phpMyAdmin o consola):

   ```sql
   CREATE DATABASE library;
   ```

3. Ejecutar la aplicación:

   ```shell script
   ./mvnw quarkus:dev
   ```

4. Probar con **Postman** o el navegador:
   * `GET http://localhost:8080/api/books` -> `[]`
   * `POST http://localhost:8080/api/books` con Body JSON -> crea y devuelve `201`.

## Configuración de la base de datos

En `src/main/resources/application.properties`:

```properties
quarkus.datasource.db-kind=mysql
quarkus.datasource.username=root
quarkus.datasource.password=
quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/library
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.database.version-check.enabled=false
```
