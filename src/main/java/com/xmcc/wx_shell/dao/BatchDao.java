package com.xmcc.wx_shell.dao;

import java.util.List;

public interface BatchDao<T> {
    void batchInsert(List<T> batchList);
}
