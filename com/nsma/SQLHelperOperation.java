package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperOperation {

    public SQLHelperInsertStatment insert(String tableName) throws Exception;

    public SQLHelperUpdateStatment update(String tableName) throws Exception;

    public SQLHelperDeleteStatment delete(String tableName) throws Exception;

    public SQLHelperSelectStatment select(String tableName) throws Exception;

}
