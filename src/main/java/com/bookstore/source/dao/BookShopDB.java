package com.bookstore.source.dao;

import com.bookstore.source.entity.Admin;
import com.bookstore.source.entity.Book;
import com.bookstore.source.entity.User;
import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.sql.*;

/**
 * This class has been designed to create databases for the bookshop.
 *
 * @author Ahmadreza Vakil
 */

public class BookShopDB {
    private static Connection con = null;
    private static boolean hasAdminTable = false;
    private static boolean hasBookStockTable = false;
    private static boolean hasOrdersTable = false;
    private static boolean hasShoppingBasketTable = false;
    private static boolean hasUsersTable = false;
    protected static String initVector = "RandomInitVector"; // 16 bytes IV
    protected static String key = "Bar12345Bar12345"; // 128 bit key
    public static boolean testMode = false;


    public static Connection getConn() throws Exception {
        if (con == null || con.isClosed()) {
            if (testMode) {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection("jdbc:sqlite:BookShopTest.db");
                initialiseTables();
            } else {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection("jdbc:sqlite:BookShop.db");
                initialiseTables();
            }
        }
        return con;
    }

    public static void initialiseTables() throws Exception {
        initialiseAdminTable();
        initialiseBookStockTable();
        initialiseOrdersTable();
        initialiseShoppingBasketTable();
        initialiseUsersTable();
    }

    public void deleteBookFromShoppingBasket(Book book, String userId) throws Exception {
        Statement basket = getConn().createStatement();
        ResultSet res = basket.executeQuery("select * from ShoppingBasket where title='" + book.getTitle() + "' AND author='" + book.getAuthor() + "' AND userid ='" + userId + "' AND price ='" + book.getPrice() + "'");
        if (res.next()) {
            if (book.getQuantity() <= res.getInt("quantity")) {
                String sql = "UPDATE bookStockTable SET quantity = quantity +'" + book.getQuantity() + "' WHERE author='" + res.getString("author") + "' AND title='" + res.getString("title") + "' AND price ='" + res.getBigDecimal("price") + "'";
                PreparedStatement bookStockQuantity = getConn().prepareStatement(sql);
                bookStockQuantity.executeUpdate();
                sql = "UPDATE ShoppingBasket SET quantity = quantity -'" + book.getQuantity() + "' WHERE author='" + book.getAuthor() + "' AND title='" + book.getTitle() + "' AND price ='" + book.getPrice() + "'";
                PreparedStatement updateBasketQuantity = getConn().prepareStatement(sql);
                updateBasketQuantity.executeUpdate();
                res = basket.executeQuery("select * from ShoppingBasket where title='" + book.getTitle() + "' AND author='" + book.getAuthor() + "' AND userid ='" + userId + "' AND price ='" + book.getPrice() + "'");
                if (res.getInt("quantity") == 0) {
                    sql = "DELETE FROM ShoppingBasket where title='" + book.getTitle() + "' AND author='" + book.getAuthor() + "' AND userid ='" + userId + "' AND price ='" + book.getPrice() + "'";
                    PreparedStatement preparedStatement = getConn().prepareStatement(sql);
                    preparedStatement.execute();
                }

            }
        }
        con.close();
    }

    public void deleteBookFromStock(Book book) throws Exception {
        String sql = "DELETE FROM bookStockTable where title='" + book.getTitle() + "' AND author='" + book.getAuthor() + "' AND price ='" + book.getPrice() + "'";
        PreparedStatement preparedStatement = getConn().prepareStatement(sql);
        preparedStatement.execute();
        con.close();
    }

    public static boolean isQuantityAvailable(int requestedQuantity, String author, String title, BigDecimal price) throws Exception {
        return availableQuantity(author, title, price) >= requestedQuantity;
    }

    public static int availableQuantity(String author, String title, BigDecimal price) throws Exception {
        Statement state = getConn().createStatement();
        ResultSet result = state.executeQuery("select * from bookStockTable where title='" + title + "' AND author='" + author + "' AND price ='" + price + "'");
        return result.next() ? result.getInt("quantity") : 0;
    }

    public static int bookQuantityInTheBasket(String author, String title, BigDecimal price) throws Exception {
        Statement state = getConn().createStatement();
        ResultSet result = state.executeQuery("select * from ShoppingBasket where title='" + title + "' AND author='" + author + "' AND price ='" + price + "'");
        return result.next() ? result.getInt("quantity") : 0;
    }

