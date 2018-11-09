package com.nsma;

import java.util.Map;

/**
 *
 * @author Saleh Haddawi
 */
public interface KeyValueTable {
    
    public String getKeyValueTableName();

    public void put(String key, Object value) throws Exception;

    public Object get(String key) throws Exception;

    public <T> T get(String key, Class<T> resultType) throws Exception;

    public boolean containsKey(String key) throws Exception;
    
    public String containsValue(Object value) throws Exception;

    public Object remove(String key) throws Exception;
    
    public Object replace(String key,Object newValue) throws Exception;
    
    public void clear() throws Exception;

    public int size() throws Exception;

    public Map<String,Object> getMap() throws Exception;
    
    public void putMap(Map<String,Object> map) throws Exception;
}
