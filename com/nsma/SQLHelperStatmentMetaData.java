package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperStatmentMetaData {

    public String getSQLString() throws Exception;

    public String getWhereString();

    public String getHavingString();

    public String getColumns() throws Exception;
    
    public String getTable();

    public Object[] getValues();

    public Object[] getWhereValues();

    public Object[] getHavingValues();

    public int getRequiredValuesCount() throws Exception;
}
