# TP : Développement d'une API REST avec Java, Jersey et Grizzly

## Objectif du TP
L'objectif de ce TP est de créer progressivement une API RESTful autonome en Java. Nous utiliserons **Jakarta RESTful Web Services (JAX-RS)** (implémentation Jersey) et le serveur web léger embarqué **Grizzly**.

---

## Étape 1 : Initialisation du projet

1. Dans votre IDE, créez un nouveau projet **Maven**.
2. Remplacez le contenu du fichier `pom.xml` par le code suivant pour ajouter les dépendances nécessaires (Grizzly, Jersey, JSON, etc.) et définir la version de Java :

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="[http://maven.apache.org/POM/4.0.0](http://maven.apache.org/POM/4.0.0)"
         xmlns:xsi="[http://www.w3.org/2001/XMLSchema-instance](http://www.w3.org/2001/XMLSchema-instance)"
         xsi:schemaLocation="[http://maven.apache.org/POM/4.0.0](http://maven.apache.org/POM/4.0.0) [http://maven.apache.org/xsd/maven-4.0.0.xsd](http://maven.apache.org/xsd/maven-4.0.0.xsd)">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.tutorial</groupId>
    <artifactId>HelloJersey</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <jersey.version>4.0.2</jersey.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.glassfish.jersey.containers</groupId>
            <artifactId>jersey-container-grizzly2-http</artifactId>
            <version>${jersey.version}</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jersey.inject</groupId>
            <artifactId>jersey-hk2</artifactId>
            <version>${jersey.version}</version>
        </dependency>
        <dependency>
            <groupId>org.glassfish.jersey.media</groupId>
            <artifactId>jersey-media-json-binding</artifactId>
            <version>${jersey.version}</version>
        </dependency>
        <dependency>
            <groupId>com.mysql</groupId>
            <artifactId>mysql-connector-j</artifactId>
            <version>26.7.0</version>
            <scope>compile</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.16.0</version>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>exec-maven-plugin</artifactId>
                <version>3.6.4</version>
                <configuration>
                    <mainClass>org.tutorial.Main</mainClass>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```

---

## Étape 2 : Création du lanceur (Main) et configuration

1. Dans `src/main/resources`, créez un fichier `application.properties` qui contiendra les URL de notre serveur :

```properties
server.base.uri=http://localhost:8080/
server.api.uri=api/

```

2. Créez le package `org.tutorial` dans `src/main/java`.
3. Créez-y la classe `Main`. Cette classe initialise et démarre le serveur Grizzly en lui attachant notre configuration Jersey.

```java
package org.tutorial;

import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        try {
            final HttpServer httpServer = initServer();
            httpServer.start();

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                   LOGGER.info("Arrêt de l'application...");
                   httpServer.shutdownNow();
                   LOGGER.info("Fermeture terminée.");
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, null, e);
                }
            }));

           LOGGER.info("Application démarrée. (Faites CTRL+C pour arrêter)");
           Thread.currentThread().join();

        } catch (InterruptedException | IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public static HttpServer initServer() {
        Properties props = new Properties();
        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
            props.load(input);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

        String baseUri = props.getProperty("server.base.uri");
        String apiUri = baseUri + props.getProperty("server.api.uri");

        final ResourceConfig rc = new ResourceConfig();
        // C'est ici que l'on déclare notre futur contrôleur
        rc.register(BookController.class);

        HttpServer httpServer = GrizzlyHttpServerFactory.createHttpServer(URI.create(apiUri), rc);
        return httpServer;
    }
}

```

---

## Étape 3 : Création du Contrôleur (Hello World)

Nous allons créer notre premier point d'entrée REST (Endpoint).
Créez la classe `BookController` dans le même package :

```java
package org.tutorial;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.logging.Logger;

@Path("/books")
public class BookController {

    private static final Logger LOGGER = Logger.getLogger(BookController.class.getName());

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    public String hello() {
        LOGGER.info("Hello world !");
        return "Hello World!";
    }
}

```

**Testez votre application :** Lancez la classe `Main` (ou via `mvn exec:java`) et rendez-vous sur `http://localhost:8080/api/books/hello`.

---

## Étape 4 : Création du Modèle de Données (Book)

Créez une classe `Book` dans le package `org.tutorial` représentant un livre.
*À vous de l'implémenter en suivant ces directives :*

* **Attributs privés :** `id` (int), `title` (String), `author` (String).
* **Constructeur :** Un constructeur prenant ces trois paramètres pour initialiser les attributs.
* **Encapsulation :** Générez les getters et setters pour tous les attributs.
* **Affichage :** Redéfinissez la méthode `toString()` pour faciliter le débogage.

