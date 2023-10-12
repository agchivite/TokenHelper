package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.utils.TimeUtils;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;

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
    private TableView<UserDTO> tableUsers;
    @FXML
    private TableColumn<UserDTO, String> columnUsername;
    @FXML
    private TableColumn<UserDTO, String> columnSuccess;
    @FXML
    private TableColumn<UserDTO, String> columnTotalBets;

    // Global Result
    @FXML
    private Label textFinalResultDate;
    @FXML
    private Label textFinalResultTime;
    @FXML
    private Label textFinalResultPercentSuccess;
    @FXML
    private Label textFinalResultTotalBets;

    public void init(UserViewModel userViewModel) {
        logger.info("Initializing MainViewController");
        this.userViewModel = userViewModel;
        initEvents();
        initBindings();
        initDetails();
    }

    private void initDetails() {
        setColorsTable();
        centerTextTable();

        comboTimeFilter.getStylesheets().add(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css").toExternalForm());
        comboTime.getStylesheets().add(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css").toExternalForm());
    }

    private void initBindings() {
        logger.info("Initializing Bindings");

        // ComboTime
        comboTime.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTime.getSelectionModel().select(24);

        // ComboTimeFilter
        comboTimeFilter.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTimeFilter.getSelectionModel().select(24);

        // Tabla (nada más carga el programa)
        //tableUsers.setItems(FXCollections.observableArrayList(userViewModel.getUserStateProperty().getValue().getUsers()));

        // Para poder concatenar el % al final del número del porcentaje
        Callback<TableColumn.CellDataFeatures<UserDTO, String>, ObservableValue<String>> percentReliableCellFactory
                = new Callback<TableColumn.CellDataFeatures<UserDTO, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<UserDTO, String> p) {
                if (p.getValue() == null) {
                    return new SimpleStringProperty("");
                }
                return new SimpleStringProperty(p.getValue().getPercentReliable() + "%");
            }
        };

        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnSuccess.setCellValueFactory(percentReliableCellFactory);
        columnTotalBets.setCellValueFactory(new PropertyValueFactory<>("totalBets"));

        ToggleGroup toggleGroup = new ToggleGroup();
        radioButtonGood.setToggleGroup(toggleGroup);
        radioButtonBad.setToggleGroup(toggleGroup);

        // Observable del State, cada vez que cambie se actualiza la tabla
        //userViewModel.getUserStateProperty().addListener((observable, oldState, newState) -> updateTable(oldState, newState));
    }

    private void setColorsTable() {
        tableUsers.setRowFactory(tv -> new TableRow<UserDTO>() {
            @Override
            protected void updateItem(UserDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setStyle("");
                } else {
                    double success = Double.parseDouble(item.getPercentReliable());
                    if (success < 45) {
                        setStyle("-fx-background-color: #ff6161;");
                    } else if (success >= 45 && success <= 55) {
                        setStyle("-fx-background-color: orange;");
                    } else if (success > 55) {
                        setStyle("-fx-background-color: #53db78;");
                    } else {
                        setStyle(""); // Restablece cualquier estilo personalizado si el valor no cumple ningún criterio.
                    }
                }
            }
        });
    }

    private void centerTextTable() {
        columnSuccess.setCellFactory(tc -> {
            TableCell<UserDTO, String> cell = new TableCell<UserDTO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // Centrar el contenido de la celda
                    }
                }
            };
            return cell;
        });

        columnUsername.setCellFactory(tc -> {
            TableCell<UserDTO, String> cell = new TableCell<UserDTO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // Centrar el contenido de la celda
                    }
                }
            };
            return cell;
        });

        columnTotalBets.setCellFactory(tc -> {
            TableCell<UserDTO, String> cell = new TableCell<UserDTO, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                    } else {
                        setText(item);
                        setAlignment(Pos.CENTER); // Centrar el contenido de la celda
                    }
                }
            };
            return cell;
        });
    }

