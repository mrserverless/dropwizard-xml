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
import com.yunspace.dropwizard.xml.example.core.Ship;
import com.yunspace.dropwizard.xml.example.db.ShipDAO;

import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

@Path("/ships")
public class ShipResource {

    private final ShipDAO shipDAO;

    public ShipResource(ShipDAO shipDAO) {
        this.shipDAO = shipDAO;
    }

    @POST
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public Ship createShip(Ship ship) {
        return shipDAO.create(ship);
    }

    @GET
    @Path("{shipId}")
    @UnitOfWork
    @Produces(MediaType.APPLICATION_XML)
    public Optional<Ship> getShip(@PathParam("shipId") LongParam shipId) {
        return shipDAO.findById(shipId.get());
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
    public void updateShip(Ship Ship) {

        shipDAO.update(Ship);
    }

    @DELETE
    @Path("{shipId}")
    @UnitOfWork
    public void deleteShip(@PathParam("shipId") LongParam shipId) {
        Optional<Ship> shipOptional = shipDAO.findById(shipId.get());
        if (shipOptional.isPresent()) {
            shipDAO.delete(shipOptional.get());
        }
    }

}
