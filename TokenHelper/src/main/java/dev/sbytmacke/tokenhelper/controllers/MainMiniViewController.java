package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.utils.TimeUtils;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainMiniViewController {
    private final String noDataTime = "--:--";
    private final List<UserDTO> copyListFromMainView = new ArrayList<>();
    private UserViewModel userViewModel;
    private MainViewController mainViewController;
    private TableView<UserDTO> tableUsersMainView;
    @FXML
    private TableView<UserDTO> tableUsers;
    @FXML
    private TableColumn<UserDTO, String> columnUsername;
    @FXML
    private TableColumn<UserDTO, String> columnSuccess;
    @FXML
    private TableColumn<UserDTO, String> columnTotalBets;
    @FXML
    private ComboBox<String> comboTimeFilter;
    @FXML
    private RadioButton radioButtonHideTime;
    @FXML
    private RadioButton radioButtonNone;
    @FXML
    private RadioButton radioButtonMonday;
    @FXML
    private RadioButton radioButtonTuesday;
    @FXML
    private RadioButton radioButtonWednesday;
    @FXML
    private RadioButton radioButtonThursday;
    @FXML
    private RadioButton radioButtonFriday;
    @FXML
    private RadioButton radioButtonSaturday;
    @FXML
    private RadioButton radioButtonSunday;
    @FXML
    private Button buttonClearFilters;

    public void init(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
        this.userViewModel = mainViewController.getUserViewModel();
        this.tableUsersMainView = mainViewController.getTableUsers();
        initBindings();
        initDetails();
        initEvents();
    }

    private void initEvents() {
        radioButtonHideTime.setOnAction(event -> updateAllTables());

        buttonClearFilters.setOnAction(event -> {
            comboTimeFilter.getSelectionModel().select(0);
            radioButtonHideTime.setSelected(false);
            radioButtonNone.setSelected(true);
            updateAllTables();
        });

        comboTimeFilter.setOnAction(event -> updateAllTables());

        radioButtonNone.setOnAction(event -> updateAllTables());
        radioButtonMonday.setOnAction(event -> updateAllTables());
        radioButtonTuesday.setOnAction(event -> updateAllTables());
        radioButtonWednesday.setOnAction(event -> updateAllTables());
        radioButtonThursday.setOnAction(event -> updateAllTables());
        radioButtonFriday.setOnAction(event -> updateAllTables());
        radioButtonSaturday.setOnAction(event -> updateAllTables());
        radioButtonSunday.setOnAction(event -> updateAllTables());
    }

    private void initDetails() {
        centerAndFontTextTable();
        setColorsTable();
        tableUsers.setSelectionModel(null);

        comboTimeFilter.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm());
    }

    private void setColorsTable() {
        tableUsers.setRowFactory(tv -> new TableRow<>() {

            @Override
            protected void updateItem(UserDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    setStyle("");
                } else {
                    if (item.getPercentReliable() <= 49.00) {
                        setStyle("-fx-background-color: #ff6161;");
                    } else if (item.getPercentReliable() > 49.00 && item.getPercentReliable() <= 65.00) {
                        setStyle("-fx-background-color: orange;");
                    } else if (item.getPercentReliable() > 65.00) {
                        setStyle("-fx-background-color: #53db78;");
                    } else {
                        setStyle("-fx-background-color: #ffffff;");
                    }
                }
            }
        });
    }

    private void centerAndFontTextTable() {
        for (int i = 0; i < tableUsers.getColumns().size(); i++) {
            tableUsers.getColumns().get(i).setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 14px;");
        }
        tableUsers.getColumns().get(0).setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 15px; ");

    }

    private void initBindings() {

        radioButtonHideTime.setSelected(mainViewController.getRadioButtonHideTime().isSelected());

        comboTimeFilter.setDisable(radioButtonHideTime.isSelected());

        comboTimeFilter.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTimeFilter.getSelectionModel().select(mainViewController.getIndexComboTimeFilter());

        // Radio button group
        ToggleGroup toggleGroup = new ToggleGroup();
        radioButtonNone.setToggleGroup(toggleGroup);
        radioButtonMonday.setToggleGroup(toggleGroup);
        radioButtonTuesday.setToggleGroup(toggleGroup);
        radioButtonWednesday.setToggleGroup(toggleGroup);
        radioButtonThursday.setToggleGroup(toggleGroup);
        radioButtonFriday.setToggleGroup(toggleGroup);
        radioButtonSaturday.setToggleGroup(toggleGroup);
        radioButtonSunday.setToggleGroup(toggleGroup);
        if (mainViewController.getNewDateOfWeek() == null) {
            radioButtonNone.setSelected(true);
        } else {
            if (mainViewController.getNewDateOfWeek() == 1) {
                radioButtonMonday.setSelected(true);
            }
            if (mainViewController.getNewDateOfWeek() == 2) {
                radioButtonTuesday.setSelected(true);
            }
            if (mainViewController.getNewDateOfWeek() == 3) {
                radioButtonWednesday.setSelected(true);
            }
            if (mainViewController.getNewDateOfWeek() == 4) {
                radioButtonThursday.setSelected(true);
            }
            if (mainViewController.getNewDateOfWeek() == 5) {
                radioButtonFriday.setSelected(true);
            }
            if (mainViewController.getNewDateOfWeek() == 6) {
                radioButtonSaturday.setSelected(true);
            }
            if (mainViewController.getNewDateOfWeek() == 7) {
                radioButtonSunday.setSelected(true);
            }
        }

        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnSuccess.setCellValueFactory(new PropertyValueFactory<>("percentReliable"));
        columnTotalBets.setCellValueFactory(new PropertyValueFactory<>("totalBets"));

        ObservableList<UserDTO> userData = FXCollections.observableArrayList();
        if (tableUsersMainView != null) {

            copyListFromMainView.addAll(tableUsersMainView.getItems());
            // Eliminamos los rojos
            copyListFromMainView.removeIf(user -> user.getPercentReliable() <= 49.00);
            userData.addAll(copyListFromMainView);
        }

        // Asigna los datos a la tabla
        tableUsers.setItems(userData);
    }

    public void updateAllTables() {
        Integer newDateOfWeek = getNewDateOfWeek();
        String newTime;

        if (radioButtonHideTime.isSelected()) {
            newTime = noDataTime;
            comboTimeFilter.setDisable(true);
        } else {
            newTime = comboTimeFilter.getSelectionModel().getSelectedItem();
            comboTimeFilter.setDisable(false);
        }

        Boolean onFilterByDate = onFilterDataTableByDate(newTime, newDateOfWeek);
        Boolean onFilterByTime = onFilterDataTableByTime(newTime, newDateOfWeek);
        Boolean onFilterByDateTime = onFilterDataTableByDateTime(newTime, newDateOfWeek);

        mainViewController.orderByTotalSuccessBets(tableUsers);

        List<UserDTO> filteredUsers = mainViewController.filterRakingUsersReliable(tableUsers.getItems());

        mainViewController.setStarTopUsers(filteredUsers);

        if (!onFilterByDateTime && !onFilterByDate && !onFilterByTime) {
            tableUsers.getItems().clear();
        }
    }

    private Boolean onFilterDataTableByDateTime(String newTime, Integer newDate) {
        if (!newTime.equals(noDataTime) && newDate != null) {
            List<UserDTO> usersToShow = userViewModel.getAllByDateTime(newTime, newDate);

            // Eliminamos los rojos
            usersToShow.removeIf(user -> user.getPercentReliable() <= 49.00);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
            return true;
        }
        return false;
    }

    private Boolean onFilterDataTableByTime(String newTime, Integer newDate) {
        if (!newTime.equals(noDataTime) && newDate == null) {
            List<UserDTO> usersToShow = userViewModel.getAllByTime(newTime);

            // Eliminamos los rojos
            usersToShow.removeIf(user -> user.getPercentReliable() <= 49.00);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
            return true;
        }
        return false;
    }

    private Boolean onFilterDataTableByDate(String newTime, Integer newDateOfWeek) {
        if (newTime.equals(noDataTime) && newDateOfWeek != null) {
            List<UserDTO> usersToShow = userViewModel.getAllByDate(newDateOfWeek);


            // Eliminamos los rojos
            usersToShow.removeIf(user -> user.getPercentReliable() <= 49.00);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));

            return true;
        }
        return false;
    }

    private Integer getNewDateOfWeek() {
        Integer newDateOfWeek = null;

        if (radioButtonMonday.isSelected()) {
            newDateOfWeek = 1;
        }
        if (radioButtonTuesday.isSelected()) {
            newDateOfWeek = 2;
        }
        if (radioButtonWednesday.isSelected()) {
            newDateOfWeek = 3;
        }
        if (radioButtonThursday.isSelected()) {
            newDateOfWeek = 4;
        }
        if (radioButtonFriday.isSelected()) {
            newDateOfWeek = 5;
        }
        if (radioButtonSaturday.isSelected()) {
            newDateOfWeek = 6;
        }
        if (radioButtonSunday.isSelected()) {
            newDateOfWeek = 7;
        }
        return newDateOfWeek;
    }
}

