package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDetailController {

    private UserViewModel userViewModel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label bestDay;
    @FXML
    private Label bestHour;
    @FXML
    private Label bestDayHour;

    public void init(UserViewModel userViewModel, UserDTO user) {
        this.userViewModel = userViewModel;

        // 1. Necesitamos todas las apuestas del usuario
        List<UserEntity> listAllBets = userViewModel.getAllBetsByUser(user.getUsername());
        // 2.1 Realizar un % de acierto mediante un mapa, de las apuestas que acierta por día
        /* clave: día, valor: apuestas acertadas */

        // Agrupamos total de apuestas por día, para posteriormente compararlo con la de aciertos
        Map<Integer, Integer> mapTotalBetsByDay = new HashMap<>();
        Map<Integer, Integer> mapTotalSuccessByDay = new HashMap<>();

        for (UserEntity bet : listAllBets) {
            LocalDate betDate = bet.getDateBet();
            int dayOfWeek = betDate.getDayOfWeek().getValue();

            // Si no tenemos un día lo inicializamos a 0, creamos un nuevo día clave-valor
            if (!mapTotalBetsByDay.containsKey(dayOfWeek)) {
                mapTotalBetsByDay.put(dayOfWeek, 0);
                mapTotalSuccessByDay.put(dayOfWeek, 0);
            }

            mapTotalBetsByDay.put(dayOfWeek, mapTotalBetsByDay.get(dayOfWeek) + 1);
            if (bet.getReliable()) {
                mapTotalSuccessByDay.put(dayOfWeek, mapTotalSuccessByDay.get(dayOfWeek) + 1);
            }
        }

        // Con los mapas anteriores comprobamos el % de acierto por día
        Map<Integer, Double> mapPercentSuccessByDay = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : mapTotalSuccessByDay.entrySet()) {
            int dayOfWeek = entry.getKey();
            int totalBets = mapTotalBetsByDay.get(dayOfWeek);
            int totalSuccess = entry.getValue();

            if (totalBets == 0) {
                mapPercentSuccessByDay.put(dayOfWeek, 0.0);
            } else {
                double totalPercentSuccess = (double) totalSuccess * 100 / totalBets * 100;
                mapPercentSuccessByDay.put(dayOfWeek, Math.round(totalPercentSuccess) / 100.0);
            }
        }

        // Encontrar el día con el mayor porcentaje de aciertos
        int dayWithMaxSuccess = -1;
        double maxSuccessRate = -1.0;

        for (Map.Entry<Integer, Double> entry : mapPercentSuccessByDay.entrySet()) {
            int dayOfWeek = entry.getKey();
            double successRate = entry.getValue();

            if (successRate > maxSuccessRate) {
                maxSuccessRate = successRate;
                dayWithMaxSuccess = dayOfWeek;
            }
        }

        String dayOfWeek;
        String dayNameWithMaxSuccess = DayOfWeek.of(dayWithMaxSuccess).toString();

        dayOfWeek = switch (dayNameWithMaxSuccess) {
            case "MONDAY" -> "LUNES";
            case "TUESDAY" -> "MARTES";
            case "WEDNESDAY" -> "MIÉRCOLES";
            case "THURSDAY" -> "JUEVES";
            case "FRIDAY" -> "VIERNES";
            case "SATURDAY" -> "SÁBADO";
            default -> "DOMINGO";
        };
        bestDay.setText(dayOfWeek);

        // 2.2 Realizar un % de acierto mediante un mapa, de las apuestas que acierta por hora
        /* clave: hora, valor: apuestas acertadas */

        // 2.3 Realizar un % de acierto mediante un mapa, de las apuestas que acierta por día-hora
        /* clave: día-hora, valor: apuestas acertadas */

        // 3. Seleccionar aquellos días, horas y día-hora que más % de acierto tenga


        usernameLabel.setText(user.getUsername());
        //bestDay.setText(String.valueOf(user.getPercentReliable()));
        bestHour.setText("No implementado");
        bestDayHour.setText("No implementado");
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
    }
}
