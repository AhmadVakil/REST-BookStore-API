# Spring Boot REST API

This project represent a book store or a backend system that responds to POST/GET requests. This book store developed together with its RESTful API and supports requests/responds. This project can be used as a good backend demonstration for e-commerce. Once you have this backend up and running, then you can spend some time to develope the frontend/view which is really entertaining. Just follow the instructions and you will be fine.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

Make sure you have Apache Maven installed and you have JDK ready for the compilation purposes, you will also need a REST client application such as Postman or Advanced REST client.

Clone the repository to your local machine and navigate to the root directory of the project.

Run the following command in the root directory in order to package the application and to create Jar files.
```
mvn -f server-pom.xml clean
mvn -f server-pom.xml package 
mvn -f commandline-pom.xml package
```
<img src="/src/tutorials/mvn-clean-package-bookstore.png">

Now you will have two Jar files in the target directory which one of them belongs to the server and the other one belongs to the commandline application.

In order to run both Jar files, navigate to target directory and run the following commands in two separate terminal, shell or Git Bash.
Consider that the target directory will be created when you use package command for a pom and will be deleted with clean command.
```
java -jar server-1.0-RELEASE.jar
```
<img src="/src/tutorials/mvn-java-server.png">

```
java -jar commandline-1.0-RELEASE.jar
```
<img src="/src/tutorials/mvn-java-commandline.png">

Now you have both the server and the commandline up and running. You will also have a database file called BookShop.db which store all the data.

##### Do you have compilation problem?
In case if you had compilation error please add the following plugin in both commandline-pom and server-pom and redo all the above commands again to run the application.
```
<plugin>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.1</version>
    <configuration>
        <fork>true</fork>
        <executable>PATH_TO_YOUR_JDK\bin\javac.exe</executable>
    </configuration>
</plugin>
```

### Important Prerequisites 
Now you have the application ready to work, but inorder to work with the application you need to have "user account" to buy books and manage baskets and also an "admin account" to manage the shop.

No worries!
You can create accounts with both the command line and also through the POST/GET request which is discussed further.

### Commandline

Use the command line tool to have full control on the shop as a customer/user or as an admin to manage the shop. Here is how the commandline is look like.
```
Please choose one of the following options:
	1- Login with userId and manage shopping baskets.
	2- Login with admin privilege and manage the shop.
	3- Create user account.
	4- Create admin account.
	5- Exit

Enter your option: 
```

Before you begin using the commandline, make sure that you have created at least a user account and later on to see all users, see all orders and to add new book to the stock, create an admin account.

### POST/GET 

Almost all the POST and GET request comes with a respond, message and boolean values which will also help the Front-end developer in the development process.

We have two controller, one belongs to the shopping basket and the other one belongs to the store.

In order to simplify the use cases, when you run the application for the first time a default user account, admin account and also books will be added to the store. These defaults data are:

Default userId: `Dummy_User` 

Default username for admin: `Dummy_Admin` and the password: `Dummy`

Default books are hosting from my server in this address: https://www.webriders.se/other/bookstoredata.txt
 
The server will running on http://localhost:8080/, so the **_`POST`_** and **_`GET`_** requests for the shopping basket are as follows:

Start by adding a book to the Dummy_User basket.
You can only add books that are already available in the stock, the quantity should be also less or equal to the the available quantity of the shop.
If there are no books available in the stock, rerun the application to reload the stock as default or add book through admin account.

#### Shopping Basket

##### To `add (POST) book` to the shopping basket of a user:

```
{
"title": "Mastering åäö",
"author": "Average Swede",
"price": 762,
"quantity": 2,
"description": "Dummy description",
"bookId": 0
}
```

Note that the `quantity` here means the quantity that we want to add to the basket.
So, post the above JSON object to the following address:

http://localhost:8080/addBookToBasket/userId=Dummy_User

##### To `GET all books in the basket` use the following address and use GET request:

