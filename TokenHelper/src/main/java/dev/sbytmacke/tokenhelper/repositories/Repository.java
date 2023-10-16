package dev.sbytmacke.tokenhelper.repositories;

import dev.sbytmacke.tokenhelper.dto.UserDTO;

import java.util.List;

public interface Repository<T, ID> {
    T addItem(T item);

    List<UserDTO> getAll();
}

