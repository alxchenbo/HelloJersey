package org.tutorial;

import java.util.List;

public class BookDAOMockImpl implements BookDAO {

	@Override
	public List<Book> findAll() {
		List<Book> books = initBooks();
		return books;
	}

	private List<Book> initBooks() {
		Book book1 = new Book(1, "titre1", "author1");
		Book book2 = new Book(2, "title2", "Author2");
		List<Book> books = List.of(book1, book2);
		return books;
	}

	@Override
	public List<Book> findByTitle(String searchText) {
		List<Book> books = initBooks();

		return books.stream().filter(x -> x.getTitle().contains(searchText)).toList();

	}

}
