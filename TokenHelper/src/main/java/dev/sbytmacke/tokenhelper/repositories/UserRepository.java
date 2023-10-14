package dev.sbytmacke.tokenhelper.repositories;

import dev.sbytmacke.tokenhelper.dto.UserDTO;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository<U, N> extends Repository<U, N> {

    List<UserDTO> getAllByTime(N newTime);

    List<UserDTO> getAllByDateTime(String newTime, LocalDate newDate);

    Integer getGlobalTotalBetsByTime(String newTime);

    Integer getGlobalTotalBetsByDateTime(String newTime, LocalDate newDate);

    List<N> getAllUsernamesNoRepeat();

    Integer getGlobalTotalBetsByDate(LocalDate newDate);

    List<UserDTO> getAllByDate(LocalDate newDate);
}

