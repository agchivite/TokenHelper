package dev.sbytmacke.tokenhelper.viewmodel;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.mappers.UserMapper;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.repositories.UserRepository;
import dev.sbytmacke.tokenhelper.state.UserState;
import javafx.beans.property.SimpleObjectProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserViewModel {
    private final SimpleObjectProperty<UserState> userStateProperty = new SimpleObjectProperty<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository<UserEntity, String> repository;
    private final UserMapper userMapper;

    public UserViewModel(UserRepository<UserEntity, String> repository) {
        logger.info("Initializing UserViewModel");
        this.repository = repository;
        List<UserEntity> userEntities = repository.findAll();

        for (UserEntity userEntity : userEntities) {
            logger.info("User: " + userEntity.getUsername());
        }

        this.userMapper = new UserMapper(repository);
        List<UserDTO> userDTOs = userMapper.convertUserEntitiesToDTOs(userEntities);

        logger.info("Initializing UserStateProperty");
        UserState userState = new UserState(userDTOs);
        userStateProperty.setValue(userState);
    }

    public UserEntity saveUser(UserEntity userEntity) {
        repository.addItem(userEntity);
        List<UserDTO> currentUsers = userMapper.convertUserEntitiesToDTOs(repository.findAll());

        // Crea un nuevo UserState con el nuevo usuario
        UserState updatedState = new UserState(currentUsers);

        // Establece el nuevo valor en userStateProperty
        userStateProperty.setValue(updatedState);

        return userEntity;
    }


    public SimpleObjectProperty<UserState> getUserStateProperty() {
        return userStateProperty;
    }
}
