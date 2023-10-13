package dev.sbytmacke.tokenhelper.repositories;

import dev.sbytmacke.tokenhelper.dto.UserDTO;

import java.time.LocalDate;
import java.util.List;

public interface UserRepository<T, ID> extends Repository<T, ID> {

    List<UserDTO> getAllByTime(ID newTime);

    List<UserDTO> getAllByDateTime(String newTime, LocalDate newDate);

    List<UserDTO> getByUsernameTime(ID newUsername, String newTime);

    List<UserDTO> getByUsernameDateTime(ID newUsername, String newTime, LocalDate newDate);

    Integer getGlobalTotalBetsByTime(String newTime);

    Integer getGlobalTotalBetsByDateTime(String newTime, LocalDate newDate);
}

