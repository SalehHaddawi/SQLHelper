# # # SQLHelper
java class to manage access and operations on database.

This class needs a JDBC driver.

How to use it (example using SQLite):

```
try(SQLHelper sql = new SQLHelper("jdbc:sqlite:my database.db")){

} catch(Exception e){

}
```
