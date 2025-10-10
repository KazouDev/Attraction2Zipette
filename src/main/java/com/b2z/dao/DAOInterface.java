package com.b2z.dao;

import com.b2z.model.Attraction;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public interface DAOInterface<T> {
    List<T> findAll();
    T findById(@NotNull int id);
    T create(@NotNull Map<String, Object> props);
    T delete(@NotNull int id);
}
