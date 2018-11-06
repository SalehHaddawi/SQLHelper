package com.nsma;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperValue {
    
    public void setSQLHelperValue(String colName,ResultSet rs) throws Exception;
    
    public void getSQLHelperValue (String colName,int index,PreparedStatement ps) throws Exception;
    
}
