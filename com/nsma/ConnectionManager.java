package com.nsma;

import com.nsma.SQLHelper.DatabaseType;
import java.sql.Connection;

/**
 *
 * @author Saleh Haddawi
 */
public interface ConnectionManager {
    
    public Connection getConnection();
    
    public String getDatabaseURL();
    
    public DatabaseType getDatabaseType();
    
    public boolean isClosed() throws Exception;
    
    public void close() throws Exception;
}
