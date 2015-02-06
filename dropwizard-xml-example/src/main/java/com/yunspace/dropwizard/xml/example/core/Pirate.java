package com.yunspace.dropwizard.xml.example.core;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@Entity
@Table(name = "pirates")
@JacksonXmlRootElement(localName = "Pirate")
public class Pirate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pirate_id", nullable = false)
    @JacksonXmlProperty(isAttribute = true)
    private long pirateId;

    @Column(name = "nick_name", nullable = false)
    @JacksonXmlProperty(localName = "NickName")
    private String nickName;

    @Column(name = "real_name", nullable = false)
    @JacksonXmlProperty(localName = "RealName")
    private String realName;

    @OneToMany(mappedBy = "shipCaptain", fetch = FetchType.EAGER)
    @JacksonXmlProperty(localName = "Ship")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "Ships")
    private List<Ship> ships;

    public long getPirateId() {
        return pirateId;
    }

    public void setPirateId(long pirate_id) {
        this.pirateId = pirate_id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public List<Ship> getShips() {
        return ships;
    }
}
