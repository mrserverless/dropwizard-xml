package com.yunspace.helloxml;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.yunspace.dropwizard.jackson.xml.JacksonXML;
import com.yunspace.helloxml.core.Player;
import com.yunspace.helloxml.db.PlayerDAO;
import com.yunspace.helloxml.resources.CharacterResource;
import com.yunspace.helloxml.resources.PlayerResource;
import io.dropwizard.Application;
import io.dropwizard.assets.AssetsBundle;
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
            new HibernateBundle<HelloXmlConfiguration>(Player.class) {
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

        bootstrap.addBundle(new AssetsBundle());
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

        JacksonXmlModule jacksonXmlModule = new JacksonXmlModule();
        jacksonXmlModule.setDefaultUseWrapper(false); // default to unwrapped lists
        XmlMapper mapper = JacksonXML.newXMLMapper(jacksonXmlModule);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        final PlayerDAO dao = new PlayerDAO(hibernateBundle.getSessionFactory());
        environment.jersey().register(new PlayerResource(dao));
        environment.jersey().register(new CharacterResource(dao));
    }
}
