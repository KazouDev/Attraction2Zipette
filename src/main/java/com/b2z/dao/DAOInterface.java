package com.b2z.dao;

import java.util.List;

public interface DAOInterface<T> {
    List<T> findAll();
    T findById(int id);
    void create();
    void delete();
}
