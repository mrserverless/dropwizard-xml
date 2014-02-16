package com.yunspace.helloxml.core;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;

@Entity
@Table(name = "ships")
@NamedQueries({
        @NamedQuery(
                name = "com.yunspace.helloxml.core.Ship.findAll",
                query = "SELECT s FROM Ship s"
        ),
        @NamedQuery(
                name = "com.yunspace.helloxml.core.Ship.findById",
                query = "SELECT s FROM Ship s WHERE s.shipId = :id"
        )
})
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
    private Pirate shipCaptain; //use getter to avoid serialization inifite loop

    public Ship() {
        // Jackson deserialization
    }

    @JacksonXmlProperty(localName = "ShipCaptain")
    public String getCaptain() {
        // avoid a infinite loop by returning Captain name isn't of Captain object.
        return shipCaptain.getNickName();
    }

    @JacksonXmlProperty(localName = "ShipCaptain")
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
