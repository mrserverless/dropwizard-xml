package com.yunspace.dropwizard.xml.example.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@Entity
@Table(name = "ships")
@JacksonXmlRootElement(localName = "Ship")
public class Ship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ship_id")
    @JacksonXmlProperty(isAttribute = true)
    private long shipId;

    @Column(name = "ship_name", nullable = false)
    @JacksonXmlProperty(isAttribute = true)
    private String shipName;

    @ManyToOne()
    @JoinColumn(name = "pirate_id")
    private Pirate shipCaptain;

    public Ship() {
        // Jackson deserialization
    }

    @JacksonXmlProperty(localName = "ShipCaptain")
    public String getCaptain() {
        // avoid a infinite loop by returning Captain name instead of Captain object.
        return shipCaptain.getNickName();
    }

    public void setCaptain(Pirate pirate) {
        this.shipCaptain = pirate;
    }

    public Ship(String name) {
        this.shipName = name;
    }

    public long getShipId() {
        return shipId;
    }

    public void setShipId(long shipId) {
        this.shipId = shipId;
    }

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String name) {
        this.shipName = name;
    }

}
