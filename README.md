```markdown
# tic-usta - Gestión de Préstamos TIC

API **RESTful** de **préstamo de equipos** con Quarkus para Desarrollo Orientado a Servicios (2026-2), Semana 7.
Contexto: el departamento de TIC de la USTA presta equipos (`equipment`) a estudiantes (`student`), registrando cada
préstamo (`loan`) con su cantidad, fecha de préstamo y fecha de devolución.
Base de datos **MySQL (XAMPP)**.

Incluye: capas **Resource → Service → Repository**, **DTOs** inmutables con **Bean Validation**,
**manejo de errores** con `ExceptionMapper` (400/404 en JSON) y **Lombok** en los modelos.

## Modelo de datos

```text
equipment (1) ----< loan >---- (1) student
  id PK                          id PK
  code                           name
  name                           email
  stock                          program

loan
  id PK
  loan_date
  return_date        (null mientras el préstamo está activo)
  quantity
  equipment_id FK -> equipment.id
  student_id   FK -> student.id

```

## Estructura del proyecto

```text
src/main/java/usta/
├── model/                        <- MODELO
│   ├── Equipment.java                Entidad JPA (tabla equipment) + Lombok
│   ├── EquipmentRepository.java      Repositorio con Panache
│   ├── Student.java                  Entidad JPA (tabla student) + Lombok
│   ├── StudentRepository.java        Repositorio con Panache
│   ├── Loan.java                     Entidad JPA (tabla loan), ManyToOne a Equipment y Student
│   └── LoanRepository.java           Repositorio con Panache
├── dto/                          <- DTO (records con validación)
│   ├── EquipmentDTO.java             @NotBlank @Size @Min
│   ├── StudentDTO.java               @NotBlank @Email
│   └── LoanDTO.java                  @NotNull @Min
├── service/                      <- SERVICE (reglas de negocio)
│   ├── EquipmentService.java         CRUD + bloquea borrado si tiene préstamos
│   ├── StudentService.java           CRUD + bloquea borrado si tiene préstamos
│   └── LoanService.java              CRUD + control de stock + devolución
├── resource/                     <- RESOURCE (endpoints REST JSON)
│   ├── EquipmentResource.java
│   ├── StudentResource.java
│   └── LoanResource.java
└── exception/                    <- MANEJO DE ERRORES
    ├── ValidationExceptionMapper.java    400 (Bean Validation)
    ├── BadRequestExceptionMapper.java    400 (reglas de negocio)
    └── NotFoundExceptionMapper.java      404 (recurso no existe)

```

| Capa | Responsabilidad |
| --- | --- |
| **Resource** | Recibe la petición HTTP y responde JSON |
| **Service** | Lógica de negocio (stock, préstamos) y validaciones de reglas |
| **Repository** | Acceso a datos (Panache + MySQL) |
| **DTO** | Datos que entran/salen de la API, con validación |
| **Exception** | Convierte errores en JSON uniforme |

## Endpoints

### Equipos - `http://localhost:8081/api/equipment`

| Método | Ruta | Descripción | Cuerpo (JSON) |
| --- | --- | --- | --- |
| GET | `/api/equipment` | Listar equipos | - |
| GET | `/api/equipment/{id}` | Obtener uno | - |
| POST | `/api/equipment` | Crear | `{"code":"EQ-001","name":"Portátil Dell","stock":5}` |
| PUT | `/api/equipment/{id}` | Actualizar | `{"code":"EQ-001","name":"Portátil Dell 14\"","stock":4}` |
| DELETE | `/api/equipment/{id}` | Eliminar (falla si tiene préstamos) | - |

### Estudiantes - `http://localhost:8081/api/students`

| Método | Ruta | Descripción | Cuerpo (JSON) |
| --- | --- | --- | --- |
| GET | `/api/students` | Listar estudiantes | - |
| GET | `/api/students/{id}` | Obtener uno | - |
| POST | `/api/students` | Crear | `{"name":"Ana Pérez","email":"ana@correo.com","program":"Ingeniería de Sistemas"}` |
| PUT | `/api/students/{id}` | Actualizar | `{"name":"Ana Pérez","email":"ana2@correo.com","program":"Ingeniería de Sistemas"}` |
| DELETE | `/api/students/{id}` | Eliminar (falla si tiene préstamos) | - |

