package usta.exception;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

// Convierte 400 en JSON con el mensaje ("Sin stock disponible..." / validaciones)
@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException ex) {
        String msg = ex.getMessage() != null ? ex.getMessage() : "Solicitud incorrecta";
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"" + msg + "\"}")
                .type("application/json")
                .build();
    }
}
