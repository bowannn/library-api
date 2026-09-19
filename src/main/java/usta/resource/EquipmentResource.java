package usta.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import usta.dto.EquipmentDTO;
import usta.model.Equipment;
import usta.service.EquipmentService;

import java.util.List;

@Path("/api/equipment")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EquipmentResource {

    @Inject
    EquipmentService service;

    // Lista todos
    @GET
    public List<Equipment> list() {
        return service.findAll();
    }

    // Busca por id
    @GET
    @Path("/{id}")
    public Equipment get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    // Crea (JSON validado)
    @POST
    public Response create(@Valid EquipmentDTO dto) {
        Equipment created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // Actualiza (JSON validado)
    @PUT
    @Path("/{id}")
    public Equipment update(@PathParam("id") Long id, @Valid EquipmentDTO dto) {
        return service.update(id, dto);
    }

    // Elimina por id
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }
}
