package com.yunspace.dropwizard.xml.example;

import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.yunspace.dropwizard.xml.XmlBundle;
import com.yunspace.dropwizard.xml.example.core.Pirate;
import com.yunspace.dropwizard.xml.example.core.Ship;
import com.yunspace.dropwizard.xml.example.db.PirateDAO;
import com.yunspace.dropwizard.xml.example.db.ShipDAO;
import com.yunspace.dropwizard.xml.example.resources.PirateResource;
import com.yunspace.dropwizard.xml.example.resources.ShipResource;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.databind.SerializationFeature.INDENT_OUTPUT;

public class PirateApplication extends Application<PirateConfiguration> {
    public static void main(String[] args) throws Exception {
        new PirateApplication().run(args);
    }

    private final HibernateBundle<PirateConfiguration> hibernateBundle =
            new HibernateBundle<PirateConfiguration>(Pirate.class, Ship.class) {
                @Override
                public DataSourceFactory getDataSourceFactory(PirateConfiguration configuration) {
                    return configuration.getDataSourceFactory();
                }
            };

    @Override
    public String getName() {
        return "XML Pirate Application";
    }

    @Override
    public void initialize(Bootstrap<PirateConfiguration> bootstrap) {
        bootstrap.addBundle(hibernateBundle);

        final XmlBundle xmlBundle = new XmlBundle();
        xmlBundle.getXmlMapper()
                .enable(INDENT_OUTPUT)
                .setSerializationInclusion(NON_NULL)
                .registerModule(new Hibernate4Module());
        bootstrap.addBundle(xmlBundle);
    }

    @Override
    public void run(PirateConfiguration configuration,
            Environment environment) throws ClassNotFoundException {

        final PirateDAO pirateDAO = new PirateDAO(hibernateBundle.getSessionFactory());
        final ShipDAO shipDAO = new ShipDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new PirateResource(pirateDAO));
        environment.jersey().register(new ShipResource(shipDAO));
    }
}