    public static boolean isBasketEmpty(String userId) throws Exception {
        Statement state = getConn().createStatement();
        ResultSet rs = state.executeQuery("select * from ShoppingBasket where userid='" + userId + "'");
        return rs.next() ? false : true;

    }

    public static void addBookToStock(Book book) throws Exception {
        if (isBookExistInStock(book.getAuthor(), book.getTitle(), book.getPrice())) {
            String sql = "UPDATE bookStockTable SET quantity = quantity +'" + book.getQuantity() + "' WHERE author='" + book.getAuthor() + "' AND title='" + book.getTitle() + "' AND price ='" + book.getPrice() + "'";
            PreparedStatement updateStockQuantity = getConn().prepareStatement(sql);
            updateStockQuantity.executeUpdate();
            updateStockQuantity.close();
            con.close();
        } else {
            PreparedStatement prep = getConn()
                    .prepareStatement("insert into bookStockTable values(?,?,?,?,?,?);");
            prep.setString(2, book.getTitle());
            prep.setString(3, book.getAuthor());
            prep.setString(4, book.getDescription());
            prep.setBigDecimal(5, book.getPrice());
            prep.setInt(6, book.getQuantity());
            prep.execute();
            prep.close();
            con.close();
        }
    }

    public static void addNewBookToUserBasket(Book book, String userId) throws Exception {
        Statement state = getConn().createStatement();
        ResultSet rs = state.executeQuery("select * from ShoppingBasket where title='" + book.getTitle() + "' AND author='" + book.getAuthor() + "' AND price ='" + book.getPrice() + "'");

        if (isQuantityAvailable(1, book.getAuthor(), book.getTitle(), book.getPrice())) {
            String sql = "UPDATE bookStockTable SET quantity = quantity -'" + book.getQuantity() + "' WHERE author='" + book.getAuthor() + "' AND title='" + book.getTitle() + "' AND price ='" + book.getPrice() + "' AND quantity > 0";
            PreparedStatement updateBookQuantity = getConn().prepareStatement(sql);
            updateBookQuantity.executeUpdate();
            updateBookQuantity.close();
            con.close();
            if (!rs.next()) {
                PreparedStatement prep = getConn()
                        .prepareStatement("insert into ShoppingBasket values(?,?,?,?,?,?,?);");
                prep.setString(2, book.getTitle());
                prep.setString(3, book.getAuthor());
                prep.setString(4, book.getDescription());
                prep.setBigDecimal(5, book.getPrice());
                prep.setInt(6, book.getQuantity());
                prep.setString(7, userId);
                prep.execute();
                prep.close();
                con.close();
            } else {
                sql = "UPDATE ShoppingBasket SET quantity = quantity +'" + book.getQuantity() + "' WHERE author='" + book.getAuthor() + "' AND title='" + book.getTitle() + "' AND price ='" + book.getPrice() + "'";
                PreparedStatement updateBasketQuantity = getConn().prepareStatement(sql);
                updateBasketQuantity.executeUpdate();
                updateBasketQuantity.close();
                con.close();
            }
        } else {
            System.out.println("The quantity is not available. " + "Available quantity is: " + availableQuantity(book.getAuthor(), book.getTitle(), book.getPrice()));
        }

    }

    public static void addBooksToOrdersTable(String userId) throws Exception {
        Statement state = getConn().createStatement();
        ResultSet result = state.executeQuery("select * from ShoppingBasket where userid='" + userId + "'");
        while (result.next()) {
            PreparedStatement prep = getConn()
                    .prepareStatement("insert into OrdersTable values(?,?,?,?,?,?,?);");
            prep.setString(2, result.getString("title"));
            prep.setString(3, result.getString("author"));
            prep.setString(4, result.getString("description"));
            prep.setBigDecimal(5, result.getBigDecimal("price"));
            prep.setInt(6, result.getInt("quantity"));
            prep.setString(7, userId);
            prep.execute();
        }
        Statement statement = getConn().createStatement();
        statement.executeUpdate("DELETE FROM ShoppingBasket where userid='" + userId + "'");
        con.close();
    }

    public static void addNewUser(User user) throws Exception {
        PreparedStatement prep = getConn()
                .prepareStatement("insert into UsersTable values(?,?,?,?,?,?,?);");
        prep.setString(2, user.getFirstname());
        prep.setString(3, user.getLastname());
        prep.setString(4, user.getAddress());
        prep.setString(5, user.getEmail());
        prep.setString(6, user.getPersonalnumber());
        prep.setString(7, user.getUserid());
        prep.execute();
        prep.close();
        con.close();
    }

