package com.yunspace.helloxml.db;

import com.yunspace.helloxml.core.Player;
import com.google.common.base.Optional;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class PlayerDAO extends AbstractDAO<Player> {
    public PlayerDAO(SessionFactory factory) {
        super(factory);
    }

    public Player create(Player player) {
        return persist(player);
    }


    public Optional<Player> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public List<Player> findAll() {
        return list(namedQuery("com.yunspace.helloxml.core.Player.findAll"));
    }

    public void update (Player player) {

        this.currentSession().update(Player.class.getName(), player);
    }

    public void delete(Player player) {
        this.currentSession().delete(Player.class.getName(), player);
    }


}
