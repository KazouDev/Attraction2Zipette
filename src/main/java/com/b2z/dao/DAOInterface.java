package com.b2z.dao;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface DAOInterface<T, U> {
    List<T> findAll();
    T findById(@NotNull int id);
    T create(@NotNull U props);
    T delete(@NotNull int id);
}
