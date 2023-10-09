package dev.sbytmacke.tokenhelper.viewmodel;

import dev.sbytmacke.tokenhelper.models.User;
import dev.sbytmacke.tokenhelper.repositories.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UserViewModel {
    private final Repository<User, String> repository;
    Logger logger = LoggerFactory.getLogger(getClass());

    // Gestión del estado de la vista de creación y edición


    public UserViewModel(Repository<User, String> repository) {
        this.repository = repository;
    }

    public User saveUser(User user) {
        // Check si es correcto el USER
        repository.addItem(user);
        return user;
    }

    public List<String> getAllSliceHours() {
        logger.info("Getting all slice hours");

        List<String> sliceHours = new ArrayList<>();
        LocalTime startTime = LocalTime.of(0, 1); // Hora de inicio a las 00:01

        while (true) { // Bucle infinito para generar todas las franjas
            LocalTime endTime = startTime.plusMinutes(59); // Agregar 59 minutos
            String timeRange = startTime.format(DateTimeFormatter.ofPattern("HH:mm")) + "-" +
                    endTime.format(DateTimeFormatter.ofPattern("HH:mm"));
            sliceHours.add(timeRange);

            if (startTime.equals(LocalTime.of(23, 1))) {
                break; // Salir del bucle después de la franja 23:01-00:00
            }

            startTime = endTime.plusMinutes(1); // Agregar 1 minuto para comenzar en el siguiente minuto

            if (startTime.equals(LocalTime.of(0, 0))) {
                break; // Salir del bucle después de la franja 23:01-00:00
            }
        }

        return sliceHours;
    }
}
