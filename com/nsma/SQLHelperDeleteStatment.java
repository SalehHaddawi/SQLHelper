package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperDeleteStatment {

    public SQLHelperDeleteStatment setCondition(String condition, Object... values);
    
    public SQLHelperDeleteStatment reset();

    public int execute() throws Exception;

    public long executeLarge() throws Exception;
}
