package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperDeleteStatment {

    public SQLHelperDeleteStatment where(String condition, Object... values);

    public SQLHelperDeleteStatment reset();

    public SQLHelperStatmentMetaData getMetaData();

    public int execute() throws Exception;

    public long executeLarge() throws Exception;
}
