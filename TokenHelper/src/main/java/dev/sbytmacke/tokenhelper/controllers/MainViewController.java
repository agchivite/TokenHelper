package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.utils.DateFormatterUtils;
import dev.sbytmacke.tokenhelper.utils.TimeUtils;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class MainViewController {
    private final String noDataTime = "--:--";
    Logger logger = LoggerFactory.getLogger(getClass());
    private UserViewModel userViewModel;
    /* Create user */
    @FXML
    private TextField textFieldUser;
    @FXML
    private ListView<String> suggestionListView;
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
    @FXML
    private RadioButton radioButtonHideGreen;
    @FXML
    private RadioButton radioButtonHideOrange;
    @FXML
    private RadioButton radioButtonHideRed;
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

    private void initEvents() {
        logger.info("Initializing Events");

        buttonCreateUser.setOnAction(event -> saveUser());

        // Filters
        textSearchUserFilter.setOnKeyReleased(event -> onFilterDataTable());
        comboTimeFilter.getSelectionModel().selectedItemProperty().addListener(event -> onFilterDataTable());

        DateFormatterUtils dateFormatterUtils = new DateFormatterUtils();
        DateTimeFormatter dateFormatterDatePickerFilter = dateFormatterUtils.formatDate(datePickerFilter);
        datePickerFilter.valueProperty().addListener(event -> onFilterDataTable());
        datePickerFilter.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                LocalDate parsedDate = LocalDate.parse(newValue, dateFormatterDatePickerFilter);
                datePickerFilter.setValue(parsedDate); // Actualiza el DatePicker
                onFilterDataTable(); // Llama a la función después de actualizar el DatePicker
            } catch (DateTimeParseException e) {
                logger.error("No Valid - DatePickerFilter: " + newValue);
                if (newValue.isEmpty()) {
                    datePickerFilter.setValue(null); // Establece newDate como nulo si el valor está vacío
                    onFilterDataTable(); // Llama a la función con newDate nulo
                } else {
                    tableUsers.setSelectionModel(null); // Borramos toda la tabla en caso de fallar
                    tableUsers.getItems().clear();
                }
            }
        });

        // Cambiamos también en el DatePicker al añadir
        DateTimeFormatter dateFormatterDatePicker = dateFormatterUtils.formatDate(datePicker);
        datePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                LocalDate parsedDate = LocalDate.parse(newValue, dateFormatterDatePicker);
                datePicker.setValue(parsedDate); // Actualiza el DatePicker
            } catch (DateTimeParseException e) {
                logger.error("No Valid - DatePickerFilter: " + newValue);
            }
        });

        radioButtonHideGreen.setOnAction(event -> onFilterDataTable());
        radioButtonHideOrange.setOnAction(event -> onFilterDataTable());
        radioButtonHideRed.setOnAction(event -> onFilterDataTable());

        // Autocomplete suggestions
        suggestionListView.setVisible(false);
        textFieldUser.textProperty().addListener((observable, oldValue, newValue) -> {
            updateSuggestionsListView(newValue);
            suggestionListView.setVisible(!newValue.isEmpty());
        });

        suggestionListView.setOnMouseClicked(event -> {
            String selectedSuggestion = suggestionListView.getSelectionModel().getSelectedItem();
            textFieldUser.setText(selectedSuggestion);
            suggestionListView.setVisible(false);
        });
    }

    private void initBindings() {
        logger.info("Initializing Bindings");

        // ComboTime
        comboTime.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTime.getSelectionModel().select(24);

        // ComboTimeFilter
        comboTimeFilter.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTimeFilter.getSelectionModel().select(24);

        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnSuccess.setCellValueFactory(new PropertyValueFactory<>("percentReliable"));
        columnTotalBets.setCellValueFactory(new PropertyValueFactory<>("totalBets"));

        ToggleGroup toggleGroup = new ToggleGroup();
        radioButtonGood.setToggleGroup(toggleGroup);
        radioButtonBad.setToggleGroup(toggleGroup);
    }

    private void initDetails() {
        setColorsTable();
        centerTextTable();

        comboTimeFilter.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm());
        comboTime.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm());
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
                        setStyle("-fx-background-color: #ffffff");
                    }

                    // Ahora puedes usar formattedPercentReliable donde necesites mostrar el valor con dos decimales.
                }
            }
        });
    }

    private void centerTextTable() {
        for (int i = 0; i < tableUsers.getColumns().size(); i++) {
            tableUsers.getColumns().get(i).setStyle("-fx-alignment: CENTER;");
        }
    }

    private void updateSuggestionsListView(String input) {
        List<String> allUsernames = userViewModel.getAllUsernamesNoRepeat();
        List<String> filteredSuggestions = new ArrayList<>();

        // Crear una expresión regular para coincidir con el inicio del nombre de usuario
        String regex = "^" + input.toLowerCase() + ".*";

        for (String suggestion : allUsernames) {
            if (suggestion.toLowerCase().matches(regex)) {
                filteredSuggestions.add(suggestion);
            }
        }

        suggestionListView.getItems().setAll(filteredSuggestions);
    }

    private void onFilterDataTable() {
        String newUsername = textSearchUserFilter.getText().toUpperCase();
        String newTime = comboTimeFilter.getSelectionModel().getSelectedItem();
        LocalDate newDate = datePickerFilter.getValue();

        setGlobalResult(newTime, newDate);

        Boolean onFilterByDate = onFilterDataTableByDate(newUsername, newTime, newDate);
        Boolean onFilterByTime = onFilterDataTableByTime(newUsername, newTime, newDate);
        Boolean onFilterByDateTime = onFilterDataTableByDateTime(newUsername, newTime, newDate);
        Boolean onFilterByUserDate = onFilterDataTableByUserDate(newUsername, newTime, newDate);
        Boolean onFilterByUserTime = onFilterDataTableByUserTime(newUsername, newTime, newDate);
        Boolean onFilterByUserDateTime = onFilterDataTableByUserDateTime(newUsername, newTime, newDate);

        // Ordena la tabla por 'percentSucces'
        tableUsers.getSortOrder().setAll(columnSuccess);

        if (!onFilterByDate && !onFilterByTime && !onFilterByDateTime && !onFilterByUserDate && !onFilterByUserTime && !onFilterByUserDateTime) {
            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
        }
    }


    private void setGlobalResult(String newTime, LocalDate newDate) {
        logger.info("Setting global result");

        setGlobalTime(newTime, noDataTime);
        setGlobalDate(newDate);

        // Recoger todas las apuestas y hacer la media con hora y día de la semana
        if (!newTime.equals(noDataTime) && newDate != null) {
            Integer totalBets = userViewModel.getGlobalTotalBetsByDateTime(newTime, newDate);
            double percentSuccess = userViewModel.getGlobalPercentSuccessByDateTime(newTime, newDate);
            setDetailsGlobalResult(totalBets, percentSuccess);
        } else if (!newTime.equals(noDataTime)) {
            // Si no solo con la hora
            Integer totalBets = userViewModel.getGlobalTotalBetsByTime(newTime);
            double percentSuccess = userViewModel.getGlobalPercentSuccessByTime(newTime);
            setDetailsGlobalResult(totalBets, percentSuccess);
        } else if (newDate != null) {
            // Solo con la fecha (todos los datos)
            Integer totalBets = userViewModel.getGlobalTotalBetsByDate(newDate);
            double percentSuccess = userViewModel.getGlobalPercentSuccessByDate(newDate);
            setDetailsGlobalResult(totalBets, percentSuccess);
        } else {
            // Si no hay ningún filtro
            setDetailsGlobalResult(0, 0.0);
        }
    }

    private void setDetailsGlobalResult(Integer totalBets, double percentSuccess) {
        if (percentSuccess == 0.0) {
            textFinalResultPercentSuccess.setText("sin datos");
            textFinalResultPercentSuccess.setTextFill(Color.WHITE);
        }

        if (totalBets == 0) {
            textFinalResultTotalBets.setText("sin datos");
            textFinalResultTotalBets.setTextFill(Color.WHITE);
            return;
        }
        textFinalResultPercentSuccess.setText(percentSuccess + "%");
        if (percentSuccess <= 49.00) {
            textFinalResultPercentSuccess.setTextFill(Color.RED);
        } else if (percentSuccess > 49.00 && percentSuccess <= 65.00) {
            textFinalResultPercentSuccess.setTextFill(Color.ORANGE);
        } else if (percentSuccess > 65.00) {
            textFinalResultPercentSuccess.setTextFill(Color.GREEN);
        } else {
            textFinalResultPercentSuccess.setTextFill(Color.WHITE);
        }
        textFinalResultTotalBets.setText(String.valueOf(totalBets));
    }

    private void setGlobalDate(LocalDate newDate) {
        if (newDate == null) {
            textFinalResultDate.setText("sin filtro");
        } else {
            Map<Integer, String> dayOfWeekMap = new HashMap<>();
            dayOfWeekMap.put(1, "Lunes");
            dayOfWeekMap.put(2, "Martes");
            dayOfWeekMap.put(3, "Miércoles");
            dayOfWeekMap.put(4, "Jueves");
            dayOfWeekMap.put(5, "Viernes");
            dayOfWeekMap.put(6, "Sábado");
            dayOfWeekMap.put(7, "Domingo");

            int dayOfWeekValue = newDate.getDayOfWeek().getValue();
            String dayOfWeek = dayOfWeekMap.get(dayOfWeekValue);
            textFinalResultDate.setText(dayOfWeek);
        }
        textFinalResultDate.setTextFill(Color.WHITE);
    }

    private void setGlobalTime(String newTime, String noDataTime) {
        if (newTime.equals(noDataTime)) {
            textFinalResultTime.setText("sin filtro");
        } else {
            textFinalResultTime.setText(newTime);
        }
        textFinalResultTime.setTextFill(Color.WHITE);
    }

    private void extractedUserByRadioButtonFilter(List<UserDTO> usersToShow) {
        if (radioButtonHideGreen.isSelected()) {
            usersToShow.removeIf(user -> user.getPercentReliable() > 65.00);
        }

        if (radioButtonHideOrange.isSelected()) {
            usersToShow.removeIf(user -> user.getPercentReliable() > 49.00 && user.getPercentReliable() <= 65.00);
        }

        if (radioButtonHideRed.isSelected()) {
            usersToShow.removeIf(user -> user.getPercentReliable() <= 49.00);
        }
    }

    private Boolean onFilterDataTableByDate(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername == null || newUsername.isEmpty() && newTime.equals(noDataTime) && newDate != null) {
            logger.info("Filtering all by date");
            List<UserDTO> usersToShow = userViewModel.getAllByDate(newDate);

            extractedUserByRadioButtonFilter(usersToShow);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));

            return true;
        }
        return false;
    }

    private Boolean onFilterDataTableByTime(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername == null || newUsername.isEmpty() && !newTime.equals(noDataTime) && newDate == null) {
            logger.info("Filtering all by time");
            List<UserDTO> usersToShow = userViewModel.getAllByTime(newTime);

            extractedUserByRadioButtonFilter(usersToShow);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
            return true;
        }
        return false;
    }

    private Boolean onFilterDataTableByDateTime(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername == null || newUsername.isEmpty() && !newTime.equals(noDataTime) && newDate != null) {
            logger.info("Filtering all by date time");

            List<UserDTO> usersToShow = userViewModel.getAllByDateTime(newTime, newDate);

            extractedUserByRadioButtonFilter(usersToShow);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
            return true;
        }
        return false;
    }

    private List<UserDTO> filterUsersByPartialUsername(List<UserDTO> users, String newUsername) {
        List<UserDTO> filteredUsers = new ArrayList<>();

        String regex = "^" + newUsername + ".*";

        for (UserDTO user : users) {
            if (user.getUsername().toUpperCase().matches(regex)) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    private Boolean onFilterDataTableByUserDate(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername != null && !newUsername.isEmpty() && newTime.equals(noDataTime) && newDate != null) {
            logger.info("Filtering all by user & date");
            List<UserDTO> usersToShow = userViewModel.getAllByDate(newDate);

            extractedUserByRadioButtonFilter(usersToShow);

            // Filtrar la lista por los primeros caracteres del nombre de usuario
            usersToShow = filterUsersByPartialUsername(usersToShow, newUsername);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));

            return true;

        }
        return false;
    }

    private Boolean onFilterDataTableByUserTime(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername != null && !newUsername.isEmpty() && !newTime.equals(noDataTime) && newDate == null) {
            logger.info("Filtering by user & time");

            List<UserDTO> usersToShow = userViewModel.getAllByTime(newTime);

            extractedUserByRadioButtonFilter(usersToShow);

            // Filtrar la lista por los primeros caracteres del nombre de usuario
            usersToShow = filterUsersByPartialUsername(usersToShow, newUsername);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
            return true;
        }
        return false;
    }

    private Boolean onFilterDataTableByUserDateTime(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername != null && !newUsername.isEmpty() && !newTime.equals(noDataTime) && newDate != null) {
            logger.info("Filtering by user & date time");

            List<UserDTO> usersToShow = userViewModel.getAllByDateTime(newTime, newDate);

            extractedUserByRadioButtonFilter(usersToShow);

            // Filtrar la lista por los primeros caracteres del nombre de usuario
            usersToShow = filterUsersByPartialUsername(usersToShow, newUsername);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
            return true;
        }
        return false;
    }

    private void saveUser() {
        String titleError = "Error al guardar";
        String infoError = "Error saving user";

        // Validate User
        String userName = (textFieldUser.getText() != null && !textFieldUser.getText().isEmpty()) ? textFieldUser.getText() : null;
        LocalDate date = (datePicker.getValue() != null && !datePicker.getValue().toString().isEmpty()) ? datePicker.getValue() : null;
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
            alert.setHeaderText("Fecha incorrecta");
            alert.setContentText("La fecha es incorrecta, debe tener formato (yyyy-MM-dd)");
            alert.showAndWait();
            return;
        } else if (date.isAfter(LocalDate.now())) {
            logger.info(infoError);
            Alert alert = new Alert(ERROR);
            alert.setTitle(titleError);
            alert.setHeaderText("Fecha incorrecta");
            alert.setContentText("La fecha no puede ser más tarde del día presente");
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

        userViewModel.saveUser(new UserEntity(textFieldUser.getText().toUpperCase(), datePicker.getValue(), comboTime.getValue(), radioButtonGood.isSelected()));

        // Clean fields
        datePicker.setValue(null);
        comboTime.setValue(null);
        radioButtonGood.setSelected(false);
        radioButtonBad.setSelected(false);

        onFilterDataTable();
    }
}
