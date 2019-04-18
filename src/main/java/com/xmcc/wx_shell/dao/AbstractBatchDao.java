package com.xmcc.wx_shell.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * 批量插入接口开发
 * @param <T>
 */
public class AbstractBatchDao<T> implements BatchDao<T> {

    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void batchInsert(List<T> batchList) {
        int size = batchList.size();
        for (int i = 0; i < size; i++) {
            em.persist(batchList.get(i));
            if (i%100==0 || i==size-1){     //每100条执行一次写入数据库操作
                em.flush();
                em.clear();
            }
        }
    }
}
