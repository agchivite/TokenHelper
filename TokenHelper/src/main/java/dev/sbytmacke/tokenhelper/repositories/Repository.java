package dev.sbytmacke.tokenhelper.repositories;

import java.util.ArrayList;

public interface Repository<T, ID> {
    T addItem(T item);
    
    ArrayList<T> findAll();
}

