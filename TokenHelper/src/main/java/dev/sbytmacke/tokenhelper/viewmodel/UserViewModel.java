package dev.sbytmacke.tokenhelper.viewmodel;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;

public class UserViewModel {
    //private final SimpleObjectProperty<UserState> userStateProperty = new SimpleObjectProperty<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository<UserEntity, String> repository;

    public UserViewModel(UserRepository<UserEntity, String> repository) {
        logger.info("Initializing UserViewModel");
        this.repository = repository;
    }

    public UserEntity saveUser(UserEntity userEntity) {
        repository.addItem(userEntity);
/*        List<UserDTO> currentUsers = repository.findAll();

        // Crea un nuevo UserState con el nuevo usuario
        UserState updatedState = new UserState(currentUsers);

        // Establece el nuevo valor en userStateProperty
        userStateProperty.setValue(updatedState);*/

        return userEntity;
    }

/*    public SimpleObjectProperty<UserState> getUserStateProperty() {
        return userStateProperty;
    }*/

    public ArrayList<UserDTO> getAllByTime(String newTime) {
        logger.info("getAllByTime");
        return repository.getAllByTime(newTime);
    }

    public ArrayList<UserDTO> getAllByDateTime(String newTime, LocalDate newDate) {
        logger.info("getAllByDateTime");
        return repository.getAllByDateTime(newTime, newDate);
    }

    public ArrayList<UserDTO> getByUsernameTime(String newUsername, String newTime) {
        logger.info("getByUsernameTime");
        return repository.getByUsernameTime(newUsername, newTime);
    }

    public ArrayList<UserDTO> getByUsernameDateTime(String newUsername, String newTime, LocalDate newDate) {
        logger.info("getByUsernameDateTime");
        return repository.getByUsernameDateTime(newUsername, newTime, newDate);
    }


}
