package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperStatmentMetaData {

    public String getSQLString() throws Exception;

    public Object[] getValues();
    
    public Object[] getConditionValues();
    
    public String getConditioString();
    
    public String getColumns() throws Exception;
    
    public int getParametersCount() throws Exception;
}
