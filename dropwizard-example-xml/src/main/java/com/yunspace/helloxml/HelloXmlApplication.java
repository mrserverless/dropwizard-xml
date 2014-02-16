package com.yunspace.helloxml;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.yunspace.dropwizard.jackson.xml.JacksonXML;
import com.yunspace.dropwizard.jersey.jackson.xml.JacksonXMLMessageBodyProvider;
import com.yunspace.helloxml.core.Pirate;
import com.yunspace.helloxml.core.Ship;
import com.yunspace.helloxml.db.PirateDAO;
import com.yunspace.helloxml.db.ShipDAO;
import com.yunspace.helloxml.resources.PirateResource;
import com.yunspace.helloxml.resources.ShipResource;
import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.migrations.MigrationsBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class HelloXmlApplication extends Application<HelloXmlConfiguration> {
    public static void main(String[] args) throws Exception {
        new HelloXmlApplication().run(args);
    }

    private final HibernateBundle<HelloXmlConfiguration> hibernateBundle =
            new HibernateBundle<HelloXmlConfiguration>(Pirate.class, Ship.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(HelloXmlConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "hello-xml";
    }

    @Override
    public void initialize(Bootstrap<HelloXmlConfiguration> bootstrap) {

        bootstrap.addBundle(new MigrationsBundle<HelloXmlConfiguration>() {
            @Override
            public DataSourceFactory getDataSourceFactory(HelloXmlConfiguration configuration) {
                return configuration.getDataSourceFactory();
            }
        });
        bootstrap.addBundle(hibernateBundle);
    }

    @Override
    public void run(HelloXmlConfiguration configuration,
                    Environment environment) throws ClassNotFoundException {

        //register xml provider
        environment.jersey().register(new JacksonXMLMessageBodyProvider(configureMapper(), environment.getValidator()));

        final PirateDAO pirateDAO = new PirateDAO(hibernateBundle.getSessionFactory());
        final ShipDAO shipDAO = new ShipDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new PirateResource(pirateDAO));
        environment.jersey().register(new ShipResource(shipDAO));
    }

    /**
     * configures the XMLMapper based on requirements
     * @return xmlMapper
     */
    private XmlMapper configureMapper () {
        XmlMapper xmlMapper = JacksonXML.newXMLMapper();        // also accepts JacksonXmlModule as input
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT).               // turn on indenting
                setSerializationInclusion(JsonInclude.Include.NON_NULL);    // ignore null elements

        return xmlMapper;
    }
}
