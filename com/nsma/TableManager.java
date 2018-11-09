package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface TableManager {
    
    public String getColumns(String table) throws Exception;
    
    public void addColumn(String table,String columnWithType) throws Exception;
    
    public void dropColumn(String table,String column) throws Exception;

    public void dropTable(String table) throws Exception;
    
    public void dropTableIfExists(String table) throws Exception;
    
    public void createTable(String table,String columnsNamesWithType) throws Exception;
    
    public void createTableIfNotExists(String table,String columnsNamesWithType) throws Exception;
    
    public boolean isTableExists(String table) throws Exception;
}
