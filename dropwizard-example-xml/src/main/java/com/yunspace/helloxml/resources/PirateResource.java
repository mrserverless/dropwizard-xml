package com.yunspace.helloxml.resources;

import com.yunspace.helloxml.core.Pirate;
import com.yunspace.helloxml.db.PirateDAO;
import com.google.common.base.Optional;
import com.sun.jersey.api.NotFoundException;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/pirates")
public class PirateResource {

    private final PirateDAO pirateDAO;

    public PirateResource(PirateDAO peopleDAO) {
        this.pirateDAO = peopleDAO;
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Pirate createPirate(Pirate pirate) {
        Pirate newPirate = pirateDAO.create(pirate);

        return newPirate;
    }

    @GET
    @Path("{pirateId}")
    @UnitOfWork
    @Produces(MediaType.APPLICATION_XML)
    public Pirate getPirate(@PathParam("pirateId") LongParam pirateId) {
        return findSafely(pirateId.get());
    }

    @GET
    @UnitOfWork
    @Produces(MediaType.APPLICATION_XML)
    public List<Pirate> listPirates() {
        return pirateDAO.findAll();
    }

    @PUT
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_XML)
    public void updatePirate (Pirate pirate) {

        pirateDAO.update(pirate);
    }


    @DELETE
    @Path("{pirateId}")
    @UnitOfWork
    public void deletePirate (@PathParam("pirateId") LongParam pirateId) {

        Pirate pirate = findSafely(pirateId.get());
        pirateDAO.delete(pirate);
    }

    // helper method
    private Pirate findSafely(long pirateId) {
        final Optional<Pirate> person = pirateDAO.findById(pirateId);
        if (!person.isPresent()) {
            throw new NotFoundException("No such user.");
        }
        return person.get();
    }

}
