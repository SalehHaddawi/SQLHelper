# SQLHelper
java class to manage access and operations on database.

This class needs a JDBC driver.

How to use it (example using SQLite):

```
try(SQLHelper sql = new SQLHelper("jdbc:sqlite:my database.db")){

} catch(Exception e){

}
```
 **SQLHelper have three `constructors`:**
 
 - databse url only:
 ```
new SQLHelper("jdbc:sqlite:my database.db")
```

- databse url and username and password:
 ```
new SQLHelper("jdbc:oracle:thin:@localhost:1521:xe","username","password")
```

- databse url and connection `properties`:
 ```
Properties properties = new Properties();
properties.setProperty("user", "USERNAME");
properties.setProperty("password", "PASSWORD");
properties.setProperty("serverTimezone", "UTC");

new SQLHelper("jdbc:mysql://127.0.0.1:3306/testing",properties)
```
