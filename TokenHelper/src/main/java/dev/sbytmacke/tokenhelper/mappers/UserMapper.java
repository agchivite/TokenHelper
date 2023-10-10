package dev.sbytmacke.tokenhelper.mappers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserMapper {
    private final UserRepository<UserEntity, String> userRepository;

    public UserMapper(UserRepository<UserEntity, String> userRepository) {
        this.userRepository = userRepository;
    }

    public UserDTO convertUserEntityToDTO(UserEntity userEntity) {

        int totalBets = userRepository.calculateTotalBetsByUsername(userEntity.getUsername());
        String percentSuccess = userRepository.calculatePercentSuccess(userEntity, totalBets);

        // Crear un UserDTO con los valores calculados
        return new UserDTO(userEntity.getUsername(), percentSuccess, totalBets);
    }

    public List<UserDTO> convertUserEntitiesToDTOs(List<UserEntity> userEntities) {
        Set<String> processedUsernames = new HashSet<>();
        List<UserDTO> userDTOs = new ArrayList<>();

        for (UserEntity userEntity : userEntities) {
            String username = userEntity.getUsername();

            // Verificar si ya procesamos a este usuario
            if (!processedUsernames.contains(username)) {
                // Marcar el usuario como procesado
                processedUsernames.add(username);

                // Utiliza la función convertUserEntityToDTO para obtener el UserDTO
                UserDTO userDTO = convertUserEntityToDTO(userEntity);
                userDTOs.add(userDTO);
            }
        }

        return userDTOs;
    }
}
