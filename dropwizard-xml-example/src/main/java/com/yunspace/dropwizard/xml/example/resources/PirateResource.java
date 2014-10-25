package com.yunspace.dropwizard.xml.example.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Optional;
import com.yunspace.dropwizard.xml.example.core.Pirate;
import com.yunspace.dropwizard.xml.example.db.PirateDAO;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

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
    public Optional<Pirate> getPirate(@PathParam("pirateId") LongParam pirateId) {
        return pirateDAO.findById(pirateId.get());
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
    public void updatePirate(Pirate pirate) {

        pirateDAO.update(pirate);
    }

    @DELETE
    @Path("{pirateId}")
    @UnitOfWork
    public void deletePirate(@PathParam("pirateId") LongParam pirateId) {
        Optional<Pirate> pirateOptional = pirateDAO.findById(pirateId.get());
        if (pirateOptional.isPresent()) {
            pirateDAO.delete(pirateOptional.get());
        }
    }

}
