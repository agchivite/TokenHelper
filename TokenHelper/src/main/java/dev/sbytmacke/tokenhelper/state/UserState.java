package dev.sbytmacke.tokenhelper.state;

import dev.sbytmacke.tokenhelper.dto.UserDTO;

import java.util.List;

public class UserState {
    private final List<UserDTO> userDTOS;

    public UserState(List<UserDTO> userDTOS) {
        this.userDTOS = userDTOS;
    }

    public List<UserDTO> getUsers() {
        return userDTOS;
    }
}
