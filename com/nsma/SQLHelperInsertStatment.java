package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperInsertStatment {

    public SQLHelperInsertStatment setCols(String columns);
    
    public SQLHelperInsertStatment setValues(Object... values);
        
    public SQLHelperInsertStatment setValues(SQLHelperValue valueObject) throws Exception;
    
    public SQLHelperInsertStatment setCol(String col,Object value)throws Exception;
    
    public SQLHelperInsertStatment reset();
    
    public SQLHelperStatmentMetaData getMetaData();
    
    public int execute() throws Exception;
}
