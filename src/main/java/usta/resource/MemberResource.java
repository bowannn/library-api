package usta.resource;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import usta.dto.MemberDTO;
import usta.model.Member;
import usta.service.MemberService;

import java.util.List;

@Path("/api/members")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MemberResource {
    @Inject
    MemberService service;

    // Lista todos
    @GET
    public List<Member> list() {
        return service.findAll();
    }

    // Busca por id
    @GET
    @Path("/{id}")
    public Member get(@PathParam("id") Long id) {
        return service.findById(id);
    }

    // Crea (JSON validado)
    @POST
    public Response create(@Valid MemberDTO dto) {
        Member created = service.create(dto);
        return Response.status(Response.Status.CREATED).entity(created).build();
    }

    // Actualiza (JSON validado)
    @PUT
    @Path("/{id}")
    public Member update(@PathParam("id") Long id, @Valid MemberDTO dto) {
        return service.update(id, dto);
    }

    // Elimina por id
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        service.delete(id);
        return Response.noContent().build();
    }

    // Presta un libro al miembro
    @POST
    @Path("/{memberId}/books/{bookId}")
    public Member borrowBook(@PathParam("memberId") Long memberId, @PathParam("bookId") Long bookId) {
        return service.borrowBook(memberId, bookId);
    }

    // Devuelve un libro del miembro
    @DELETE
    @Path("/{memberId}/books/{bookId}")
    public Member returnBook(@PathParam("memberId") Long memberId, @PathParam("bookId") Long bookId) {
        return service.returnBook(memberId, bookId);
    }
}
