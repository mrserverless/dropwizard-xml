package com.yunspace.helloxml.resources;

import com.yunspace.helloxml.core.Player;
import com.yunspace.helloxml.db.PlayerDAO;
import com.google.common.base.Optional;
import com.sun.jersey.api.NotFoundException;
import io.dropwizard.hibernate.UnitOfWork;
import io.dropwizard.jersey.params.LongParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/player")
@Produces(MediaType.APPLICATION_XML)
public class PlayerResource {

    private final PlayerDAO peopleDAO;

    public PlayerResource(PlayerDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }

    @POST
    @UnitOfWork
    public Player createPerson(Player player) {
        return peopleDAO.create(player);
    }

    @GET
    @Path("{playerId}")
    @UnitOfWork
    public Player getPerson(@PathParam("personId") LongParam personId) {
        return findSafely(personId.get());
    }

    @GET
    @UnitOfWork
    public List<Player> listPeople() {
        return peopleDAO.findAll();
    }

    @PUT
    @UnitOfWork
    public void updatePerson (Player player) {

        peopleDAO.update(player);
    }


    @DELETE
    @Path("{playerId}")
    @UnitOfWork
    public void deletePerson (@PathParam("playerId") LongParam playerId) {

        Player player = findSafely(playerId.get());
        peopleDAO.delete(player);
    }

    // helper method
    private Player findSafely(long personId) {
        final Optional<Player> person = peopleDAO.findById(personId);
        if (!person.isPresent()) {
            throw new NotFoundException("No such user.");
        }
        return person.get();
    }

}
