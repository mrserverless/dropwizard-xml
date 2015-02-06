package com.yunspace.dropwizard.xml.example.db;

import java.util.List;

import org.hibernate.SessionFactory;

import com.google.common.base.Optional;
import com.yunspace.dropwizard.xml.example.core.Ship;

import io.dropwizard.hibernate.AbstractDAO;

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
        return currentSession().createCriteria( Ship.class ).list();
    }

    public void update(Ship ship) {

        this.currentSession().update(Ship.class.getName(), ship);
    }

    public void delete(Ship ship) {
        this.currentSession().delete(Ship.class.getName(), ship);
    }

}
