# SQLHelper
## java class to manage access and operations on database.

SQLHelper requires a basic knowledge in SQL and in java `PreparedStatment` and `ResultSet`.

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

The class is devided into 5 parts:

- [Databse Operations](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#databse-operations).
- [Key Value Table Manager](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#key-value-table-manager).
- [Databse Table Manager](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#databse-table-manager).
- [Databse Connection Manager](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#database-connection-manager).
- [Databse Transaction Manager](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#database-transaction-manager).



## Databse Operations.
include [insert](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#insert) , [update](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#update) , [delete](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#delete) and [select](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#select) operations.

accessing Databse Operations using `op()` method from SQLHelper object:
```
try(SQLHelper sql = new SQLHelper(... databse url ...)){
    sql.op();
} catch(Exception e){
    
}
```

in the following part we will assume a table `items` with 3 columns : `id INT, name VARCHAR(45), price REAL`

### Insert:

to `insert` into database you need to get refrence to SQLHelperInsertStatment from `op()` method and provide the table name that you want to insert to, like follows:

`sql.op().insert("items")`

`SQLHelperInsertStatment` has the following methods:

- `setCols(String columns)`: specify the columns you wish to insert can be all columns or some, example: `setCols("id,name,price")`
- `setValues(Object... values)`: specify values for the columns in `setCols()` method in the same order, example: `setValues(10235,"toy",10.25)`
- `setValues(SQLHelperValue object)`: another way to specify values for the columns in `setCols()` method, will be discussed later.
- `setCol(String column,Object value)`: specify a column and it's value, example: `setCol("price",10.25)`
- `reset()`: reset all values and columns for all methods in `SQLHelperInsertStatment` instance.
- `execute()`: execute command and insert to the specified table and call `reset()`.

complete example to insert:
```
try(SQLHelper sql = new SQLHelper(... databse url ...)){
    // first way
    sql.op().insert("items").setCols("id,name,price").setValues(10235,"toy",10.25).execute();
    
    // second way
    sql.op().insert("items").setCol("id",10235).setCol("name","toy").setCol("price",10.25).execute();
    
    // third way (will be discussed later)
    Item item = new Item(10235,"toy",10.25);
    sql.op().insert("items").setCols("id,name,price").setValues(item).execute();
    
} catch(Exception e){
    
}
```
### Preparing class to be used with SQLHelper:
`setValues(SQLHelperValue object)` method is designed because most of values when retrieved from databse is stored in objects, so it's more convenient to insert an object and make the class get the values from it rather than getting the values one by one, **this works with [update](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#update) also**, here are the steps to make it work:

1. have a class implements SQLHelperValue interface.
2. implements `getSQLHelperValue` method in the class.

Example Item class:
```
public class item implements SQLHelperValue {
    int id;
    String name;
    double price;
    
    // ... getter and setter...
    
    public void getSQLHelperValue (String colName,int index,PreparedStatement ps) throws Exception{
        // make column to lower case because some databses uses upper case names.
        String col = colName.toLowerCase();
        switch (col) {
            case "id":
                ps.setInt(index, getId());
                break;
            case "name":
                ps.setString(index, getName());
                break;
            case "price":
                ps.setDouble(index, getPrice());
                break;
            default:
                throw new Exception("UNKNOWN column: " + col);
        }
    }
    
    public void setSQLHelperValue(String colName,ResultSet rs) throws Exception {}
}
```

now you can use Item object in `setValues()` method.

**Notes:**
- using multiple ways to set values will reset the other ways.
- if columns are not specified, then SQLHelper will get all columns from databse, **and they will be in the same order as in databse.**
- when doing multiable insert into single table consider saving a refrence to `SQLHelperInsertStatment` object instead of creating new one for each insert, consider the follwing example:
```
sql.op().insert("items").setCols("id,name,price").setValues(1023,"toy1",10.25).execute();
sql.op().insert("items").setCols("id,name,price").setValues(1024,"toy2",15.25).execute();
sql.op().insert("items").setCols("id,name,price").setValues(1025,"toy3",18.25).execute();
```
should be converted to:
```
SQLHelperInsertStatment insert = sql.op().insert("items");
insert.setCols("id,name,price").setValues(1023,"toy1",10.25).execute();
insert.setCols("id,name,price").setValues(1024,"toy2",15.25).execute();
insert.setCols("id,name,price").setValues(1025,"toy3",18.25).execute();
```

### Update:
to `update` to database you need to get refrence to SQLHelperUpdateStatment from `op()` method and provide the table name that you want to update, like follows:

`sql.op().update("items")`

`SQLHelperUpdateStatment` has the same structure as `SQLHelperInsertStatment` has, exept for `setCondition()` and `executeLarge()` methods, here the complete methods:

- `setCols(String columns)`: specify the columns you wish to update can be all columns or some, example: `setCols("id,name,price")`
- `setValues(Object... values)`: specify values for the columns in `setCols()` method in the same order, example: `setValues(10235,"toy",10.25)`
- `setValues(SQLHelperValue object)`: another way to specify values for the columns in `setCols()` method, as discussed in [Preparing class to be used with SQLHelper](https://github.com/SalehHaddawi/SQLHelper/blob/master/README.md#preparing-class-to-be-used-with-sqlhelper) part.
- `setCol(String column,Object value)`: specify a column and it's value, example: `setCol("price",10.25)`
- `setCondition(String condition,Object... values)`: condition to limit updating to specific record using SQL `WHERE` keyword, if no condition is specified all records will be updated, the sympol `?` is used instead of values in the condition, after the condition, you specify each value for `?` in condition seperated by comma, example: `setCondition("WHERE id = ?",1023)`.
- `reset()`: reset all values and columns for all methods in `SQLHelperUpdateStatment` instance.
- `execute()`: execute command and update the specified table and call `reset()`.
- `executeLarge()`: same as `execute()` method, used when the updated records are more than `Integer.MAX_VALUE`.

complete example to update:
```
try(SQLHelper sql = new SQLHelper(... databse url ...)){
    // first way (update name and price, only for records with id = 1023 and name = toy3)
    sql.op().update("items").setCols("name,price").setValues("toy",10.25).setCondition("WHERE id = ? and name = ?",1023,"toy3").execute();
    
    // second way (update price, only for records with id = 1023)
    sql.op().update("items").setCol("price",10.25).setCondition("WHERE id = ?",1023).execute();
    
    // third way (discussed in insert, update price, only for records with name = toy1 or name = toy2)
    Item item = new Item(10235,"toy",10.25);
    sql.op().update("items").setCols("price").setValues(item).setCondition("WHERE name = ? OR name = ?","toy1","toy2").execute();
    
} catch(Exception e){
    
}
```

**Notes:**
- using multiple ways to set values will reset the other ways.
- if columns are not specified, then SQLHelper will get all columns from databse, **and they will be in the same order as in databse.**
- when doing multiple update into single table consider saving a refrence to `SQLHelperUpdateStatment` object instead of creating new one for each update, consider the follwing example:
```
sql.op().update("items").setCols("name,price").setValues("toy1",10.25).setCondition("WHERE id = ?",1023).execute();
sql.op().update("items").setCols("name,price").setValues("toy2",15.25).setCondition("WHERE id = ?",1024).execute();
sql.op().update("items").setCols("name,price").setValues("toy3",18.25).setCondition("WHERE id = ?",1025).execute();
```
should be converted to:
```
SQLHelperUpdateStatment update = sql.op().update("items");
update.setCols("name,price").setValues("toy1",10.25).setCondition("WHERE id = ?",1023).execute();
update.setCols("name,price").setValues("toy2",15.25).setCondition("WHERE id = ?",1024).execute();
update.setCols("name,price").setValues("toy3",18.25).setCondition("WHERE id = ?",1025).execute();
```
you can search the internet to know more about SQL `WHERE` Clause.

### Delete:


### Select:


## Key Value Table Manager.


## Databse Table Manager.


## Databse Connection Manager.


## Databse Transaction Manager.
