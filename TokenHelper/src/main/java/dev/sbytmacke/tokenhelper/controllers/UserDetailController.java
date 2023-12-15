package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

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

    private static Map<Integer, Double> getMapSuccessByDay(List<UserEntity> bets) {
        Map<Integer, Double> mapSuccessRateByDay = new HashMap<>();

        for (UserEntity bet : bets) {
            LocalDate betDate = bet.getDateBet();
            int dayOfWeek = betDate.getDayOfWeek().getValue();

            // Contar aciertos y apuestas por día
            mapSuccessRateByDay.putIfAbsent(dayOfWeek, 0.0);
            double successRate = Boolean.TRUE.equals(bet.getReliable()) ? 1.0 : 0.0;
            mapSuccessRateByDay.put(dayOfWeek, mapSuccessRateByDay.get(dayOfWeek) + successRate);
        }
        return mapSuccessRateByDay;
    }

    public void init(UserViewModel userViewModel, UserDTO user) {
        this.userViewModel = userViewModel;

        usernameLabel.setText(user.getUsername());

        List<UserEntity> listAllBetsOnlyByOneUser = userViewModel.getAllBetsByUser(user.getUsername());
        int totalBets = listAllBetsOnlyByOneUser.size();

        setBestDay(listAllBetsOnlyByOneUser, totalBets);
        setBestHour(listAllBetsOnlyByOneUser, totalBets);
        setBestDayHour(listAllBetsOnlyByOneUser, totalBets);
    }

    private List<Integer> getBestDay(List<UserEntity> bets, int averageBetsPerDay) {
        Map<Integer, Double> mapSuccessRateByDay = getMapSuccessByDay(bets);

        // Días con mayor porcentaje de aciertos, es una lista porque puede haber varios días con el mismo porcentaje
        List<Integer> bestDaysOfThwWeek = getDayWithMaxSuccess(mapSuccessRateByDay);

        // Filtramos los mejores días para ver si corresponden con el promedio de apuestas
        List<Integer> filteredDaysByAverageBetsPerDay = new ArrayList<>();
        for (Integer dayOfWeek : bestDaysOfThwWeek) {
            int betsOnDay = countBetsOnDay(bets, dayOfWeek);

            if (betsOnDay >= averageBetsPerDay) {
                filteredDaysByAverageBetsPerDay.add(dayOfWeek);
            }
        }

        // Si no hay ninguno, escogemos los que tengan más aciertos
        if (filteredDaysByAverageBetsPerDay.isEmpty()) {
            filteredDaysByAverageBetsPerDay.addAll(bestDaysOfThwWeek);
        }

        return filteredDaysByAverageBetsPerDay;
    }

    private int countBetsOnDay(List<UserEntity> bets, int dayOfWeek) {
        return (int) bets.stream()
                .filter(bet -> bet.getDateBet().getDayOfWeek().getValue() == dayOfWeek)
                .count();
    }

    private List<Integer> getDayWithMaxSuccess(Map<Integer, Double> successRateByDay) {
        int bestDayOfWeekNumber = -1;
        double maxSuccessRate = -1.0;

        for (Map.Entry<Integer, Double> entry : successRateByDay.entrySet()) {
            int dayOfWeek = entry.getKey();
            double successRate = entry.getValue();

            if (successRate > maxSuccessRate) {
                maxSuccessRate = successRate;
                bestDayOfWeekNumber = dayOfWeek;
            }
        }

        // Comprobamos si hay otros días con el mismo porcentaje de aciertos
        List<Integer> bestDaysOfWeek = new ArrayList<>();
        bestDaysOfWeek.add(bestDayOfWeekNumber);
        for (Map.Entry<Integer, Double> entry : successRateByDay.entrySet()) {
            int dayOfWeek = entry.getKey();
            double successRate = entry.getValue();

            if (successRate == maxSuccessRate && dayOfWeek != bestDayOfWeekNumber) {
                bestDaysOfWeek.add(dayOfWeek);
            }
        }

        return bestDaysOfWeek;
    }

    private void setBestDayHour(List<UserEntity> listAllBetsOnlyByOneUser, int totalBets) {
        // TODO: Implementar
        bestDayHour.setText("No implementado");
    }

    private void setBestHour(List<UserEntity> listAllBetsOnlyByOneUser, int totalBets) {
        int uniqueHoursWithBets = countUniqueHoursWithBets(listAllBetsOnlyByOneUser);
        int averageBetsPerHour = uniqueHoursWithBets > 0 ? Math.round((float) totalBets / uniqueHoursWithBets) : 0;
        List<String> bestHourNumber = getBestHour(listAllBetsOnlyByOneUser, averageBetsPerHour);

        bestHour.setText(String.join(", ", bestHourNumber));
    }

    private void setBestDay(List<UserEntity> listAllBetsOnlyByOneUser, int totalBets) {
        int uniqueDaysWithBets = countUniqueDaysOfWeekWithBets(listAllBetsOnlyByOneUser);
        int averageBetsPerDay = uniqueDaysWithBets > 0 ? Math.round((float) totalBets / uniqueDaysWithBets) : 0;
        List<Integer> bestDayNumber = getBestDay(listAllBetsOnlyByOneUser, averageBetsPerDay);

        // Traducción de los días
        List<String> bestDays = new ArrayList<>();
        for (Integer dayNumber : bestDayNumber) {
            String dayNameWithMaxSuccess = DayOfWeek.of(dayNumber).toString();
            String dayOfWeek = switch (dayNameWithMaxSuccess) {
                case "MONDAY" -> "LUNES";
                case "TUESDAY" -> "MARTES";
                case "WEDNESDAY" -> "MIÉRCOLES";
                case "THURSDAY" -> "JUEVES";
                case "FRIDAY" -> "VIERNES";
                case "SATURDAY" -> "SÁBADO";
                case "SUNDAY" -> "DOMINGO";
                default -> "ERROR";
            };
            bestDays.add(dayOfWeek);
        }

        bestDay.setText(String.join(", ", bestDays));
    }

    private int countUniqueDaysOfWeekWithBets(List<UserEntity> bets) {
        Set<DayOfWeek> uniqueDays = new HashSet<>();

        for (UserEntity bet : bets) {
            DayOfWeek dayOfWeek = bet.getDateBet().getDayOfWeek();
            uniqueDays.add(dayOfWeek);
        }

        return uniqueDays.size();
    }

    private Map<String, Double> getMapSuccessByHour(List<UserEntity> bets) {
        Map<String, Double> mapSuccessRateByHour = new HashMap<>();

        for (UserEntity bet : bets) {
            String betHour = bet.getTimeBet();

            // Contar aciertos y apuestas por hora
            mapSuccessRateByHour.putIfAbsent(betHour, 0.0);
            double successRate = Boolean.TRUE.equals(bet.getReliable()) ? 1.0 : 0.0;
            mapSuccessRateByHour.put(betHour, mapSuccessRateByHour.get(betHour) + successRate);
        }

        return mapSuccessRateByHour;
    }

    private List<String> getBestHour(List<UserEntity> bets, int averageBetsPerHour) {
        Map<String, Double> mapSuccessRateByHour = getMapSuccessByHour(bets);

        List<String> bestHours = getHourWithMaxSuccess(mapSuccessRateByHour);

        // Filtrar horas con igual o más apuestas que el promedio
        List<String> filteredHours = new ArrayList<>();
        for (String hour : bestHours) {
            int betsOnDay = countBetsOnHour(bets, hour);

            if (betsOnDay >= averageBetsPerHour) {
                filteredHours.add(hour);
            }
        }

        // Si no hay ninguno, escogemos los que tengan más aciertos
        if (filteredHours.isEmpty()) {
            filteredHours.addAll(bestHours);
        }

        // Encontrar la hora con el mayor porcentaje de aciertos entre las filtradas
        return filteredHours;
    }

    private int countBetsOnHour(List<UserEntity> bets, String betHour) {
        return (int) bets.stream()
                .filter(bet -> bet.getTimeBet().equals(betHour))
                .count();
    }

    private List<String> getHourWithMaxSuccess(Map<String, Double> successRateByHour) {
        String bestHour = null;
        double maxSuccessRate = -1.0;

        for (Map.Entry<String, Double> entry : successRateByHour.entrySet()) {
            String hour = entry.getKey();
            double successRate = entry.getValue();

            if (successRate > maxSuccessRate) {
                maxSuccessRate = successRate;
                bestHour = hour;
            }
        }

        // Comprobamos si hay otras horas con el mismo porcentaje de aciertos
        List<String> bestHours = new ArrayList<>();
        bestHours.add(bestHour);
        for (Map.Entry<String, Double> entry : successRateByHour.entrySet()) {
            String hour = entry.getKey();
            double successRate = entry.getValue();

            if (successRate == maxSuccessRate && !hour.equals(bestHour)) {
                bestHours.add(hour);
            }
        }

        return bestHours;
    }

    private int countUniqueHoursWithBets(List<UserEntity> bets) {
        Set<String> uniqueHours = new HashSet<>();

        for (UserEntity bet : bets) {
            uniqueHours.add(bet.getTimeBet());
        }

        return uniqueHours.size();
    }


    @FXML
    private void closeWindow() {
        Stage stage = (Stage) usernameLabel.getScene().getWindow();
        stage.close();
    }
}
