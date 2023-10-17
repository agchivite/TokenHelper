package dev.sbytmacke.tokenhelper.viewmodel;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;

public class UserViewModel {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final UserRepository<UserEntity, String> repository;

    public UserViewModel(UserRepository<UserEntity, String> repository) {
        logger.info("Initializing UserViewModel");
        this.repository = repository;
    }

    private double calculateGlobalAverageSuccess(List<UserDTO> users) {
        int totalSuccess = 0;
        int totalBets = 0;

        for (UserDTO user : users) {
            totalSuccess += user.getTotalSuccess();
            totalBets += user.getTotalBets();
        }

        // Comprobar si la suma total es cero
        if (totalBets == 0) {
            return 0.0;
        }

        double totalPercentSuccess = (double) totalSuccess * 100 / totalBets * 100;

        return Math.round(totalPercentSuccess) / 100.0;
    }


    public void saveUser(UserEntity userEntity) {
        repository.addItem(userEntity);
    }

    public List<UserDTO> getAllByTime(String newTime) {
        logger.info("getAllByTime");
        return repository.getAllByTime(newTime);
    }

    public List<UserDTO> getAllByDateTime(String newTime, LocalDate newDate) {
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

        return calculateGlobalAverageSuccess(users);
    }

    public Integer getGlobalTotalBetsByDateTime(String newTime, LocalDate newDate) {
        logger.info("getGlobalTotalBetsByDateTime");
        return repository.getGlobalTotalBetsByDateTime(newTime, newDate);
    }

    public double getGlobalPercentSuccessByDateTime(String newTime, LocalDate newDate) {
        logger.info("getGlobalPercentSuccessByDateTime");
        List<UserDTO> users = repository.getAllByDateTime(newTime, newDate);

        if (users.isEmpty()) {
            return 0.0;
        }

        return calculateGlobalAverageSuccess(users);
    }

    public List<String> getAllUsernamesNoRepeat() {
        logger.info("getAllUsernames");
        return repository.getAllUsernamesWithoutRepeat();
    }

    public Integer getGlobalTotalBetsByDate(LocalDate newDate) {
        logger.info("getGlobalTotalBetsByDate");
        return repository.getGlobalTotalBetsByDate(newDate);
    }

    public double getGlobalPercentSuccessByDate(LocalDate newDate) {
        logger.info("getGlobalPercentSuccessByDate");
        List<UserDTO> users = repository.getAllByDate(newDate);

        if (users.isEmpty()) {
            return 0.0;
        }

        return calculateGlobalAverageSuccess(users);
    }

    public List<UserDTO> getAllByDate(LocalDate newDate) {
        logger.info("getAllByDate");
        return repository.getAllByDate(newDate);
    }

    public List<UserDTO> getAll() {
        logger.info("getAll");
        List<UserDTO> users = repository.getAll();

        if (users.isEmpty()) {
            return null;
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
}