/*    private void updateTable(UserState oldState, UserState newState) {
        logger.debug("Updating user's table");

        if (!oldState.getUsers().equals(newState.getUsers())) {
            // Limpia la selección en la tabla
            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(newState.getUsers()));
        }
    }*/

    private void initEvents() {
        logger.info("Initializing Events");
        buttonCreateUser.setOnAction(event -> saveUser());

        textSearchUserFilter.setOnKeyReleased(event -> {
            onFilterDataTable();
        });

        comboTimeFilter.getSelectionModel().selectedItemProperty().addListener(event -> {
            onFilterDataTable();
        });

        datePickerFilter.valueProperty().addListener(event -> {
            onFilterDataTable();
        });
    }

    private void onFilterDataTable() {
        String newUsername = textSearchUserFilter.getText();
        String newTime = comboTimeFilter.getSelectionModel().getSelectedItem();
        LocalDate newDate = datePickerFilter.getValue();

        setGlobalResult(newTime, newDate);

        onFilterDataTableByTime(newUsername, newTime, newDate);
        onFilterDataTableByDateTime(newUsername, newTime, newDate);
        onFilterDataTableByUserTime(newUsername, newTime, newDate);
        onFilterDataTableByUserDateTime(newUsername, newTime, newDate);
    }

    private void setGlobalResult(String newTime, LocalDate newDate) {
        logger.info("Setting global result");
        if (newTime.equals("--:--")) {
            textFinalResultTime.setText("sin filtro");
        } else {
            textFinalResultTime.setText(newTime);
        }

        if (newDate == null) {
            textFinalResultDate.setText("sin filtro");
        } else {
            String dayOfWeek = String.valueOf(newDate.getDayOfWeek().getValue());
            logger.info("Day of week: " + dayOfWeek);
            if (dayOfWeek.equals("1")) {
                textFinalResultDate.setText("Lunes" + "-" + newDate);
            }
            if (dayOfWeek.equals("2")) {
                textFinalResultDate.setText("Martes" + "-" + newDate);
            }
            if (dayOfWeek.equals("3")) {
                textFinalResultDate.setText("Miércoles" + "-" + newDate);
            }
            if (dayOfWeek.equals("4")) {
                textFinalResultDate.setText("Jueves" + "-" + newDate);
            }
            if (dayOfWeek.equals("5")) {
                textFinalResultDate.setText("Viernes" + "-" + newDate);
            }
            if (dayOfWeek.equals("6")) {
                textFinalResultDate.setText("Sábado" + "-" + newDate);
            }
            if (dayOfWeek.equals("7")) {
                textFinalResultDate.setText("Domingo" + "-" + newDate);
            }
        }

        // Recoger todas las apuestas y hacer la media
        if (newDate == null) {
            //double percentSuccess = userViewModel.getGlobalPercentSuccessByTime(newTime);
        }
        
        textFinalResultPercentSuccess.setText("sin datos");
        textFinalResultTotalBets.setText("sin datos");
    }

    private void onFilterDataTableByTime(String newUsername, String newTime, LocalDate newDate) {
        logger.info("Filtering all by time");
        if (newUsername == null || newUsername.isEmpty() && newTime != null && !newTime.isEmpty() && newDate == null) {
            ArrayList<UserDTO> usersToShow = userViewModel.getAllByTime(newTime);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
        }
    }


    private void onFilterDataTableByDateTime(String newUsername, String newTime, LocalDate newDate) {
        logger.info("Filtering all by date time");
        if (newUsername == null || newUsername.isEmpty() && newTime != null && !newTime.isEmpty() && newDate != null) {
            ArrayList<UserDTO> usersToShow = userViewModel.getAllByDateTime(newTime, newDate);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
        }
    }

    private void onFilterDataTableByUserTime(String newUsername, String newTime, LocalDate newDate) {
        logger.info("Filtering by user & time");
        if (newUsername != null && !newUsername.isEmpty() && newTime != null && !newTime.isEmpty() && newDate == null) {
            ArrayList<UserDTO> usersToShow = userViewModel.getByUsernameTime(newUsername, newTime);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
        }
    }

    private void onFilterDataTableByUserDateTime(String newUsername, String newTime, LocalDate newDate) {
        logger.info("Filtering by user & date time");
        if (newUsername != null && !newUsername.isEmpty() && newTime != null && !newTime.isEmpty() && newDate != null) {
            ArrayList<UserDTO> usersToShow = userViewModel.getByUsernameDateTime(newUsername, newTime, newDate);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
        }
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

        // En el momento uno esté seleccionado la condición no se cumple
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

        userViewModel.saveUser(new UserEntity(textFieldUser.getText(), datePicker.getValue(), comboTime.getValue(), radioButtonGood.isSelected()));

        // Clean fields
        datePicker.setValue(null);
        comboTime.setValue(null);
        radioButtonGood.setSelected(false);
        radioButtonBad.setSelected(false);

        onFilterDataTable();
    }
}
