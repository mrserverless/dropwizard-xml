package com.yunspace.helloxml.resources;

import com.yunspace.helloxml.db.PlayerDAO;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/people/{personId}")
@Produces(MediaType.APPLICATION_JSON)
public class CharacterResource {

    private final PlayerDAO peopleDAO;

    public CharacterResource(PlayerDAO peopleDAO) {
        this.peopleDAO = peopleDAO;
    }


}