---

## Étape 5 : Création du DAO (Interface)

Pour interagir avec nos données, nous utilisons le patron de conception Data Access Object (DAO).
Créez l'interface `BookDAO` avec pour l'instant une unique méthode pour récupérer tous les livres :

```java
package org.tutorial;

import java.util.List;

public interface BookDAO {
    List<Book> findAll();
}

```

---

## Étape 6 : Création du Mock (Bouchon)

Pour tester notre API sans base de données, nous allons créer une implémentation fictive en mémoire.
Créez la classe `BookDAOMockImpl` qui implémente l'interface `BookDAO` :

```java
package org.tutorial;

import java.util.List;

public class BookDAOMockImpl implements BookDAO {

    @Override
    public List<Book> findAll() {
        return initBooks();
    }

    private List<Book> initBooks() {
        Book book1 = new Book(1, "Titre1", "Auteur1");
        Book book2 = new Book(2, "Titre2", "Auteur2");
        return List.of(book1, book2);
    }
}

```

---

## Étape 7 : Ajout de la méthode getBooks dans le contrôleur

Retournez dans la classe `BookController`.
*À vous d'implémenter un nouvel endpoint pour lister les livres, en suivant ces étapes :*

1. Déclarez un attribut de classe privé instanciant votre DAO : `private BookDAO bookDAO = new BookDAOMockImpl();`
2. Créez une méthode publique `getBooks()` retournant une `List<Book>`.
3. Ajoutez l'annotation `@GET` au-dessus de la méthode.
4. Ajoutez l'annotation `@Produces(MediaType.APPLICATION_JSON)` pour indiquer que le résultat sera du JSON.
5. Dans le corps de la méthode, appelez `bookDAO.findAll()` et retournez le résultat.

*Testez l'URL `http://localhost:8080/api/books` pour voir votre liste de livres en JSON.*

---

## Étape 8 : Connexion à la Base de Données

Le code pour requêter une véritable base de données MySQL via JDBC vous sera fourni lors de la séance (classe `BookDAOImpl`).

1. Intégrez la classe `BookDAOImpl` fournie dans votre projet.
2. Modifiez votre `BookController` pour utiliser cette nouvelle implémentation :
Changez `new BookDAOMockImpl()` par `new BookDAOImpl()`.
3. Relancez le serveur et testez à nouveau votre endpoint pour vérifier que les données proviennent bien de la base.

---

## Étape 9 : Ajouter une méthode de recherche par titre

1. Ajoutez la signature suivante dans l'interface `BookDAO` :
`List<Book> findByTitle(String searchText);`
2. Implémentez cette méthode dans votre `BookDAOMockImpl` (utilisez les streams Java ou une boucle classique pour filtrer la liste initialisée via `initBooks()` selon si le titre contient le texte recherché).
*(N'oubliez pas d'implémenter aussi cette méthode dans le DAO Base de données, le code SQL vous sera fourni).*

---

## Étape 10 : Paramètre de requête (QueryParam)

Nous allons modifier notre endpoint de liste des livres pour qu'il gère une recherche facultative par titre.

*À vous de modifier la méthode `getBooks` du `BookController` :*

1. Ajoutez le paramètre `@QueryParam("title") String title` dans la signature de la méthode.
2. Dans la méthode, ajoutez une condition :
* Si `title` n'est pas nul et n'est pas vide (`!title.isEmpty()`), appelez `bookDAO.findByTitle(title)`.
* Sinon, appelez `bookDAO.findAll()`.


3. Retournez la liste de livres correspondante.

*Testez l'URL `http://localhost:8080/api/books?title=Titre1`.*

---

## Étape 11 : Ajouter la création d'un livre (POST)

Enfin, ajoutons un endpoint pour créer un livre (via l'envoi d'un formulaire classique).
Ajoutez la méthode suivante dans `BookController` :

```java
    @POST
    @Consumes("application/x-www-form-urlencoded")
    public void createBook(@FormParam("book_title") String bookTitle, @FormParam("book_author") String bookAuthor) {
        Book book = new Book(0, bookTitle, bookAuthor);
        // Pour ce TP, on se contente de l'afficher dans la console du serveur
        System.out.println("Nouveau livre créé : " + book);
    }

```

*Pour tester ce POST, utilisez cURL dans un terminal ou un outil comme Postman/Insomnia :*

```bash
curl -X POST http://localhost:8080/api/books \
-H "Content-Type: application/x-www-form-urlencoded" \
-d "book_title=Mon Super Livre&book_author=Moi"

```

*(Vérifiez la console de votre IDE, le message devrait s'y afficher).*

```

```