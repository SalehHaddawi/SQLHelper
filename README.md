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
new SQLHelper("jdbc:sqlite:my database.db","username","password")
```

- databse url and connection `properties`:
 ```
new SQLHelper("jdbc:sqlite:my database.db",properties)
```
