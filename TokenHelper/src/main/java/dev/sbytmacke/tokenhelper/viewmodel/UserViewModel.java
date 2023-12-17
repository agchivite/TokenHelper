package dev.sbytmacke.tokenhelper.viewmodel;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.repositories.UserRepository;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class UserViewModel {
    public static final int USER_FILTER_RELABLE = 60;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository<UserEntity, String> repository;
    public double goodAverageAllUsersSuccessRate;
    public double badAverageAllUsersSuccessRate;
    public int medianTotalBets;
    private Boolean firstRun = true;
    private double averageSuccessRate;

    public UserViewModel(UserRepository<UserEntity, String> repository) {
        logger.info("Initializing UserViewModel");
        this.repository = repository;
        calculateThirdsSuccessRate();
        calculateMedianTotalBets(repository.getAll());
    }


    private void calculateThirdsSuccessRate() {
        averageSuccessRate = getAverageSuccessRate();
        double portionSuccessRate = averageSuccessRate / 3;
        badAverageAllUsersSuccessRate = averageSuccessRate - portionSuccessRate;
        goodAverageAllUsersSuccessRate = averageSuccessRate + portionSuccessRate;
        System.out.println("badThirdSuccessRate: " + badAverageAllUsersSuccessRate);
        System.out.println("goodSuccessRate: " + goodAverageAllUsersSuccessRate);
        System.out.println("medianSuccessRate: " + averageSuccessRate);
    }

    public void saveUser(UserEntity userEntity) {
        repository.addItem(userEntity);
    }

    public List<UserDTO> getAllByTime(String newTime) {
        logger.info("getAllByTime");
        return repository.getAllByTime(newTime);
    }

    public List<UserDTO> getAllByDateTime(String newTime, Integer newDate) {
        logger.info("getAllByDateTime");
        return repository.getAllByDateTime(newTime, newDate);
    }

    public Integer getGlobalTotalBetsByTime(String newTime) {
        logger.info("getGlobalTotalBetsByTime");
        return repository.getGlobalTotalBetsByTime(newTime);
    }

    public double getGlobalPercentSuccessByTime(String newTime) {
        logger.info("getGlobalPercentSuccessByTime");
        List<UserDTO> users = repository.getAllByTime(newTime);

        if (users.isEmpty()) {
            return 0.0;
        }

        return calculateMedianTotalBets(users);
    }

    public Integer getGlobalTotalBetsByDateTime(String newTime, Integer newDate) {
        logger.info("getGlobalTotalBetsByDateTime");
        return repository.getGlobalTotalBetsByDateTime(newTime, newDate);
    }

    public double getGlobalPercentSuccessByDateTime(String newTime, Integer newDate) {
        logger.info("getGlobalPercentSuccessByDateTime");
        List<UserDTO> users = repository.getAllByDateTime(newTime, newDate);

        if (users.isEmpty()) {
            return 0.0;
        }

        return calculateMedianTotalBets(users);
    }

    public List<String> getAllUsernamesNoRepeat() {
        logger.info("getAllUsernames");
        return repository.getAllUsernamesWithoutRepeat();
    }

    public Integer getGlobalTotalBetsByDate(Integer newDate) {
        logger.info("getGlobalTotalBetsByDate");
        return repository.getGlobalTotalBetsByDate(newDate);
    }

    public double getGlobalPercentSuccessByDate(Integer newDate) {
        logger.info("getGlobalPercentSuccessByDate");
        List<UserDTO> users = repository.getAllByDate(newDate);

        if (users.isEmpty()) {
            return 0.0;
        }

        return calculateMedianTotalBets(users);
    }

    public List<UserDTO> getAllByDate(Integer newDate) {
        logger.info("getAllByDate");
        return repository.getAllByDate(newDate);
    }

    public List<UserDTO> getAll() {
        logger.info("getAll");
        List<UserDTO> users = repository.getAll();

        if (users.isEmpty()) {
            return Collections.emptyList();
        }

        return users;
    }

    public List<UserEntity> getAllEntityByDateTime(String newTime, LocalDate newDate) {
        logger.info("getAllEntityByDateTime");
        return repository.getAllEntityByDateTime(newTime, newDate);
    }

    public List<UserEntity> getAllEntityByTime(String newTime) {
        logger.info("getAllEntityByTime");
        return repository.getAllEntityByTime(newTime);
    }

    public List<UserEntity> getAllEntityByDate(LocalDate newDate) {
        logger.info("getAllEntityByDate");
        return repository.getAllEntityByDate(newDate);
    }

    public List<UserEntity> getAllEntity() {
        logger.info("getAllEntity");
        return repository.getAllEntity();
    }

    public void deleteUser(UserEntity user) {
        logger.info("deleteUser");
        repository.deleteItem(user);
    }

    public void updateUsername(String oldUsername, String newUsername) {
        logger.info("updateUsername");
        repository.updateUsername(oldUsername, newUsername);
    }

    public List<Document> backupData() {
        logger.info("backupData");
        return repository.backupData();
    }

    public List<UserEntity> getAllBetsByUser(String username) {
        logger.info("getAllBetsByUser");
        return repository.getAllBetsByUser(username);
    }

    private double getAverageSuccessRate() {
        logger.info("getAverageSuccessRate");
        List<UserDTO> users = repository.getAll();

        if (users.isEmpty()) {
            return 0.0;
        }

        double totalPercentSuccess = 0.0;
        for (UserDTO user : users) {
            totalPercentSuccess += user.getPercentReliable();
        }

        // Redondea al entero más cercano
        return Math.round(totalPercentSuccess / users.size() * 100) / 100.0;
    }

    private double calculateMedianTotalBets(List<UserDTO> allUsers) {
        int numUsers = allUsers.size();

        List<UserDTO> sortedAllUsers = allUsers.stream()
                .sorted(Comparator.comparing(UserDTO::getTotalBets))
                .toList();

        // Calcula la mediana de los valores seleccionados
        double medianValue;
        if (numUsers % 2 == 0) {
            int middle = numUsers / 2;
            double value1 = sortedAllUsers.get(middle - 1).getTotalBets();
            double value2 = sortedAllUsers.get(middle).getTotalBets();
            medianValue = (value1 + value2) / 2.0;
        } else {
            medianValue = sortedAllUsers.get(numUsers / 2).getTotalBets();
        }

        if (Boolean.TRUE.equals(firstRun)) {
            medianTotalBets = (int) Math.round(medianValue);
            firstRun = false;
        }

        return medianTotalBets;
    }

    public void refreshData() {
        logger.info("refreshData");
        calculateThirdsSuccessRate();
        calculateMedianTotalBets(repository.getAll());
    }
}