http://localhost:8080/showBooksInUserBasket/userId=Dummy_User

##### To `GET the price of the basket` use the following address and use GET request:

http://localhost:8080/totalPriceOfTheBasket/userId=Dummy_User

##### To `Delete a specific book` from the basket use the following address and `POST` the book that is already exist in the basket:


```
{
"title": "Mastering åäö",
"author": "Average Swede",
"price": 762,
"quantity": 1,
"description": "Dummy description",
"bookId": 0
}
```

Note that the `quantity` here is the quantity that we want to delete from the basket.

POST the above JSON object to the following address to delete the book from the basket:

http://localhost:8080/deleteFromBasket/userId=Dummy_User

##### To `Delete all books` from the basket send a POST request to the following address:

http://localhost:8080/deleteAllBooksFromBasket/userId={userId}

##### To `Search for a book` in the stock, use the following link and replace the searchString with your preferred phrase:

http://localhost:8080/search={searchString}

##### To `Get book quantity` from the stock, use the following link and POST the book details to get the quantity as a respond:

http://localhost:8080/getBookQuantity

```
{
"title": "Mastering åäö",
"author": "Average Swede",
"price": 762
}
```

It is enough to just send the title, author and price since these values will specify a unique book.

##### To `Buy all books` from the basket, use the following address and with a POST request:

http://localhost:8080/buyBooks/userId=Dummy_User


#### Book Store

##### To `Get all the books` from the stock, use the following link with a GET request:

http://localhost:8080/bookStock

##### To `Get orders list` use the following address with a GET request:

http://localhost:8080/bookStock/showOrders

##### To `Get all admins` use the following address with a GET request:

http://localhost:8080/bookStock/showAdmins

##### To `Get all users` use the following address with a GET request:

http://localhost:8080/bookStock/showUsers

##### To `Check if admin exist` use the following address with a GET request

http://localhost:8080/bookStock/isAdminExist/adminUserName=Dummy_Admin

##### To `Check if user exist` use the following address with a GET request

http://localhost:8080/bookStock/isUserExist/userId=Dummy_User

##### To `Add new book` to the stock, use the following address and POST a book object, if the book is already exist, then only the quantity will be updated otherwise a new book will be added.

http://localhost:8080/bookStock/addBook

##### To `Check if a book exist` use the following address and POST the book object.

http://localhost:8080/bookStock/isBookExist

##### To `Add a new admin account` POST the admin object to the following address:
 
http://localhost:8080/bookStock/addNewAdmin

Example of an admin object is as following:

```
{
"username": "admin_user",
"password": "admin_pass",
"firstname": "admin_name",
"lastname": "admin_lastname",
"address": admin_address,
"email": admin_email,
"personalnumber": 123456
}
```

The password will be encrypted in the backend and will be stored in the database. Also there is a decryption algorithm when we want to decrypt the password.
If the admin does not exist the new admin will be added, otherwise the request will be rejected.

Security vulnerable?
In real implementation the site needs to be https to avoid attackers sniffing the password.
Also, all requests to server can use a token. The token is stored as cookie on user's machine and can have expiry time for session invalidation.

##### To `Check if the admin password is correct` use the following address and POST the user name and password:

http://localhost:8080/bookStock/isAdminPasswordCorrect

```
{
"username": "admin_user",
"password": "admin_pass"
}
```

##### To `Add a new user` use the following address and POST the user object to the server:

http://localhost:8080/bookStock/addNewUser

Example of a user object:
```
 {
"firstname": "user_firstname",
"lastname": "user_lastname",
"address": user_address,
"email": user_email,
"userid": "user_uniqueId",
"personalnumber": 1234
}
```

## Running the tests

This project is using SQLite database, so for the testing purposes a new database (BookShopTest.db) will be created for only tests.

Run the following Maven command to test:

mvn test


## Licence

MIT

## Contribute

Contributors are welcome! :)
