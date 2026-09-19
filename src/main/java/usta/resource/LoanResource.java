package usta.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import usta.dto.LoanDTO;
import usta.model.Loan;
import usta.service.LoanService;

import java.util.List;

@Path("/api/loans")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LoanResource {

    @Inject
    LoanService service;

    // Lista todos
    @GET
    public List<Loan> list() {
        return service.findAll();
    }

    // Busca por id
    @GET
    @Path("/{id}")
    public Loan get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    // Crea (JSON validado) - descuenta stock del equipo
    @POST
    public Response create(@Valid LoanDTO dto) {
        Loan created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // Actualiza (JSON validado) - solo si el préstamo sigue activo
    @PUT
    @Path("/{id}")
    public Loan update(@PathParam("id") Long id, @Valid LoanDTO dto) {
        return service.update(id, dto);
    }

    // Elimina por id
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

    // Marca el préstamo como devuelto y repone el stock del equipo
    @PUT
    @Path("/{id}/devolver")
    public Loan devolver(@PathParam("id") Long id) {
        return service.devolver(id);
    }
}
