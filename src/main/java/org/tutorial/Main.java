package org.tutorial;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {

        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
               LOGGER.severe("Erreur : le fichier application.properties est introuvable.");
                return;
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        String baseUri = props.getProperty("server.base.uri");
        String apiUri = baseUri+props.getProperty("server.api.uri");

        final ResourceConfig rc = new ResourceConfig();
        rc.register(BookController.class);

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(apiUri), rc);

        CLStaticHttpHandler staticHttpHandler = new CLStaticHttpHandler(Main.class.getClassLoader(), "/static/");
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");

       LOGGER.info("L'API JAX-RS est accessible sur : " + apiUri);
       LOGGER.info("Les pages Web statiques sont servies à la racine du serveur : "+baseUri);
    }
}