    public static void addNewAdmin(Admin admin) throws Exception {
        PreparedStatement prep = getConn()
                .prepareStatement("insert into AdminTable values(?,?,?,?,?,?,?,?);");
        prep.setString(2, admin.getUsername());
        prep.setString(3, encryptPassword(key, initVector, admin.getPassword()));
        prep.setString(4, admin.getFirstname());
        prep.setString(5, admin.getLastname());
        prep.setString(6, admin.getAddress());
        prep.setString(7, admin.getEmail());
        prep.setString(8, admin.getPersonalnumber());
        prep.execute();
        prep.close();
        con.close();
    }

    public static ResultSet displaySetOfAllBooksInStock() throws Exception {
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select title, author, description, price, quantity from bookStockTable");
        return res;
    }

    public static ResultSet displaySetOfAllBooksInOrdersTable() throws Exception {
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select * from OrdersTable");
        return res;
    }

    private static void initialiseShoppingBasketTable() throws Exception {
        if (!hasShoppingBasketTable) {
            hasShoppingBasketTable = true;
            Statement TaskSt = getConn().createStatement();
            ResultSet Task = TaskSt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='ShoppingBasket'");

            if (!Task.next()) {
                Statement state2 = getConn().createStatement();
                state2.executeUpdate("create table ShoppingBasket(id integer,"
                        + "title varchar(60)," + "author varchar(60)," + "description varchar(60)," + "price numeric," + "quantity integer," + "userid varchar(60)," + "primary key (id));");
            }
        }
    }

    private static void initialiseBookStockTable() throws Exception {
        if (!hasBookStockTable) {
            hasBookStockTable = true;
            Statement bookStockSt = getConn().createStatement();
            ResultSet bookStock = bookStockSt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='bookStockTable'");

            if (!bookStock.next()) {
                Statement state2 = getConn().createStatement();
                state2.executeUpdate("create table bookStockTable(id integer,"
                        + "title varchar(60)," + "author varchar(60)," + "description varchar(60)," + "price numeric," + "quantity integer," + "primary key (id));");
            }

        }
    }

    private static void initialiseUsersTable() throws Exception {
        if (!hasUsersTable) {
            hasUsersTable = true;
            Statement usersSt = getConn().createStatement();
            ResultSet users = usersSt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='UsersTable'");

            if (!users.next()) {
                Statement state2 = getConn().createStatement();
                state2.executeUpdate("create table UsersTable(id integer,"
                        + "firstname varchar(60)," + "lastname varchar(60)," + "address varchar(60)," + "email varchar(60)," + "personalnumber integer," + "userid varchar(60)," + "primary key (id));");
            }
        }
    }

    private static void initialiseAdminTable() throws Exception {
        if (!hasAdminTable) {
            hasAdminTable = true;
            Statement adminSt = getConn().createStatement();
            ResultSet admin = adminSt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='AdminTable'");

            if (!admin.next()) {
                Statement state2 = getConn().createStatement();
                state2.executeUpdate("create table AdminTable(id integer,"
                        + "username varchar(60)," + "password varchar(60)," + "firstname varchar(60)," + "lastname varchar(60)," + "address varchar(60)," + "email varchar(60)," + "personalnumber varchar(60)," + "primary key (id));");
            }
        }
    }

    private static void initialiseOrdersTable() throws Exception {
        if (!hasOrdersTable) {
            hasOrdersTable = true;
            Statement adminSt = getConn().createStatement();
            ResultSet admin = adminSt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='OrdersTable'");

            if (!admin.next()) {
                Statement state2 = getConn().createStatement();
                state2.executeUpdate("create table OrdersTable(id integer,"
                        + "title varchar(60)," + "author varchar(60)," + "description varchar(60)," + "price numeric," + "quantity integer," + "userid varchar(60)," + "primary key (id));");
            }
        }
    }

    public static boolean isAdminPasswordCorrect(String adminUserName, String adminPassword) throws Exception {
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select * from AdminTable where username='" + adminUserName + "'");
        if (isAdminUserExist(adminUserName) && adminPassword.equals(decryptPassword(key, initVector, res.getString("password")))) {
            con.close();
            return true;
        } else {
            con.close();
            return false;
        }
    }

