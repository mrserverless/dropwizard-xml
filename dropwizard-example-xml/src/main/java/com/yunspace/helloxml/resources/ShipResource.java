package com.yunspace.helloxml.resources;

import com.google.common.base.Optional;
import com.sun.jersey.api.NotFoundException;
import com.yunspace.helloxml.core.Ship;
import com.yunspace.helloxml.db.ShipDAO;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/ships")
public class ShipResource {

    private final ShipDAO shipDAO;

    public ShipResource(ShipDAO shipDAO) {
        this.shipDAO = shipDAO;
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_XML)
    public Ship createShip(Ship ship) {
        return shipDAO.create(ship);
    }

    @GET
    @Path("{shipId}")
    @UnitOfWork
    @Produces(MediaType.APPLICATION_XML)
    public Ship getShip(@PathParam("shipId") LongParam shipId) {
        return findSafely(shipId.get());
    }

    @GET
    @UnitOfWork
    @Produces(MediaType.APPLICATION_XML)
    public List<Ship> listShips() {
        return shipDAO.findAll();
    }

    @PUT
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_XML)
    public void updateShip (Ship Ship) {

        shipDAO.update(Ship);
    }


    @DELETE
    @Path("{shipId}")
    @UnitOfWork
    public void deleteShip (@PathParam("shipId") LongParam shipId) {

        Ship Ship = findSafely(shipId.get());
        shipDAO.delete(Ship);
    }

    // helper method
    private Ship findSafely(long ShipId) {
        final Optional<Ship> person = shipDAO.findById(ShipId);
        if (!person.isPresent()) {
            throw new NotFoundException("No such user.");
        }
        return person.get();
    }

}
