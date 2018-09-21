package com.nsma;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Saleh Haddawi
 */
public class SQLHelper implements AutoCloseable {

    //the connection to the databse
    private Connection connection = null;

    //data base URL
    private String DB_URL = "";

    public Connection getConnection() {
        return connection;
    }

    public SQLHelper(String URL) {
        DB_URL = URL;
    }

    public void registerDriver(Driver driver) {
        try {
            DriverManager.registerDriver(driver);
        } catch (SQLException e) {
            System.err.println("[" + Thread.currentThread().getStackTrace()[1].getClassName() + " Error] -> " + Thread.currentThread().getStackTrace()[2] + " " + e.getMessage());
        }
    }

    /**
     * get the provided path to the database.
     *
     * @return the current value of the database path, default is Empty String.
     */
    public String getDBPath() {
        return DB_URL;
    }

    /**
     * connect to the data base with provided DB Path<br><br>
     * <br> this method must be called before any operation on database
     * <br> If connection is already opened it will be closed and new one will
     * be created.
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not connect and it will be
     * displayed in the output:<br>
     * <br> 1- DB path is empty or null.
     * <br> 2- DB path is invalid.
     * <br> 3- could not find driver (library).
     * <br> 4- Error while trying to connect with the database.
     *
     */
    public void open() throws SQLException {
        if (getDBPath().trim().isEmpty()) {
            throw new SQLHelperExeption("can't connect to database location is not specified");
        }

        close();

        connection = null;
        // create a connection to the database
        connection = DriverManager.getConnection(DB_URL);
        System.out.println("Connection to [" + DB_URL + "] has been started.");
    }

    /**
     * connect to the data base with provided DB Path and username and
     * password<br>if the database support username and password
     * <br> this method must be called before any operation on database
     * <br> If connection is already opened it will be closed and new one will
     * be created.
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not connect and it will be
     * displayed in the output:<br>
     * <br> 1- DB path is empty or null.
     * <br> 2- DB path is invalid.
     * <br> 3- could not find driver (library).
     * <br> 4- Error while trying to connect with the database.
     *
     * @param username database username
     * @param password database password
     *
     */
    public void open(String username, String password) throws SQLException {
        if (getDBPath().trim().isEmpty()) {
            throw new SQLHelperExeption("can't connect to database location is not specified");
        }

        close();

        connection = null;

        // create a connection to the database
        connection = DriverManager.getConnection(DB_URL, username, password);
        System.out.println("Connection to [" + DB_URL + "] has been started.");
    }

