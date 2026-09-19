package usta.exception;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// Convierte 404 en JSON con el mensaje ("Libro no encontrado" / "Miembro no encontrado")
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Recurso no encontrado";
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"" + msg + "\"}")
                .type("application/json")
                .build();
    }
}
