package org.tutorial;

import java.util.List;

public interface BookDAO {

	List<Book> findAll();

	List<Book> findByTitle(String searchText);
}
