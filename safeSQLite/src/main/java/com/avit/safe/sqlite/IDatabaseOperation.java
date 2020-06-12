package com.avit.safe.sqlite;

import java.util.List;
import java.util.Map;

public interface IDatabaseOperation<DATA> {

    String getLogTag();

    /**
     * -------save-------------
     */
    int saveOrUpdate(DATA data);


    int saveOrUpdate(List<DATA> datas);

    /**
     * -------read-------------
     */
    DATA read(DATA data);

    List<DATA> read(Map<String, Object> objects);

    List<DATA> read(int pageSize, int offset, Map<String, Object> object);

    List<DATA> read();

    /**
     * -------delete-------------
     */
    int delete(DATA data);

    int delete(Map<String, Object> objects);

    int delete(List<DATA> datas);

    int delete();

    /**
     * --------count--------------
     */
    int count();

    int count(DATA data);

    int count(Map<String, Object> objects);
}
