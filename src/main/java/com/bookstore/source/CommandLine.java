package com.bookstore.source;

import com.bookstore.source.entity.Admin;
import com.bookstore.source.entity.Book;
import com.bookstore.source.entity.Order;
import com.bookstore.source.entity.User;
import com.bookstore.source.service.BookStockService;
import com.bookstore.source.service.ShoppingBasketService;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class CommandLine extends ShoppingBasketService {

    public static void main(String[] args) throws Exception {

        Map<String, Object> shopResponse;
        boolean run = true;
        ShoppingBasketService basketService = new ShoppingBasketService();
        BookStockService shopService = new BookStockService();

        Scanner lineReader = new Scanner(new InputStreamReader(
                System.in, Charset.forName("UTF-8")));

        while (run) {
            int loginAttempt = 0;
            String option;
            System.out.println("Please choose one of the following options:");
            System.out.println("\t1- Login with userId and manage shopping baskets.");
            System.out.println("\t2- Login with admin privilege and manage the shop.");
            System.out.println("\t3- Create user account.");
            System.out.println("\t4- Create admin account.");
            System.out.println("\t5- Exit.\n");
            System.out.print("Enter your option: ");
            option = lineReader.nextLine();
            while (true) {
                if (option.equals("0")) {
                    break;
                } else if (option.equals("1")) {
                    System.out.print("\nPlease enter the user ID of the customer or choose 0 to exit: ");
                    String userId = lineReader.nextLine();
                    if (userId.equals("0")) {
                        break;
                    }
                    shopResponse = shopService.isUserExist(userId);
                    boolean isUserExist = (Boolean) shopResponse.get("isUserExist");
                    System.out.println(shopResponse.get("message"));
                    while (isUserExist) {
                        System.out.println("Welcome " + userId + "!");
                        System.out.println("Please choose one of the following options:");
                        System.out.println("\t1- Show all books in the stock.");
                        System.out.println("\t2- Show all books in the basket.");
                        System.out.println("\t3- Add book to the basket from the stock list.");
                        System.out.println("\t4- Delete books from the basket list.");
                        System.out.println("\t5- Delete the entire basket.");
                        System.out.println("\t6- Search for books.");
                        System.out.println("\t7- Buy all books from the basket");
                        System.out.println("\t8- Logout to previous menu.");
                        System.out.println("Enter your option:");
                        option = lineReader.nextLine();
                        if (option.equals("1")) {
                            List<Book> books = shopService.getAllBooks();
                            int rowNumber = 0;
                            for (Book book : books) {
                                rowNumber++;
                                System.out.println(rowNumber + "- " + book.toString());
                            }
                        } else if (option.equals("2")) {
                            List<Book> books = basketService.getAllBooksByUserId(userId);
                            int rowNumber = 0;
                            for (Book book : books) {
                                rowNumber++;
                                System.out.println(rowNumber + "- " + book.toString());
                            }
                            System.out.println("Total price of the basket: " + basketService.getPriceOfTheBasket(userId).get("totalPriceOfTheBasket"));
                        } else if (option.equals("3")) {
                            List<Book> books = shopService.getAllBooks();
                            int choice = 0;
                            String isOutOfStock;
                            System.out.println("Please choose the book number to add or choose 0 to exit:");
                            for (Book book : books) {
                                choice++;
                                isOutOfStock = book.getQuantity() == 0 ? "OUT OF STOCK ===> " : "";
                                System.out.println(isOutOfStock + choice + "- " + book.toString());
                            }

                            try {
                                System.out.println("Enter the book number to add or 0 to exit:");
                                String bookChoice = lineReader.nextLine();
                                int intValue = Integer.parseInt(bookChoice);

                                if (intValue == 0) {
                                    System.out.println("Returning to main menu...");
                                } else if (intValue > 0 && intValue <= choice) {
                                    System.out.println("Please choose the quantity:");
                                    int quantity = Integer.parseInt(lineReader.nextLine());
                                    if (quantity > 0) {
                                        choice = 0;
                                        for (Book book : books) {
                                            choice++;
                                            if (choice == intValue) {
                                                book.setQuantity(quantity);
                                                shopResponse = basketService.addNewBookToUserBasket(book, userId);
                                                System.out.println(shopResponse.get("message"));
                                            }
                                        }
                                    } else {
                                        System.out.println("Quantity can't be 0 or negative.");
                                    }
                                } else {
                                    System.out.println("Incorrect input!");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Input is not a valid integer");
                            }

                        } else if (option.equals("4")) {
                            List<Book> books = basketService.getAllBooksByUserId(userId);
                            if (!basketService.isBasketEmpty(userId)) {
                                int choice = 0;
                                System.out.println("Please choose the book number to delete or choose 0 to exit:");
                                for (Book book : books) {
                                    choice++;
                                    System.out.println(choice + "- " + book.toString());
                                }

                                try {
                                    System.out.println("Enter the book number to delete or press enter to exit:");
                                    int intValue = Integer.parseInt(lineReader.nextLine());

                                    System.out.println("Enter the quantity to delete:");
                                    int quantityToDelete = Integer.parseInt(lineReader.nextLine());

                                    if (intValue == 0) {
                                        System.out.println("Returning to main menu...");
                                    } else if (intValue > 0 && intValue <= choice && quantityToDelete > 0) {
                                        choice = 0;
                                        for (Book book : books) {
                                            choice++;
                                            if (choice == intValue && basketService.bookQuantityInTheBasket(book).get("bookQuantityInTheBasket") >= quantityToDelete) {
                                                book.setQuantity(quantityToDelete);
                                                basketService.deleteBookFromShoppingBasket(book, userId);
                                            } else {
                                                System.out.println("The requested quantity is more than the available quantity.");
                                                break;
                                            }
                                        }
                                    } else {
                                        System.out.println("Incorrect input!");
                                    }
                                } catch (NumberFormatException e) {
                                    System.out.println("Input is not a valid integer");
                                }
                            } else {
                                System.out.println("There is no book in the basket to delete.");
                            }
                        } else if (option.equals("5")) {
                            System.out.println("Are you sure to delete all books from user(" + userId + ") basket? (Y/N)");
                            String answer = lineReader.nextLine();
                            if (answer.equals("Y") || answer.equals("y")) {
                                basketService.deleteAllBooksFromBasket(userId);
                            }
                        } else if (option.equals("6")) {
                            System.out.println("Searching books in the stock.\nPlease enter the phrase or press enter to list all books:");
                            List<Book> books = basketService.list(lineReader.nextLine());
                            int counter = 0;
                            for (Book book : books) {
                                counter++;
                                System.out.println(counter + "- " + book.toString());
                            }
                        } else if (option.equals("7")) {
                            System.out.println("Are you sure you want to buy books? (Y/N)");
                            String answer = lineReader.nextLine();
                            if (answer.equals("Y") || answer.equals("y")) {
                                shopResponse = basketService.buyBooks(userId);
                                System.out.println(shopResponse.get("message"));
                            }
                        } else if (option.equals("8")) {
                            System.out.println("By exiting you will lose your items in your shopping basket.");
                            System.out.println("Are you sure you want to log out? (Y/N)");
                            String answer = lineReader.nextLine();
                            if (answer.equals("Y") || answer.equals("y")) {
                                basketService.deleteAllBooksFromBasket(userId);
                                option = "0";
                                break;
                            }
                        } else {
                            System.out.println("Incorrect input!");
                        }
                    }
                } else if (option.equals("2")) {
                    loginAttempt++;
                    if (loginAttempt > 3) {
                        System.out.println("WARNING! Too much attempt to login.\nIF YOU ARE AN UNAUTHORIZED USER, EXIT IMMEDIATELY!");
                    }
                    System.out.println("\nLogging in as admin.\n(Type 'exit' to exit)\nEnter Username:");
                    String adminUserName = lineReader.next();
                    if (adminUserName.equals("exit")) {
                        System.out.println("Exiting to previous menu.");
                        loginAttempt = 0;
                        option = "0";
                        break;
                    }
                    shopResponse = shopService.isAdminExist(adminUserName);
                    if ((Boolean) shopResponse.get("isAdminExist")) {
                        System.out.println("Enter password:");
                        String adminPassword = lineReader.next();
                        lineReader.nextLine();
                        shopResponse = shopService.isAdminPasswordCorrect(adminUserName, adminPassword);
                        System.out.println(shopResponse.get("message"));
                        if ((Boolean) shopResponse.get("isAdminPasswordCorrect")) {
                            while (true) {
                                System.out.println("Welcome " + adminUserName + "!");
                                System.out.println("Please choose one of the following options:");
                                System.out.println("\t1- Show all books in the stock.");
                                System.out.println("\t2- Add new book to the stock.");
                                System.out.println("\t3- View out of stock books.");
                                System.out.println("\t4- View all users.");
                                System.out.println("\t5- View all orders.");
                                System.out.println("\t6- Return to previous menu.");
                                option = lineReader.next();
                                if (option.equals("1")) {
                                    List<Book> books = shopService.getAllBooks();
                                    int rowNumber = 0;
                                    for (Book book : books) {
                                        rowNumber++;
                                        System.out.println(rowNumber + "- " + book.toString());
                                    }
                                } else if (option.equals("2")) {
                                    lineReader.nextLine();
                                    Book book = new Book();
                                    System.out.println("Adding new book to stock...");
                                    System.out.println("Enter the title:");
                                    book.setTitle(lineReader.nextLine());
                                    System.out.println("Enter the author name:");
                                    book.setAuthor(lineReader.nextLine());
                                    System.out.println("Enter the price:");
                                    try {
                                        book.setPrice(new BigDecimal(lineReader.nextLine()));
                                        System.out.println("Enter the description:");
                                        book.setDescription(lineReader.nextLine());
                                        try {
                                            System.out.println("Enter the quantity:");
                                            int intValue = Integer.parseInt(lineReader.nextLine());
                                            while (intValue <= 0) {
                                                System.out.println("Enter the quantity:");
                                                intValue = Integer.parseInt(lineReader.nextLine());
                                            }
                                            book.setQuantity(intValue);
                                            shopResponse = shopService.addNewBookToStock(book);
                                            System.out.println(shopResponse.get("message"));
                                        } catch (NumberFormatException e) {
                                            System.out.println("Error: The quantity should be an integer value.");
                                        }
                                    } catch (NumberFormatException e) {
                                        System.out.println("Error: The price should be decimal value.");
                                    }

                                } else if (option.equals("3")) {
                                    List<Book> books = shopService.getAllBooks();
                                    int counter = 0;
                                    for (Book book : books) {
                                        if (book.getQuantity() == 0) {
                                            counter++;
                                            System.out.println(counter + "- " + book.toString());
                                        }
                                    }
                                } else if (option.equals("4")) {
                                    List<User> users = shopService.getAllUsers();
                                    int counter = 0;
                                    for (User user : users) {
                                        counter++;
                                        System.out.println(counter + "- " + user.toString());
                                    }
                                } else if (option.equals("5")) {
                                    List<Order> orders = shopService.getAllBooksFromOrdersTable();
                                    int counter = 0;
                                    for (Order order : orders) {
                                        counter++;
                                        System.out.println(counter + "- " + order.toString());
                                    }
                                } else if (option.equals("6")) {
                                    System.out.println("Logging out...");
                                    option = "0";
                                    break;
                                }
                            }
                        }
                    } else {
                        System.out.println(shopResponse.get("message"));
                    }

                } else if (option.equals("3")) {
                    System.out.println("Creating a new user account. Press enter to continue.");
                    lineReader.nextLine();
                    User newUser = new User();
                    System.out.println("Enter your first name:");
                    newUser.setFirstname(lineReader.nextLine());
                    System.out.println("Enter your last name:");
                    newUser.setLastname(lineReader.nextLine());
                    System.out.println("Choose a user Id(It will be used for logging):");
                    String userId = lineReader.nextLine();
                    shopResponse = shopService.isUserExist(userId);
                    while ((Boolean) shopResponse.get("isUserExist")) {
                        System.out.println("The user ID exist in the system, please try another one");
                        userId = lineReader.nextLine();
                        shopResponse = shopService.isUserExist(userId);
                    }
                    newUser.setUserid(userId);
                    System.out.println("Enter your email address:");
                    newUser.setEmail(lineReader.nextLine());
                    System.out.println("Enter your personal number:");
                    newUser.setPersonalnumber(lineReader.nextLine());
                    System.out.println("Enter your address:");
                    newUser.setAddress(lineReader.nextLine());
                    shopResponse = shopService.addNewUser(newUser);
                    System.out.println(shopResponse.get("message"));
                    option = "0";
                } else if (option.equals("4")) {
                    System.out.println("Creating a new admin account. Press enter to continue.");
                    lineReader.nextLine();
                    Admin newAdmin = new Admin();
                    System.out.println("Choose a username:");
                    String adminUsername = lineReader.nextLine();
                    shopResponse = shopService.isAdminExist(adminUsername);
                    while ((Boolean) shopResponse.get("isAdminExist")) {
                        System.out.println("The username exist in the system, please try another one");
                        adminUsername = lineReader.nextLine();
                        shopResponse = shopService.isAdminExist(adminUsername);
                    }
                    newAdmin.setUsername(adminUsername);
                    System.out.println("Please choose a password:");
                    newAdmin.setPassword(lineReader.nextLine());
                    System.out.println("Enter your first name:");
                    newAdmin.setFirstname(lineReader.nextLine());
                    System.out.println("Enter your last name:");
                    newAdmin.setLastname(lineReader.nextLine());
                    System.out.println("Enter your email address:");
                    newAdmin.setEmail(lineReader.nextLine());
                    System.out.println("Enter your personal number:");
                    newAdmin.setPersonalnumber(lineReader.nextLine());
                    System.out.println("Enter your address:");
                    newAdmin.setAddress(lineReader.nextLine());
                    shopResponse = shopService.addNewAdmin(newAdmin);
                    System.out.println(shopResponse.get("message"));
                    option = "0";
                } else if (option.equals("5")) {
                    shopService.deleteTheEntireDb();
                    run = false;
                    lineReader.close();
                    System.out.println("Exiting....");
                    break;
                } else {
                    option = "0";
                    System.out.println("Incorrect input.");
                }
            }
        }
    }

}
