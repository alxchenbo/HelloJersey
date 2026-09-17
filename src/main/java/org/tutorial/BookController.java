package org.tutorial;


//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

import java.util.ArrayList;
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

	/*
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/helloSession")
	public String helloSession(@Context HttpServletRequest req) {
		HttpSession session = req.getSession();
		int count = session.getAttribute("count") == null ? 0 : (int) session.getAttribute("count");
		count++;
		session.setAttribute("count", count);
		return "Hello World! -> " + count;
	}
*/
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<Book> getBooks(@QueryParam("title") String title/*, @Context HttpServletRequest request*/) {

		List<Book> books = null;
		if (title != null && !title.isEmpty()) {
			books = bookDAO.findByTitle(title);
		} else {
			books = bookDAO.findByAll();
		}

		//useSession(request, title);

		return books;
	}
/*
	private void useSession(HttpServletRequest request, String title) {

		HttpSession session = request.getSession();
		List<String> queries = (List<String>) session.getAttribute("queries");
		if (queries == null) {
			queries = new ArrayList<>();
			session.setAttribute("queries", queries);
		}
		queries.add(title);
		System.out.println("liste des recherches stockées en session :");
		queries.stream().forEach(x -> System.out.println("-" + x));

	}
*/
	@POST
	@Consumes("application/x-www-form-urlencoded")
	public void createBook(@FormParam("book_title") String bookTitle, @FormParam("book_author") String bookAuthor) {
		Book book = new Book(0, bookTitle, bookAuthor);
		System.out.println("new book created : " + book);
	}

}
