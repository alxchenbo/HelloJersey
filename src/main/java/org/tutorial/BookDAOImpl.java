package org.tutorial;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {

    public List<Book> findByQuery(String query, String... criteria) {
        List<Book> result = new ArrayList<>();
        try (
                Connection connection = DBManager.getInstance().getConnection();
                PreparedStatement statement = connection.prepareStatement(query)
                ) {
            if (criteria.length==1) {
                statement.setString(1, criteria[0]);
            }
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String title = rs.getString("title");
                int id = rs.getInt("id");
                String author = rs.getString("author");
                Book book = new Book(id, title, author);
                result.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace(); //TODO replace with logging framework
        }
        return result;
    }

    @Override
    public List<Book> findByTitle(String searchText) {
        return findByQuery("select id, title, author from books where title like ?", searchText+"%");
    }

    @Override
    public List<Book> findByAll() {
        return findByQuery("select id, title, author from books");
    }
}
