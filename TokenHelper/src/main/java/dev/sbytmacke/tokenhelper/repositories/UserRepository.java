package dev.sbytmacke.tokenhelper.repositories;

import dev.sbytmacke.tokenhelper.dto.UserDTO;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository<U, N> extends Repository<U, N> {

    List<UserDTO> getAllByTime(N newTime);

    List<UserDTO> getAllByDate(LocalDate newDate);

    List<UserDTO> getAllByDateTime(String newTime, LocalDate newDate);

    List<U> getAllEntity();

    List<U> getAllEntityByTime(N newTime);

    List<U> getAllEntityByDate(LocalDate newDate);

    List<U> getAllEntityByDateTime(N newTime, LocalDate newDate);


    Integer getGlobalTotalBetsByTime(String newTime);

    Integer getGlobalTotalBetsByDateTime(String newTime, LocalDate newDate);

    Integer getGlobalTotalBetsByDate(LocalDate newDate);

    List<N> getAllUsernamesWithoutRepeat();

    void deleteItem(U user);
}

