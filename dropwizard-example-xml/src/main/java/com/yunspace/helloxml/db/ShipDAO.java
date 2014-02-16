package com.yunspace.helloxml.db;

import com.google.common.base.Optional;
import com.yunspace.helloxml.core.Ship;
import io.dropwizard.hibernate.AbstractDAO;
import org.hibernate.SessionFactory;

import java.util.List;

public class ShipDAO extends AbstractDAO<Ship> {
    public ShipDAO(SessionFactory factory) {
        super(factory);
    }

    public Ship create(Ship ship) {
        return persist(ship);
    }


    public Optional<Ship> findById(Long id) {
        return Optional.fromNullable(get(id));
    }

    public List<Ship> findAll() {
        return list(namedQuery("com.yunspace.helloxml.core.Ship.findAll"));
    }

    public void update (Ship ship) {

        this.currentSession().update(Ship.class.getName(), ship);
    }

    public void delete(Ship ship) {
        this.currentSession().delete(Ship.class.getName(), ship);
    }


}