### Préstamos - `http://localhost:8081/api/loans`

| Método | Ruta | Descripción | Cuerpo (JSON) |
| --- | --- | --- | --- |
| GET | `/api/loans` | Listar préstamos | - |
| GET | `/api/loans/{id}` | Obtener uno | - |
| POST | `/api/loans` | Crear (resta stock) | `{"loanDate":"2026-09-19","quantity":1,"equipmentId":1,"studentId":1}` |
| PUT | `/api/loans/{id}` | Actualizar cantidad/fecha (solo si activo) | `{"loanDate":"2026-09-19","quantity":2,"equipmentId":1,"studentId":1}` |
| PUT | `/api/loans/{id}/devolver` | Marcar como devuelto (suma stock) | - |
| DELETE | `/api/loans/{id}` | Eliminar (si estaba activo, repone stock) | - |

## Reglas de negocio (stock y validaciones)

| Acción | Efecto en el stock | Errores posibles |
| --- | --- | --- |
| Crear préstamo | **Resta** `quantity` al stock del equipo | `400` sin stock suficiente · `404` si el equipo o el estudiante no existe |
| Devolver préstamo | **Suma** `quantity` al stock del equipo | `400` si ya fue devuelto · `404` si el préstamo no existe |
| Eliminar préstamo activo | **Suma** `quantity` al stock del equipo | `404` si el préstamo no existe |
| Eliminar equipo/estudiante | - | `400` si tiene préstamos asociados |

## Validación y manejo de errores

**DTOs** (records) con anotaciones de Jakarta Validation:

| Campo | Validación |
| --- | --- |
| `code`, `name` (Equipment) | `@NotBlank`, `@Size(max = ...)` |
| `stock` (Equipment) | `@NotNull`, `@Min(0)` |
| `name`, `program` (Student) | `@NotBlank`, `@Size(max = ...)` |
| `email` (Student) | `@NotBlank`, `@Email` |
| `loanDate` (Loan) | `@NotNull` |
| `quantity` (Loan) | `@NotNull`, `@Min(1)` |
| `equipmentId`, `studentId` (Loan) | `@NotNull` |

En los Resource se usa `@Valid` para activar la validación antes de entrar al método.

**Respuestas de error (JSON uniforme):**

```json
{"error":"El código es obligatorio"}                          // 400 (Bean Validation)
{"error":"Sin stock disponible para el equipo: Portátil Dell"} // 400 (regla de negocio)
{"error":"Equipo no encontrado"}                               // 404

```

## Cómo probar en Postman

**Crear equipo** `POST /api/equipment`

```json
{"code":"EQ-001","name":"Portátil Dell","stock":5}

```

**Crear estudiante** `POST /api/students`

```json
{"name":"Ana Pérez","email":"ana@correo.com","program":"Ingeniería de Sistemas"}

```

**Crear préstamo** `POST /api/loans`

```json
{"loanDate":"2026-09-19","quantity":1,"equipmentId":1,"studentId":1}

```

**Devolver préstamo** `PUT /api/loans/1/devolver` (sin cuerpo JSON)

> Para el `id`, primero haz `GET /api/equipment`, `/api/students` o `/api/loans` y toma el `id` de la lista.

## Códigos HTTP usados

| Código | Cuándo |
| --- | --- |
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
CREATE DATABASE library_api;

```


3. Ejecutar la aplicación:
```shell script
./mvnw quarkus:dev

```


4. Probar con **Postman** o el navegador:
* `GET http://localhost:8081/api/equipment` -> `[]`
* `POST http://localhost:8081/api/equipment` con Body JSON -> crea y devuelve `201`.



## Configuración de la base de datos y servidor

En `src/main/resources/application.properties`:

```properties
quarkus.http.port=8081
quarkus.datasource.db-kind=mysql
quarkus.datasource.username=root
quarkus.datasource.password=
quarkus.datasource.jdbc.url=jdbc:mysql://localhost:3306/library_api
quarkus.hibernate-orm.database.generation=update
quarkus.hibernate-orm.database.version-check.enabled=false

```

```

```