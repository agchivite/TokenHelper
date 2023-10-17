package dev.sbytmacke.tokenhelper.mappers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserMapper {
    private static int calculateSuccessfulBets(List<UserEntity> userEntities, String username) {
        int successfulBets = 0;
        for (UserEntity userEntity : userEntities) {
            if (userEntity.getUsername().equals(username) && userEntity.getReliable()) {
                successfulBets++;
            }
        }
        return successfulBets;
    }

    public List<UserDTO> convertUserEntitiesToDTOs(List<UserEntity> userEntities) {
        Set<String> processedUsernames = new HashSet<>();
        ArrayList<UserDTO> userDTOs = new ArrayList<>();

        for (UserEntity userEntity : userEntities) {
            String username = userEntity.getUsername();

            // Verificar si ya procesamos a este usuario
            if (!processedUsernames.contains(username)) {
                // Marcar el usuario como procesado
                processedUsernames.add(username);

                // Calcular el total de apuestas y el porcentaje de éxito para el usuario
                int totalSuccessfulBets = calculateSuccessfulBets(userEntities, username);
                int totalBets = calculateTotalBets(userEntities, username);
                double percentSuccess = calculatePercentSuccess(totalBets, totalSuccessfulBets);

                // Crear un UserDTO con los valores calculados
                UserDTO userDTO = new UserDTO(username, percentSuccess, totalBets, totalSuccessfulBets);
                userDTOs.add(userDTO);
            }
        }

        return userDTOs;
    }

    private int calculateTotalBets(List<UserEntity> userEntities, String username) {
        int totalBets = 0;
        for (UserEntity userEntity : userEntities) {
            if (userEntity.getUsername().equals(username)) {
                totalBets++;
            }
        }
        return totalBets;
    }

    private double calculatePercentSuccess(int totalBets, double successfulBets) {
        if (totalBets > 0) {
            double percentValue = (successfulBets / totalBets) * 100;
            DecimalFormat df = new DecimalFormat("#.##"); // Establecemos el formato a dos decimales
            return percentValue; //df.format(percentValue); // Redondeamos el valor y se convierte a String
        } else {
            return 0.0; // Evitar división por cero
        }
    }
}
