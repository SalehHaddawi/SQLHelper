package com.nsma;

import java.sql.ResultSet;
import java.util.List;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperSelectStatment {

    public SQLHelperSelectStatment where(String condition, Object... values);
    
    public SQLHelperSelectStatment having(String condition, Object... values);
    
    public SQLHelperSelectStatment setCols(String columns);
        
    public SQLHelperSelectStatment distinct(boolean selectDistinct);
    
    public SQLHelperSelectStatment limit(int rows);
    
    public double max(String column) throws Exception;
    
    public double min(String column) throws Exception;
    
    public double sum(String column) throws Exception;
    
    public double avg(String column) throws Exception;
    
    public long count(String column) throws Exception;
    
    public SQLHelperSelectStatment orderBy(String column);
    
    public SQLHelperSelectStatment groupBy(String column);
    
    public SQLHelperSelectStatment reset();
    
    public ResultSet execute() throws Exception;
        
    public <T extends SQLHelperValue> List<T> execute(Class<T> returnListType) throws Exception;
}
