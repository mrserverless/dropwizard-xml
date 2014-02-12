package com.yunspace.helloxml.core;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "player")
@NamedQueries({
    @NamedQuery(
        name = "com.yunspace.helloxml.core.Player.findAll",
        query = "SELECT p FROM Player p"
    ),
    @NamedQuery(
        name = "com.yunspace.helloxml.core.Player.findById",
        query = "SELECT p FROM Player p WHERE p.id = :id"
    )
})
@JacksonXmlRootElement(localName="Player")
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JacksonXmlProperty(isAttribute = true)
    private long id;

    @Column(name = "fullName", nullable = false)
    @JacksonXmlProperty(isAttribute = true)
    private String fullName;

    @Column(name = "emailAddress", nullable = false)
    @JacksonXmlProperty(isAttribute = true)
    private String emailAddress;

    @OneToMany(mappedBy="playerOwner", cascade=CascadeType.ALL)
    @JacksonXmlElementWrapper(localName = "Characters", useWrapping = true) //use wrapped list
    @JacksonXmlProperty(localName = "Character")
    private List<Character> characters;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public List<Character> getCharacters() {
        return characters;
    }
}
