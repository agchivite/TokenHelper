package dev.sbytmacke.tokenhelper.repositories;

import dev.sbytmacke.tokenhelper.models.UserEntity;

import java.util.List;

public interface Repository<T, ID> {
    T addItem(T item);

    List<UserEntity> findAll();
}

