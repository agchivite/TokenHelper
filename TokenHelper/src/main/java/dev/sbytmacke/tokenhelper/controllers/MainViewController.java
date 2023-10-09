package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.models.User;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class MainViewController {

    Logger logger = LoggerFactory.getLogger(getClass());
    private UserViewModel userViewModel;
    /* Create user */
    @FXML
    private TextField textFieldUser;
    @FXML
    private DatePicker datePicker;
    @FXML
    private RadioButton radioButtonGood;
    @FXML
    private RadioButton radioButtonBad;
    @FXML
    private Button buttonCreateUser;
    @FXML
    private ComboBox<String> comboTime;
    /* Filter */
    @FXML
    private TextField textSearchUserFilter;
    @FXML
    private DatePicker datePickerFilter;
    @FXML
    private ComboBox<String> comboTimeFilter;
    /* Table */
    @FXML
    private TableView<User> tableCitas;
    @FXML
    private TableColumn<User, String> columnUsername;
    @FXML
    private TableColumn<User, String> columnSuccess;
    @FXML
    private TableColumn<User, Integer> columnTotalBets;
    // Final Result
    @FXML
    private Label labelFinalResult;

    public void init(UserViewModel userViewModel) {
        logger.info("Initializing MainViewController");
        this.userViewModel = userViewModel;
        initEvents();
        initBindings();
    }

    private void initBindings() {
        logger.info("Initializing Bindings");
        ObservableList<String> horasDisponibles = FXCollections.observableArrayList(userViewModel.getAllSliceHours());
        comboTime.setItems(horasDisponibles);
    }

    private void initEvents() {
        logger.info("Initializing Events");
        buttonCreateUser.setOnAction(event -> saveUser());
    }

    private void saveUser() {
        String titleError = "Error al guardar";
        String infoError = "Error saving user";

        // Validate User
        String userName = (textFieldUser.getText() != null && !textFieldUser.getText().isEmpty()) ? textFieldUser.getText() : null;
        String date = (datePicker.getValue() != null && !datePicker.getValue().toString().isEmpty()) ? datePicker.getValue().toString() : null;
        String time = (comboTime.getValue() != null && !comboTime.getValue().isEmpty()) ? comboTime.getValue() : null;

        if (userName == null) {
            logger.info(infoError);
            Alert alert = new Alert(ERROR);
            alert.setTitle(titleError);
            alert.setHeaderText("Nombre de usuario vacío");
            alert.setContentText("El nombre de usuario NO puede estar vacío");
            alert.showAndWait();
            return;
        }

        if (date == null) {
            logger.info(infoError);
            Alert alert = new Alert(ERROR);
            alert.setTitle(titleError);
            alert.setHeaderText("Fecha vacía");
            alert.setContentText("La fecha NO puede estar vacía");
            alert.showAndWait();
            return;
        }

        if (time == null) {
            logger.info(infoError);
            Alert alert = new Alert(ERROR);
            alert.setTitle(titleError);
            alert.setHeaderText("Hora vacía");
            alert.setContentText("La hora NO puede estar vacía");
            alert.showAndWait();
            return;
        }

        if (!radioButtonGood.isSelected() && !radioButtonBad.isSelected()) {
            logger.info(infoError);
            Alert alert = new Alert(ERROR);
            alert.setTitle(titleError);
            alert.setHeaderText("Fiabilidad vacía");
            alert.setContentText("La fiabilidad NO puede estar vacía");
            alert.showAndWait();
            return;
        }

        // User is valid, continue saving the user
        logger.info("Saving user");
        userViewModel.saveUser(new User(textFieldUser.getText(), datePicker.getValue(), comboTime.getValue(), radioButtonGood.isSelected()));
    }
}
