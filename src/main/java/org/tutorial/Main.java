package org.tutorial;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {

        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                System.out.println("Erreur : le fichier application.properties est introuvable.");
                return;
            }
            props.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        String baseUri = props.getProperty("server.base.uri");
        String apiUri = baseUri+props.getProperty("server.api.uri");

        final ResourceConfig rc = new ResourceConfig().packages("org.tutorial");

        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(URI.create(apiUri), rc);

        CLStaticHttpHandler staticHttpHandler = new CLStaticHttpHandler(Main.class.getClassLoader(), "/static/");
        server.getServerConfiguration().addHttpHandler(staticHttpHandler, "/");

        System.out.println("L'API JAX-RS est accessible sur : " + apiUri);
        System.out.println("Les pages Web statiques sont servies à la racine du serveur : "+baseUri);
        System.out.println("Appuyez sur Ctrl+C pour arrêter le serveur...");
    }
}