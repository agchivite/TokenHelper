package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.utils.TimeUtils;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

public class DeleteMenuViewController {
    private final String noDataTime = "--:--";

    Logger logger = LoggerFactory.getLogger(getClass());
    private UserViewModel userViewModel;

    /* Filter */
    @FXML
    private TextField textSearchUserFilter;
    @FXML
    private DatePicker datePickerFilter;
    @FXML
    private ComboBox<String> comboTimeFilter;
    @FXML
    private RadioButton radioButtonReliableFilter;
    @FXML
    private RadioButton radioButtonNoReliableFilter;

    /* Table */
    @FXML
    private TableView<UserEntity> tableUsers;
    @FXML
    private TableColumn<UserEntity, String> columnUsername;
    @FXML
    private TableColumn<UserEntity, String> columnTime;
    @FXML
    private TableColumn<UserEntity, LocalDate> columnDate;
    @FXML
    private TableColumn<UserEntity, Boolean> columnReliable;
/*    @FXML
    private TableColumn<UserDTO, String> columnDelete;*/

    public void init(UserViewModel userViewModel) {
        logger.info("Initializing MainViewController");
        this.userViewModel = userViewModel;
        //initEvents();
        initBindings();
    }

    private void initBindings() {
        logger.info("Initializing Bindings");

        // ComboTimeFilter
        comboTimeFilter.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTimeFilter.getSelectionModel().select(24);

        // Table, mediante el State hacerlo
        //tableUsers.setItems(FXCollections.observableArrayList(userViewModel.getAllUsers()));

        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("dateBet"));
        columnTime.setCellValueFactory(new PropertyValueFactory<>("timeBet"));
        columnReliable.setCellValueFactory(new PropertyValueFactory<>("reliable"));

        ToggleGroup toggleGroup = new ToggleGroup();
        radioButtonReliableFilter.setToggleGroup(toggleGroup);
        radioButtonNoReliableFilter.setToggleGroup(toggleGroup);
    }

}
