package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface TableManager {

    public void dropTable(String table) throws Exception;
    
    public void dropTableIfExists(String table) throws Exception;
    
    public void createTable(String table,String columnsNamesWithType) throws Exception;
    
    public void createTableIfNotExists(String table,String columnsNamesWithType) throws Exception;
    
    public boolean isTableExists(String table) throws Exception;
}
