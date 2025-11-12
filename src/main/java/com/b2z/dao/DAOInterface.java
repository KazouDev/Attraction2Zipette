package com.b2z.dao;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface DAOInterface<T, U> {
    List<T> findAll();
    T findById(@NotNull int id);
    IdResponse create(@NotNull U props);
    IdResponse delete(@NotNull int id);
}