    /**
     * close connection to the database and release any resources.
     * <br> This method has no effect if connection is already closed or hasn't
     * been started.
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not disconnect and it will be
     * displayed in the output:<br>
     * <br>1- Error while trying to close connection to database.
     */
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection to [" + DB_URL + "] has been closed.");
        }
    }

    /**
     * commit (confirm) any database changes in a transaction that haven't been
     * commited.
     * <br> A transaction is the spread of one or more changes to the database.
     * For example, if you are creating, updating, or deleting a record from the
     * table, then you are performing transaction on the table. It is important
     * to control transactions to ensure data safety and to handle database
     * errors.
     * <br> transaction group operations together so if one fail all will fail.
     * <br> to start transaction call the method <b>begin().</b>
     * <br> if have many insert commands begin transaction by calling
     * <b>begin()</b> and after the last insert call <b>commit()</b> to send to
     * dtabase it will be alot faster.
     * <br> the default for database is to create transaction for every command
     * and then commit afetr it finished.
     * <br><br> <b>Example:</b>
     * <br> SQLiteHelper db = new SQLiteHelper("test database.db");
     * <br> db.connect();
     * <br> db.begin(); //start transaction
     * <br> db.insert ...
     * <br> db.insert ...
     * <br> // many inserts
     * <br> db.delete...
     * <br> db.update...
     * <br> // if any error happend before commit no changes will be made to
     * database
     * <br> db.commite(); //end transaction
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not commit and it will be
     * displayed in the output:<br>
     * <br> 1- if the connection to the database is closed.
     * <br> 2- if you call commit() before begin().
     * <br> 3- Error happend while trying to commit transaction.
     */
    public void commit_Transaction() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't commit transaction Must Call open() before that, connection is closed");
        }
        if (connection.getAutoCommit()) {
            throw new SQLHelperExeption("can't commit transaction must call begin_Transaction() before commit_Transaction()");
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * start transaction.
     * <br> A transaction is the spread of one or more changes to the database.
     * For example, if you are creating, updating, or deleting a record from the
     * table, then you are performing transaction on the table. It is important
     * to control transactions to ensure data safety and to handle database
     * errors.
     * <br> it always better to commit or rollback transacton before disconnect
     * from connection.
     * <br> the default for database is to create transaction for every command
     * and then commit afetr it finished.<b>calling begin() make database wait
     * until you commit or rollback.</b>
     * <br><br> <b>Example:</b>
     * <br> SQLiteHelper db = new SQLiteHelper("test database.db");
     * <br> db.begin(); //start transaction
     * <br> db.insert ...
     * <br> db.insert ...
     * <br> // many inserts
     * <br> db.delete...
     * <br> db.update...
     * <br> // if any error happend before commit no changes will be made to
     * database
     * <br> db.commite(); //end transaction
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not commit and it will be
     * displayed in the output:<br>
     * <br> 1- if the connection to the database is closed.
     * <br> 2- if you call begin() again without commit.
     * <br> 3- Error happend while trying to begin transaction.
     */
    public void begin_Transaction() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't begin transaction Must Call open() before that, connection is closed");
        }
        if (!connection.getAutoCommit()) {
            throw new SQLHelperExeption("can't begin transaction, transaction already begun");
        }
        connection.setAutoCommit(false);

    }

    /**
     * cancel transaction.
     * <br> A transaction is the spread of one or more changes to the database.
     * For example, if you are creating, updating, or deleting a record from the
     * table, then you are performing transaction on the table. It is important
     * to control transactions to ensure data safety and to handle database
     * errors.
     * <br> it always better to commit or rollback transacton before disconnect
     * from connection.
     * <br><br> <b>Example:</b>
     * <br> SQLiteHelper db = new SQLiteHelper("test database.db");
     * <br> db.begin(); //start transaction
     * <br> try{
     * <br> db.insert ...
     * <br> db.insert ...
     * <br> // many inserts
     * <br> db.delete...
     * <br> db.update...
     * <br> // if any error happend before commit no changes will be made to
     * database
     * <br> db.commite(); //submit and end transaction
     * <br> }catch(Exeption e){
     * <br> db.roolback(); // something wrong happend cancel changes
     * <br> }
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not commit and it will be
     * displayed in the output:<br>
     * <br> 1- if the connection to the database is closed.
     * <br> 2- if you call begin() again without rollback.
     * <br> 3- Error happend while trying to rollback transaction.
     */
    public void rollBack_Transaction() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't rollback transaction Must Call open() before that, connection is closed");
        }
        if (connection.getAutoCommit()) {
            throw new SQLHelperExeption("can't rollback transaction must call begin_Transaction() before rollBack_Transaction()");
        }
        connection.rollback();
        connection.setAutoCommit(true);
    }

    /**
     * indicate if transaction is not going.
     *
     * @return true if there is no transaction going false otherwise.
     */
    public boolean isAutoCommit() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't check isAutoCommit Must Call open() before that, connection is closed");
        }
        return connection.getAutoCommit();
    }

    /**
     * check if the connection is closed.
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will return true and it will be
     * displayed in the output:<br>
     * <br> 1- if any error happend while checking.
     *
     * @return true if the connection is closed or any error happend during
     * checking false otherwise.
     */
    public boolean isConnectionClosed() throws SQLException {
        return connection.isClosed();
    }

    /**
     * delete the table and all of it's content,<br>if the table didn't exists
     * this method have no effect.
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not drop the table and it will
     * be displayed in the output:<br>
     * <br>1- if the connection is closed.
     * <br>2- if the tableName is null or empty.
     *
     * @param tableName the name of the table to be deleted can't be empty or
     * null;
     */
    public void dropTableIfExists(String tableName) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't drop table Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new SQLHelperExeption("table name can't be empty or null");
        }
        try (PreparedStatement ps = connection.prepareStatement("DROP TABLE IF EXISTS " + tableName)) {
            ps.executeUpdate();
        }
    }

    /**
     * delete the index if it's exists.
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not drop the index and it will
     * be displayed in the output:<br>
     * <br>1- if the connection is closed.
     * <br>2- if the indexName is null or empty.
     *
     * @param indexName the index name can't be null or empty.
     */
    public void dropIndexIfExists(String indexName) throws SQLException {

        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't create drop unique index Must Call open() before that, connection is closed");
        }
        if (indexName == null || indexName.trim().isEmpty()) {
            throw new SQLHelperExeption("index can't be empty or null");
        }
        String sqlS = "DROP INDEX IF EXISTS " + indexName;
        // create a new table
        try (PreparedStatement ps = connection.prepareStatement(sqlS)) {
            // create a new table
            ps.executeUpdate();
        }
    }

    /**
     * create index on specified column if not exists.
     * <br> An index is an additional data structure that helps speed up
     * querying
     * <br> Each index must be associated with a specific table. An index
     * consists of one or more columns, but all columns of an index must be in
     * the same table. A table may have multiple indexes.
     * <br> In case you want to make sure that the value of the column is unique
     * like email, phone, etc., you use the UNIQUE option in the CREATE INDEX
     * statement.
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not create the index and it
     * will be displayed in the output:<br>
     * <br>1- if the connection is closed.
     * <br>2- if the indexName or tableName or columnName is null or empty.
     *
     * @param tableName the table name the index will be in,can't be null or
     * empty.
     * @param columnName the column that the index will effect can't be null or
     * empty.
     * @param indexName the index name,any name will be fine.
     * @param isUnique is it a unique index?
     */
    public void createIndexIfNotExists(String tableName, String columnName, String indexName, boolean isUnique) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't create new unique index Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty() || indexName == null || indexName.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or column or index can't be empty or null");
        }
        StringBuilder sqlS = new StringBuilder(50);
        sqlS.append("CREATE ").append(isUnique ? "UNIQUE" : "").append(" INDEX IF NOT EXISTS ").append(indexName).append(" ON ").append(tableName).append(" (").append(columnName).append(")");
        // create a new table
        try (PreparedStatement ps = connection.prepareStatement(sqlS.toString())) {
            // create a new table
            ps.executeUpdate();
        }
    }

    /**
     * create new table if not exists.
     * <br> SQLite have many value types,we will show some java value types and
     * corresponding SQLite value types:
     * <br> JAVA -{@literal >} SQL
     * <br> String -{@literal >} STRING,TEXT,VARCHAR
     * <br> byte,short,int,long -{@literal >} INTEGER
     * <br> float,double -{@literal >} REAL
     * <br> boolean -{@literal >} BOOLEAN
     * <br> null -{@literal >} NULL
     * <br><b>Example:</b>
     * <br> SQLiteHelper db = new SQLiteHelper();
     * <br> db.createNewTableIfNotExists("students","name STRING,id
     * STRING,serial_num INTEGER,gpa REAL,isMal BOOLEAN");
     * <br> <b>Note:</b>value types are case insensitive.
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not create the table and it
     * will be displayed in the output:<br>
     * <br>1- if the connection is closed.
     * <br>2- if the tableName or columnsNamesWithTypes is null or empty.
     *
     * @param tableName the table name.
     * @param columnsNamesWithTypes columns Names With their Types.
     */
    public void createNewTableIfNotExists(String tableName, String columnsNamesWithTypes) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't create new table Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNamesWithTypes == null || columnsNamesWithTypes.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }
        StringBuilder sqlS = new StringBuilder(50);
        sqlS.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(").append(columnsNamesWithTypes).append(")");
        // create a new table
        try (PreparedStatement ps = connection.prepareStatement(sqlS.toString())) {
            // create a new table
            ps.executeUpdate();
        }
    }

    public void createNewVirtualTableIfNotExists(String tableName, String columnsNamesWithoutType) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't create new virtual table Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNamesWithoutType == null || columnsNamesWithoutType.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }
        StringBuilder sqlS = new StringBuilder(50);
        sqlS.append("CREATE VIRTUAL TABLE IF NOT EXISTS ").append(tableName).append(" USING fts5(").append(columnsNamesWithoutType).append(")");
        // create a new table
        try (PreparedStatement ps = connection.prepareStatement(sqlS.toString())) {
            // create a new table
            ps.executeUpdate();
        }

    }

    public void createNewVirtualTableIfNotExists(String tableName, String columnsNamesWithoutType, String module) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't create new virtual table Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNamesWithoutType == null || columnsNamesWithoutType.trim().isEmpty() || module == null || module.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns or module can't be empty or null");
        }
        StringBuilder sqlS = new StringBuilder(50);
        sqlS.append("CREATE VIRTUAL TABLE IF NOT EXISTS ").append(tableName).append(" USING ").append(module).append("(").append(columnsNamesWithoutType).append(")");
        // create a new table
        try (PreparedStatement ps = connection.prepareStatement(sqlS.toString())) {
            // create a new table
            ps.executeUpdate();
        }
    }

    /**
     * database COUNT function to count rows in a table.
     * <br> the syntax is :
     * <br> <code>count(tabele Name,columns Names,where Condition,values for
     * condition);</code>
     * <br> table name and columns cant's be empty or null.
     * <br> columns names can be one or two... or all.
     * <br> columns names can be replaces by '*' that means all columns.
     * <br> the condion is optional if you don't want to use it put empty String
     * or null.
     * <br> the values for condition is also optional and it's OK that you don't
     * give any value if you dont specify the condtion
     * <br> example: <code>count("students","*","");</code> // will return
     * number of rows is students table.
     * <br> If you don't want to count dublicate rows use the Keyword
     * <b>DISTINCT</b> before columns names.
     * <br> <b>NOTE:</b> you can't use the <b>*</b> sympol when using
     * <b>DISTINCT</b> you must give one column or more.
     * <br> the condition allow you to limit the result to specific condionn.
     * <br> condition always start with keyword <b>WHERE</b> then the condition.
     * <br> example:
     * <code>count("students","name,id,gpa","WHERE gpa {@literal >}
     * 3.25");</code> // return number of students with gpa more than 3.25
     * <br> the above example is working but not good,use the next example:
     * <br> <code>count("students","name,id,gpa","WHERE gpa {@literal >}
     * ?",3.25)</code>
     * <br> notice that we have replaced the 3.25 in condition by ? and provided
     * it in the values array.
     * <br> this way is better.
     * <br> Examples:
     * <br>
     * <code>count("students","name","WHERE name = ?","Mohammed")</code>//number
     * of students with Mohammed as thier name
     * <br> count("students","gpa","WHERE gpa {@literal >}= ? AND gpa
     * {@literal <}= ?",2.5,3.5)//number of students with 3.5 {@literal >}= gpa
     * {@literal &&} gpa {@literal >}= 2.5
     * <br><br><b>****************** Warnings ******************</b>
     * <br> if any of this Errors happend it will not count it will be displayed
     * in the output:<br>
     * <br>1- if the connection is closed.
     * <br>2- if the tableName or columnsName is null or empty.
     * <br>3- if the tableName or columnsName doesn't exists.
     * <br>4- if the values array is null. -{@literal >}
     * count("students","*","",null);
     * <br>5- if the <code>values length {@literal <} SQL statement '?'
     * symbols</code>.
     *
     * @param tableName table name can't be empty or null.
     * @param columnName columns names can't be empty or null ,or * for all
     * columns.
     * @param whereCondition optional condition.
     * @param values array of values for the condition to replace the ? sympol.
     * @return number of rows in a table after applaying the condition (if
     * exists), or zero if any error happend.
     * @throws java.sql.SQLException
     */
    public int count(String tableName, String columnName, String whereCondition, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            //System.err.println("[" + Thread.currentThread().getStackTrace()[1].getClassName() + " Error] -> " + Thread.currentThread().getStackTrace()[2] + " " + "can't count connection is closed");
            throw new SQLHelperExeption("Must Call open() before count(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            //System.err.println("[" + Thread.currentThread().getStackTrace()[1].getClassName() + " Error] -> " + Thread.currentThread().getStackTrace()[2] + " " + "table name or columns can't be empty or null");
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }

        if (values == null) {
            //System.err.println("[" + Thread.currentThread().getStackTrace()[1].getClassName() + " Error] -> " + Thread.currentThread().getStackTrace()[2] + " " + "Array of values can't be null but values can be null,try cast null to '(Object)null'");
            throw new SQLHelperExeption("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT COUNT(").append(columnName).append(") FROM ").append(tableName).append(" ").append(whereCondition == null ? "" : whereCondition);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();

            if (values.length < varags) {
                //System.err.println("[" + Thread.currentThread().getStackTrace()[1].getClassName() + " Error] -> " + Thread.currentThread().getStackTrace()[2] + " " + "SQL statement require " + varags + " values but found " + values.length + " values.");
                throw new SQLHelperExeption("SQL statement require " + varags + " values but found " + values.length + " values.");
            }

            for (int i = 0; i < varags; i++) {
                setValuesForPS(ps, values[i], i + 1);
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {

            if (ex.getMessage().contains("function COUNT()")) {
                throw new SQLHelperExeption("The result of columnName parameter must be single column OR you can use '*'");
            } else {
                throw new SQLException(ex.getMessage());
            }
        }

        return -1;
    }

    /**
     * database MIN function that return lowest value in column. if a column
     * value is not number or can't be converted to number it will be considered
     * as 0,NULL values will be ignored.
     *
     * @param tableName the table name can't be null or empty.
     * @param columnName column name can't be null or empty.
     * @param round number of decimals after the comma, if the value is
     * {@literal <} 0 there will be no rounding.
     * @return lowest value in a coulmn as double.
     */
    public double min(String tableName, String columnName, int round) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("Must Call open() before min(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or column can't be empty or null");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT (").append(round < 0 ? "" : "round(").append("MIN(").append(columnName).append(")").append(round < 0 ? "" : "," + round + ")").append(") FROM ").append(tableName);

        PreparedStatement ps = connection.prepareStatement(sql.toString());

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getDouble(1);
        }

        return 0;
    }

    /**
     * database MAX function that return heighest value in column. if a column
     * value is not number or can't be converted to number it will be considered
     * as 0,NULL values will be ignored.
     *
     * @param tableName the table name can't be null or empty.
     * @param columnName column name can't be null or empty.
     * @param round number of decimals after the comma, if the value is
     * {@literal <} 0 there will be no rounding.
     * @return heighest value in a coulmn as double.
     */
    public double max(String tableName, String columnName, int round) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("Must Call open() before max(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or column can't be empty or null");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT (").append(round < 0 ? "" : "round(").append("MAX(").append(columnName).append(")").append(round < 0 ? "" : "," + round + ")").append(") FROM ").append(tableName);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    /**
     * database SUM function that return the sum of all values in column. if a
     * column value is not number or can't be converted to number it will be
     * considered as 0,NULL values will be ignored.
     *
     * @param tableName the table name can't be null or empty.
     * @param columnName column name can't be null or empty.
     * @param round number of decimals after the comma, if the value is
     * {@literal <} 0 there will be no rounding.
     * @return sum of all values in a coulmn as double.
     */
    public double sum(String tableName, String columnName, int round) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("Must Call open() before sum(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT (").append(round < 0 ? "" : "round(").append("SUM(").append(columnName).append(")").append(round < 0 ? "" : "," + round + ")").append(") FROM ").append(tableName);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    /**
     * database AVG function that return the average of all values in column. if
     * a column value is not number or can't be converted to number it will be
     * considered as 0,NULL values will be ignored.
     *
     * @param tableName the table name can't be null or empty.
     * @param columnName column name can't be null or empty.
     * @param round number of decimals after the comma, if the value is
     * {@literal <} 0 there will be no rounding.
     * @return average of all values in a coulmn as double.
     */
    public double avg(String tableName, String columnName, int round) throws SQLException {

        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("Must Call open() before avg(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT (").append(round < 0 ? "" : "round(").append("AVG(").append(columnName).append(")").append(round < 0 ? "" : "," + round + ")").append(") FROM ").append(tableName);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    /**
     * Helper method to check if a row exists. This method use the method
     * <b>count</b> with the same syntax and check if result is bigger than 0.
     *
     * @param tableName the table name can't be null or empty.
     * @param columnsNames the columns names can't be null or empty.
     * @param whereCondition the condition to look for, if empty will return
     * true if the table contain any row.
     * @param values array of values to be used with Where condition.
     * @return true if result is bigger than 0.
     * <br>
     */
    public boolean isExists(String tableName, String columnsNames, String whereCondition, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("Must Call open() Before isExists(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }

        if (values == null) {
            throw new SQLHelperExeption("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT COUNT(").append(columnsNames).append(") FROM ").append(tableName).append(" ").append(whereCondition == null ? "" : whereCondition).append(";");

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        int varags = ps.getParameterMetaData().getParameterCount();

        if (values.length < varags) {
            throw new SQLHelperExeption("SQL statment require " + varags + " values but found " + values.length + " values.");
        }

        for (int i = 0; i < varags; i++) {
            setValuesForPS(ps, values[i], i + 1);
        }

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public ResultSet select(String tableName, String columnsNames, String whereCondition, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("Must Call open() before select(), connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperExeption("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }
        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT ").append(columnsNames).append(" FROM ").append(tableName).append(" ").append(whereCondition == null ? "" : whereCondition);

        PreparedStatement ps = connection.prepareStatement(sql.toString());

        int varags = ps.getParameterMetaData().getParameterCount();

        if (values.length < varags) {
            throw new SQLHelperExeption("SQL statement require " + varags + " values but found " + values.length + " values.");
        }
        for (int i = 0; i < varags; i++) {
            setValuesForPS(ps, values[i], i + 1);
        }
        ResultSet rs = ps.executeQuery();

        return rs;
    }

    /**
     * <p>
     * Insert data into a table. Example :
     * <code>insert("persons","name,age,country","Omar",35,"KSA")</code>
     * </p>
     *
     * @param tableName
     * @param columnsNames
     * @param values
     * @return
     */
    public int insert(String tableName, String columnsNames, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("Must Call open() before insert(), connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperExeption("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }

        int size = columnsNames.split(",").length;
        StringBuilder sql = new StringBuilder(50);
        sql.append("INSERT INTO ").append(tableName).append(" (").append(columnsNames).append(") VALUES (").append(Q_Marks(size)).append(")");

        int res = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();

            if (values.length < varags) {
                throw new SQLHelperExeption("SQL statement require " + varags + " values but found " + values.length + " values.");
            }

            for (int i = 0; i < varags; i++) {
                setValuesForPS(ps, values[i], i + 1);
            }

            res = ps.executeUpdate();
        }
        return res;
    }

    public int replace(String tableName, String columnsNames, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't replace connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperExeption("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }

        int size = columnsNames.split(",").length;
        StringBuilder sql = new StringBuilder(50);
        sql.append("REPLACE INTO ").append(tableName).append(" (").append(columnsNames).append(") VALUES (").append(Q_Marks(size)).append(")");

        int res = 0;
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();
            if (values.length < varags) {
                throw new SQLHelperExeption("SQL statement require " + varags + " values but found " + values.length + " values.");
            }
            for (int i = 0; i < varags; i++) {
                setValuesForPS(ps, values[i], i + 1);
            }
            res = ps.executeUpdate();
        }

        return res;
    }

    public int delete(String tableName, String whereCondition, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("can't delete connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new SQLHelperExeption("table name can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperExeption("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }
        StringBuilder sql = new StringBuilder(50);
        sql.append("DELETE FROM ").append(tableName).append(" ").append(whereCondition == null ? "" : whereCondition);
        int res = 0;
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();
            if (values.length < varags) {
                throw new SQLHelperExeption("SQL statement require " + varags + " values but found " + values.length + " values.");
            }
            for (int i = 0; i < varags; i++) {
                setValuesForPS(ps, values[i], i + 1);
            }
            res = ps.executeUpdate();
        }

        return res;

    }

    public int update(String tableName, String columnsNames, String whereCondition, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperExeption("Must Call open() before update(), connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperExeption("table name or columns or condition can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperExeption("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }
        int size = columnsNames.split(",").length;
        StringBuilder sql = new StringBuilder(50);
        sql.append("UPDATE ").append(tableName).append(" SET ").append(Q_Marks(size, columnsNames)).append(" ").append(whereCondition == null ? "" : whereCondition);

        int res = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();

            if (values.length < varags) {
                throw new SQLHelperExeption("SQL statement require " + varags + " values but found " + values.length + " values.");
            }

            for (int i = 0; i < varags; i++) {
                setValuesForPS(ps, values[i], i + 1);
            }
            res = ps.executeUpdate();
        }

        return res;

    }

    public PreparedStatement prepareStatement(String SQLstatement) {
        try {
            if (connection == null || connection.isClosed()) {
                System.err.println("[" + Thread.currentThread().getStackTrace()[1].getClassName() + " Error] -> " + Thread.currentThread().getStackTrace()[2] + " " + "can't preparestatement Must Call open() before that, connection is closed");
                return null;
            }
            return connection.prepareStatement(SQLstatement);
        } catch (SQLException ex) {
            System.err.println("[" + Thread.currentThread().getStackTrace()[1].getClassName() + " Error] -> " + Thread.currentThread().getStackTrace()[2] + " " + ex.getMessage());
        }
        return null;
    }

    private String Q_Marks(int size) {
        StringBuilder res = new StringBuilder("");
        for (int i = 0; i < size; i++) {
            res.append((i == size - 1) ? "?" : "?,");
        }
        return res.toString();
    }

    private String Q_Marks(int size, String columnsName) {
        StringBuilder res = new StringBuilder("");
        String[] cn = columnsName.split(",");
        int columnsSize = cn.length;
        for (int i = 0; i < size && i < columnsSize; i++) {
            res.append(cn[i]).append(" = ?").append(i == size - 1 || i == columnsSize - 1 ? "" : ",");
        }
        return res.toString();
    }

    private void setValuesForPS(PreparedStatement ps, Object obj, int index) throws SQLException {
        if (obj instanceof String) {
            ps.setString(index, (String) obj);
        } else if (obj instanceof Integer) {
            ps.setInt(index, (int) obj);
        } else if (obj instanceof Double) {
            ps.setDouble(index, (double) obj);
        } else if (obj instanceof Byte[]) {
            ps.setBytes(index, (byte[]) obj);
        } else if (obj instanceof Long) {
            ps.setLong(index, (long) obj);
        } else if (obj instanceof Float) {
            ps.setFloat(index, (float) obj);
        } else if (obj instanceof Boolean) {
            ps.setBoolean(index, (boolean) obj);
        } else if (obj instanceof Byte) {
            ps.setByte(index, (byte) obj);
        } else if (obj instanceof Short) {
            ps.setShort(index, (short) obj);
        } else {
            ps.setObject(index, obj);

        }
    }
}

class SQLHelperExeption extends SQLException {

    String message;

    public SQLHelperExeption(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
