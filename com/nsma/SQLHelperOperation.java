package com.nsma;

/**
 *
 * @author Saleh Haddawi
 */
public interface SQLHelperOperation {

    public SQLHelperInsertStatment insertInto(String tableName) throws Exception;

    public SQLHelperUpdateStatment update(String tableName) throws Exception;

    public SQLHelperDeleteStatment deleteFrom(String tableName) throws Exception;

    public SQLHelperSelectStatment selectFrom(String tableName) throws Exception;

}