    public static boolean isAdminUserExist(String adminUserName) throws Exception {
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select * from AdminTable where username='" + adminUserName + "'");
        return res.next();
    }

    public static boolean isUserExist(String userId) throws Exception {
        boolean isUserExist;
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select * from UsersTable where userid='" + userId + "'");
        isUserExist = res.next();
        con.close();
        return isUserExist;
    }

    public static boolean isBookExistInStock(String authorName, String titleName, BigDecimal price) throws Exception {
        boolean isBookExist;
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select * from bookStockTable where author='" + authorName + "' AND title='" + titleName + "' AND price='" + price + "'");
        isBookExist = res.next();
        con.close();
        return isBookExist;
    }

    public ResultSet getAllAdmins() throws Exception {
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select * from AdminTable");
        return res;
    }

    public ResultSet getAllUsers() throws Exception {
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select * from UsersTable");
        return res;
    }

    public static ResultSet getAllBooksInBasketByUserId(String userId) throws Exception {
        Statement state = getConn().createStatement();
        ResultSet res = state.executeQuery("select * from ShoppingBasket where userid='" + userId + "'");
        return res;
    }

    public static BigDecimal getPriceOfAllBooksInBasketByUserId(String userId) throws Exception {
        BigDecimal totalPriceOfTheBasket = new BigDecimal("0.0");
        BigDecimal multiply;
        Statement state = getConn().createStatement();
        ResultSet priceAndQuantity = state.executeQuery("select * from ShoppingBasket where userid='" + userId + "'");
        while (priceAndQuantity.next()) {
            multiply = priceAndQuantity.getBigDecimal("price").multiply(new BigDecimal(priceAndQuantity.getInt("quantity")));
            totalPriceOfTheBasket = totalPriceOfTheBasket.add(multiply);
        }
        System.out.println("Price of all books in the basket of the user with userId " + userId + " is: " + totalPriceOfTheBasket);
        return totalPriceOfTheBasket;
    }

    public void cleanShoppingBasketTable(String userId) throws Exception {
        Statement basket = getConn().createStatement();
        ResultSet res = basket.executeQuery("select * from ShoppingBasket");
        while (res.next()) {
            String sql = "UPDATE bookStockTable SET quantity = quantity +'" + res.getInt("quantity") + "' WHERE author='" + res.getString("author") + "' AND title='" + res.getString("title") + "' AND price ='" + res.getBigDecimal("price") + "'";
            PreparedStatement updateBasketQuantity = getConn().prepareStatement(sql);
            updateBasketQuantity.executeUpdate();
        }
        Statement state = getConn().createStatement();
        state.executeUpdate("DELETE FROM ShoppingBasket where userid='" + userId + "'");
    }

    public void cleanUserTable() throws Exception {
        Statement state = getConn().createStatement();
        state.executeUpdate("DELETE FROM UsersTable");
        con.close();
    }

    public void cleanAdminTable() throws Exception {
        Statement state = getConn().createStatement();
        state.executeUpdate("DELETE FROM AdminTable");
        con.close();
    }

    public void cleanTheEntireShoppingBaskets() throws Exception {
        Statement state = getConn().createStatement();
        state.executeUpdate("DELETE FROM ShoppingBasket");
        con.close();
    }

    public void cleanOrdersTable() throws Exception {
        Statement state = getConn().createStatement();
        state.executeUpdate("DELETE FROM OrdersTable");
        con.close();
    }

    public static void cleanBookStockTable() throws Exception {
        Statement state = getConn().createStatement();
        state.executeUpdate("DELETE FROM bookStockTable");
        con.close();
    }

    public void deleteUser(String userId) throws Exception {
        Statement state = getConn().createStatement();
        state.executeUpdate("DELETE FROM UsersTable where userid='" + userId + "'");
        con.close();
    }

    public void deleteAdmin(String username) throws Exception {
        Statement state = getConn().createStatement();
        state.executeUpdate("DELETE FROM AdminTable where username='" + username + "'");
        con.close();
    }

    public void deleteTheEntireDb() throws Exception {
        cleanAdminTable();
        cleanUserTable();
        cleanBookStockTable();
        cleanTheEntireShoppingBaskets();
        cleanOrdersTable();
    }

    public static String encryptPassword(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Base64.encodeBase64String(encrypted);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String decryptPassword(String key, String initVector, String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

            byte[] original = cipher.doFinal(Base64.decodeBase64(encrypted));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

}
