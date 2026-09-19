package usta.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import usta.dto.StudentDTO;
import usta.model.Student;
import usta.service.StudentService;

import java.util.List;

@Path("/api/students")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StudentResource {

    @Inject
    StudentService service;

    // Lista todos
    @GET
    public List<Student> list() {
        return service.findAll();
    }

    // Busca por id
    @GET
    @Path("/{id}")
    public Student get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    // Crea (JSON validado)
    @POST
    public Response create(@Valid StudentDTO dto) {
        Student created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // Actualiza (JSON validado)
    @PUT
    @Path("/{id}")
    public Student update(@PathParam("id") Long id, @Valid StudentDTO dto) {
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
