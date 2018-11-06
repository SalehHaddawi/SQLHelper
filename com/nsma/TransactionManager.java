package com.nsma;

import java.sql.Savepoint;

/**
 *
 * @author Saleh Haddawi
 */
public interface TransactionManager {

    public void begin() throws Exception;

    public void commit() throws Exception;
    
    public Savepoint createSavePoint() throws Exception;

    public void rollback() throws Exception;
    
    public void rollback(Savepoint savepoint) throws Exception;
    
    public boolean isAutoCommit() throws Exception;
}
