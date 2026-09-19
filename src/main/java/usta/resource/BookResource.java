package usta.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import usta.dto.BookDTO;
import usta.model.Book;
import usta.service.BookService;

import java.util.List;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {

    @Inject
    BookService service;

    // Lista todos
    @GET
    public List<Book> list() {
        return service.findAll();
    }

    // Busca por id
    @GET
    @Path("/{id}")
    public Book get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    // Crea (JSON validado)
    @POST
    public Response create(@Valid BookDTO dto) {
        Book created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // Actualiza (JSON validado)
    @PUT
    @Path("/{id}")
    public Book update(@PathParam("id") Long id, @Valid BookDTO dto) {
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
