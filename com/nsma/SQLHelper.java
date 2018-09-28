package com.nsma;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author Saleh Haddawi
 */
/**
 * SQLHelper is designed to make quick and easy access to database to perform
 * simple operations.
 *
 * @author Saleh Haddawi
 */
public class SQLHelper implements AutoCloseable {

    //the connection to the databse
    private Connection connection = null;

    //data base URL
    private String DB_URL = "";

    /**
     * Get coonection to database, not required to use SQLHelper.
     *
     * @return The connection object to database.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Prepare the database connection url,it's diffrent from one database to
     * onother.
     *
     * @param URL databse String
     */
    public SQLHelper(String URL) {
        DB_URL = URL != null ? URL : "";
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
     * connect to the data base with provided DB Path, This method must be
     * called before any operation on database If connection is already opened
     * it will be closed and new one will be created.<br>
     * This method is used for connection without user and pass or special
     * properties.
     *
     * @throws SQLHelperException - if database url is empty.
     * @throws SQLException if a database access error occurs.
     *
     * @see #close()
     */
    public void open() throws SQLException {
        if (getDBPath().trim().isEmpty()) {
            throw new SQLHelperException("can't connect to database URL is not specified");
        }

        close();

        connection = null;
        // create a connection to the database
        connection = DriverManager.getConnection(DB_URL);
        System.out.println("Connection to [" + DB_URL + "] has been started.");
    }

    /**
     * connect to the data base with provided DB Path, This method must be
     * called before any operation on database If connection is already opened
     * it will be closed and new one will be created.
     *
     * @param username The database username.
     * @param password The user password.
     *
     * @throws SQLHelperException - if database url is empty.
     * @throws SQLException if a database access error occurs.
     *
     * @see #close()
     */
    public void open(String username, String password) throws SQLException {
        if (getDBPath().trim().isEmpty()) {
            throw new SQLHelperException("can't connect to database URL is not specified");
        }

        close();

        connection = null;//Properties properties

        // create a connection to the database
        connection = DriverManager.getConnection(DB_URL, username, password);
        System.out.println("Connection to [" + DB_URL + "] has been started.");
    }

    /**
     * connect to the data base with provided DB Path, This method must be
     * called before any operation on database If connection is already opened
     * it will be closed and new one will be created.
     *
     * @param properties Connection properties.
     *
     * @throws SQLHelperException - if database url is empty.
     * @throws SQLException if a database access error occurs.
     *
     * @see #close()
     */
    public void open(Properties properties) throws SQLException {
        if (getDBPath().trim().isEmpty()) {
            throw new SQLHelperException("can't connect to database URL is not specified");
        }

        close();

        connection = null;

        // create a connection to the database
        connection = DriverManager.getConnection(DB_URL, properties);
        System.out.println("Connection to [" + DB_URL + "] has been started.");
    }

    /**
     * close connection to the database and release any resources.
     * <br> This method has no effect if connection is already closed or hasn't
     * been started.
     *
     * @throws SQLException if a database access error occurs.
     *
     * @see #open()
     */
    @Override
    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
            System.out.println("Connection to [" + DB_URL + "] has been closed.");
        }
    }

    /**
     * Start transaction.<br> A transaction is the spread of one or more changes
     * to the database.For example, if you are creating, updating, or deleting a
     * record from the table, then you are performing transaction on the table.
     * It is important to control transactions to ensure data safety and to
     * handle database errors.
     * <br> it always better to commit or rollback transacton before disconnect
     * from connection.
     * <br> the default for database is to create transaction for every command
     * and then commit afetr it finished,calling {@link #beginTransaction() }
     * make database wait until you commit or rollback.
     * <br> For Example executing many inserts in one transaction is
     * <b>signifntly</b> faster than seperate inserts.
     *
     * @throws SQLHelperException - if connection is closed.
     * @throws SQLException if a database access error occurs, , while
     * participating in a distributed transaction
     *
     * @see #commitTransaction()
     * @see #rollBackTransaction()
     */
    public void beginTransaction() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("can't begin transaction Must Call open() before that, connection is closed");
        }
        connection.setAutoCommit(false);
    }

    /**
     * Commit (Apply) any database changes in a transaction that haven't been
     * commited, Must be called after the {@link #beginTransaction() } method.
     * If Any Error happend before committing the transaction then it will
     * rollback.
     * <br>It's recommended to commite or rollback transaction before closing
     * connection.
     * <br>
     * For more infromation about transaction see {@link #beginTransaction() }
     *
     * @throws SQLHelperException if connection is closed, haven't called {@link #beginTransaction()
     * } before committing.
     * @throws SQLException if a database access error occurs.
     *
     * @see #beginTransaction()
     * @see #rollBackTransaction()
     */
    public void commitTransaction() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("can't commit transaction Must Call open() before that, connection is closed");
        }
        if (connection.getAutoCommit()) {
            throw new SQLHelperException("can't commit transaction Must call beginTransaction() before commitTransaction()");
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Rollback (cancel) any database changes in a transaction that haven't been
     * commited, Must be called after the {@link #begin_Transaction() } method.
     * If Any Error happend before committing the transaction then it will
     * rollback.
     * <br>It's recommended to commite or rollback transaction before closing
     * connection.
     * <br>
     * For more infromation about transaction see {@link #beginTransaction() }
     *
     * @throws SQLHelperException if connection is closed, haven't called {@link #beginTransaction()
     * } before rollback.
     * @throws SQLException if a database access error occurs.
     *
     * @see #beginTransaction()
     * @see #rollBackTransaction()
     */
    public void rollBackTransaction() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("can't rollback transaction Must Call open() before that, connection is closed");
        }
        if (connection.getAutoCommit()) {
            throw new SQLHelperException("can't rollback transaction must call beginTransaction() before rollBackTransaction()");
        }
        connection.rollback();
        connection.setAutoCommit(true);
    }

    /**
     * Indicate if database will be using autoCommite.
     *
     * @return true if AutoCommite for database is true, otherwise false.
     *
     * @throws SQLHelperException if connection is closed.
     * @throws SQLException if a database access error occurs.
     */
    public boolean isAutoCommit() throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("can't check isAutoCommit Must Call open() before that, connection is closed");
        }
        return connection.getAutoCommit();
    }

    /**
     * Check if the connection is closed.
     *
     * @return true if the connection is closed or if any error happend during
     * checking, false otherwise.
     *
     * @throws SQLException if a database access error occurs.
     */
    public boolean isConnectionClosed() throws SQLException {
        return connection.isClosed();
    }

    /**
     * Drop (delete) the table and all of it's content,if the table didn't
     * exists this method has no effect.
     * <br> Not all databases support this method.
     *
     * @param tableName the name of the table to be deleted can't be empty or
     * null.
     *
     * @throws SQLHelperException if connection is closed, Table name is empty
     * or null.
     * @throws SQLException if a database access error occurs, database doesn't
     * support this functionality.
     *
     */
    public void dropTableIfExists(String tableName) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("can't drop table Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new SQLHelperException("table name can't be empty or null");
        }
        PreparedStatement ps = connection.prepareStatement("DROP TABLE IF EXISTS " + tableName);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Drop (delete) the table and all of it's content.
     *
     * @param tableName the name of the table to be deleted can't be empty or
     * null;
     *
     * @throws SQLHelperException - if connection is closed, Table name is empty
     * or null.
     * @throws SQLException if a database access error occurs, table is not
     * found.
     *
     */
    public void dropTable(String tableName) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("can't drop table Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new SQLHelperException("table name can't be empty or null");
        }
        PreparedStatement ps = connection.prepareStatement("DROP TABLE " + tableName);
        ps.executeUpdate();
        ps.close();

    }

    /**
     * Create new table if not exists.<br> Not all databses support this
     * method.<br> Example:
     * <pre><code>createNewTableIfNotExists("items","id INT PRIMARY KEY,name VARCHAR(45) NOT NULL,quantity INT");</code></pre>
     *
     * @param tableName the table name.
     * @param columnsNamesWithTypes columns Names With their Types.
     *
     * @throws SQLHelperException - if connection is closed, Table name or
     * column names are empty or null.
     * @throws SQLException if a database access error occurs, database doesn't
     * support this functionality.
     */
    public void createNewTableIfNotExists(String tableName, String columnsNamesWithTypes) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("can't create new table Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNamesWithTypes == null || columnsNamesWithTypes.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns names can't be empty or null");
        }
        StringBuilder sqlS = new StringBuilder(50);
        sqlS.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append("(").append(columnsNamesWithTypes).append(")");
        // create a new table
        PreparedStatement ps = connection.prepareStatement(sqlS.toString());
        // create a new table
        ps.executeUpdate();

        ps.close();
    }

    /**
     * Create new table if not exists.<br> Not all databses support this
     * method.<br> Example:
     * <pre><code>createNewTable("items","id INT PRIMARY KEY,name VARCHAR(45) NOT NULL,quantity INT");</code></pre>
     *
     * @param tableName the table name.
     * @param columnsNamesWithTypes columns Names With their Types.
     *
     * @throws SQLHelperException if connection is closed, Table name or column
     * names are empty or null.
     * @throws SQLException if a database access error occurs, database doesn't
     * support this functionality.
     */
    public void createNewTable(String tableName, String columnsNamesWithTypes) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("can't create new table Must Call open() before that, connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNamesWithTypes == null || columnsNamesWithTypes.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns can't be empty or null");
        }
        StringBuilder sqlS = new StringBuilder(50);
        sqlS.append("CREATE TABLE ").append(tableName).append("(").append(columnsNamesWithTypes).append(")");
        // create a new table
        PreparedStatement ps = connection.prepareStatement(sqlS.toString());
        // create a new table
        ps.executeUpdate();

        ps.close();
    }

    /**
     * SQL COUNT function to count the number of rows returned in a SELECT statement.
     *
     * <br> Example:
     * <pre><code>count("items","*","")</code></pre> //will return number of all
     * rows in "items" table.
     *
     * @param tablesNames tables names seperated by comma, can't be empty or
     * null.
     * @param aggregateExpression column or expression whose non-null values
     * will be counted.
     * @param whereCondition optional condition.
     * @param values array of values for the condition.
     *
     * @return number of rows in selected tables.
     *
     * @throws SQLHelperException if connection is closed, if tables names is
     * empty or null, aggregateExpression is empty or null, values array is
     * null, values array length {@literal <} condition Parameters.
     * @throws SQLException if a database access error occurs.
     * 
     * @see #select(java.lang.String, java.lang.String, java.lang.String, java.lang.Object...) 
     */
    public int count(String tablesNames, String aggregateExpression, String whereCondition, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("Must Call open() before count(), connection is closed");
        }

        if (tablesNames == null || tablesNames.trim().isEmpty() || aggregateExpression == null || aggregateExpression.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns names can't be empty or null");
        }

        if (values == null) {
            throw new SQLHelperException("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT COUNT(").append(aggregateExpression).append(") FROM ").append(tablesNames).append(" ").append(whereCondition == null ? "" : whereCondition);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();

            if (values.length < varags) {
                throw new SQLHelperException("SQL statement require " + varags + " values but found " + values.length + " values.");
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
                throw new SQLHelperException("The result of columnName parameter must be single column OR you can use '*'");
            } else {
                throw new SQLException(ex.getMessage());
            }
        }

        return 0;
    }

    /**
     * SQL MIN function that return lowest value in column. if a column value is
     * not number or can't be converted to number it will be considered as
     * 0,NULL values will be ignored.
     *
     * @param tableName the table name can't be null or empty.
     * @param columnName column name can't be null or empty.
     * @param round number of decimals after the comma, if the value is
     * {@literal <} 0 there will be no rounding.
     * @return lowest value in a coulmn as double.
     *
     * @throws SQLHelperException if connection is closed, if table name is
     * empty or null, columnName name is empty or null.
     * @throws SQLException if a database access error occurs.
     */
    public double min(String tableName, String columnName, int round) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("Must Call open() before min(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            throw new SQLHelperException("table name or column can't be empty or null");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT (").append(round < 0 ? "" : "round(").append("MIN(").append(columnName).append(")").append(round < 0 ? "" : "," + round + ")").append(") FROM ").append(tableName);

        PreparedStatement ps = connection.prepareStatement(sql.toString());

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
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
     *
     * @return heighest value in a coulmn as double.
     *
     * @throws SQLHelperException if connection is closed, if table name is
     * empty or null, columnName name is empty or null.
     * @throws SQLException if a database access error occurs.
     */
    public double max(String tableName, String columnName, int round) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("Must Call open() before max(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            throw new SQLHelperException("table name or column can't be empty or null");
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
     *
     * @return sum of all values in a coulmn as double.
     *
     * @throws SQLHelperException if connection is closed, if table name is
     * empty or null, columnName name is empty or null.
     * @throws SQLException if a database access error occurs.
     */
    public double sum(String tableName, String columnName, int round) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("Must Call open() before sum(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns can't be empty or null");
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
     *
     * @return average of all values in a coulmn as double.
     *
     * @throws SQLHelperException if connection is closed, if table name is
     * empty or null, columnName name is empty or null.
     * @throws SQLException if a database access error occurs.
     */
    public double avg(String tableName, String columnName, int round) throws SQLException {

        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("Must Call open() before avg(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnName == null || columnName.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns can't be empty or null");
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
     * {@link #count(java.lang.String, java.lang.String, java.lang.String, java.lang.Object...) }
     * with the same syntax and check if result is bigger than 0.
     *
     * @param tableName the table name can't be null or empty.
     * @param columnsNames the columns names can't be null or empty.
     * @param whereCondition the condition to look for, if empty will return
     * true if the table contain any row.
     * @param values array of values to be used with Where condition.
     *
     * @return true if result is bigger than 0.
     *
     * @throws SQLHelperException if connection is closed, if tables names is
     * empty or null, aggregateExpression is empty or null, values array is
     * null, values array length {@literal <} condition Parameters. SQLException
     * if a database access error occurs.
     * <br>
     */
    public boolean isExists(String tableName, String columnsNames, String whereCondition, Object... values) throws SQLException {
        if (connection == null || connection.isClosed()) {
            throw new SQLHelperException("Must Call open() Before isExists(), connection is closed");
        }

        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns can't be empty or null");
        }

        if (values == null) {
            throw new SQLHelperException("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }

        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT COUNT(").append(columnsNames).append(") FROM ").append(tableName).append(" ").append(whereCondition == null ? "" : whereCondition).append(";");

        PreparedStatement ps = connection.prepareStatement(sql.toString());
        int varags = ps.getParameterMetaData().getParameterCount();

        if (values.length < varags) {
            throw new SQLHelperException("SQL statment require " + varags + " values but found " + values.length + " values.");
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
            throw new SQLHelperException("Must Call open() before select(), connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperException("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }
        StringBuilder sql = new StringBuilder(50);
        sql.append("SELECT ").append(columnsNames).append(" FROM ").append(tableName).append(" ").append(whereCondition == null ? "" : whereCondition);

        PreparedStatement ps = connection.prepareStatement(sql.toString());

        int varags = ps.getParameterMetaData().getParameterCount();

        if (values.length < varags) {
            throw new SQLHelperException("SQL statement require " + varags + " values but found " + values.length + " values.");
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
            throw new SQLHelperException("Must Call open() before insert(), connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperException("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }

        int size = columnsNames.split(",").length;
        StringBuilder sql = new StringBuilder(50);
        sql.append("INSERT INTO ").append(tableName).append(" (").append(columnsNames).append(") VALUES (").append(Q_Marks(size)).append(")");

        int res = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();

            if (values.length < varags) {
                throw new SQLHelperException("SQL statement require " + varags + " values but found " + values.length + " values.");
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
            throw new SQLHelperException("can't replace connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperException("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }

        int size = columnsNames.split(",").length;
        StringBuilder sql = new StringBuilder(50);
        sql.append("REPLACE INTO ").append(tableName).append(" (").append(columnsNames).append(") VALUES (").append(Q_Marks(size)).append(")");

        int res = 0;
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();
            if (values.length < varags) {
                throw new SQLHelperException("SQL statement require " + varags + " values but found " + values.length + " values.");
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
            throw new SQLHelperException("can't delete connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new SQLHelperException("table name can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperException("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }
        StringBuilder sql = new StringBuilder(50);
        sql.append("DELETE FROM ").append(tableName).append(" ").append(whereCondition == null ? "" : whereCondition);
        int res = 0;
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();
            if (values.length < varags) {
                throw new SQLHelperException("SQL statement require " + varags + " values but found " + values.length + " values.");
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
            throw new SQLHelperException("Must Call open() before update(), connection is closed");
        }
        if (tableName == null || tableName.trim().isEmpty() || columnsNames == null || columnsNames.trim().isEmpty()) {
            throw new SQLHelperException("table name or columns or condition can't be empty or null");
        }
        if (values == null) {
            throw new SQLHelperException("Array of values can't be null but values can be null,try cast null to '(Object)null'");
        }
        int size = columnsNames.split(",").length;
        StringBuilder sql = new StringBuilder(50);
        sql.append("UPDATE ").append(tableName).append(" SET ").append(Q_Marks(size, columnsNames)).append(" ").append(whereCondition == null ? "" : whereCondition);

        int res = 0;

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int varags = ps.getParameterMetaData().getParameterCount();

            if (values.length < varags) {
                throw new SQLHelperException("SQL statement require " + varags + " values but found " + values.length + " values.");
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

/**
 * An exception that provides information on SQLHelper error, not necessary a database error.
 *
 * @author Saleh Haddawi
 */
class SQLHelperException extends SQLException {

    String message;

    public SQLHelperException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
