package org.tutorial;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;


import java.util.List;

@Path("/books")
public class BookController {

	private BookDAO bookDAO = new BookDAOImpl();
	// private BookDAO bookDAO = new BookDAOMockImpl();

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/hello")
	public String hello() {
		return "Hello World!";
	}




	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Book> getBooks(@QueryParam("title") String title) {

		List<Book> books = null;
		if (title != null && !title.isEmpty()) {
			books = bookDAO.findByTitle(title);
		} else {
			books = bookDAO.findByAll();
		}


		return books;
	}

	@POST
	@Consumes("application/x-www-form-urlencoded")
	public void createBook(@FormParam("book_title") String bookTitle, @FormParam("book_author") String bookAuthor) {
		Book book = new Book(0, bookTitle, bookAuthor);
		System.out.println("new book created : " + book);
	}

}
