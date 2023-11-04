package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.routes.RoutesManager;
import dev.sbytmacke.tokenhelper.utils.DateFormatterUtils;
import dev.sbytmacke.tokenhelper.utils.TimeUtils;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

import static javafx.scene.control.Alert.AlertType.ERROR;

public class MainViewController {
    private static final int NUM_USERS_TO_SHOW_RANKING = 20;
    private static final double PERCENT_SUCCESS_RANKING_TO_SHOW = 51.0;
    private final String noDataTime = "--:--";
    Logger logger = LoggerFactory.getLogger(getClass());
    private UserViewModel userViewModel;
    private double medianValueTotalBets;
    private double medianValueTotalSuccess; // Para poner los colores de la tabla en el futuro!
    @FXML
    private Button buttonMainMiniView;
    // Menu
    @FXML
    private MenuItem menuDeleteData;
    @FXML
    private MenuItem menuLeyenda;
    @FXML
    private MenuItem menuUpdateData;
    @FXML
    private MenuItem menuBackup;

    /* Create user */
    @FXML
    private Button buttonCleanSaveUsername;
    @FXML
    private TextField textFieldUser;
    private ContextMenu contextMenu;
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
    private Button buttonClearFilters;
    @FXML
    private TextField textSearchUserFilter;
    @FXML
    private ComboBox<String> comboTimeFilter;
    @FXML
    private RadioButton radioButtonHideGreen;
    @FXML
    private RadioButton radioButtonHideOrange;
    @FXML
    private RadioButton radioButtonHideRed;
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
    /* Table General */
    @FXML
    private TableView<UserDTO> tableUsers;
    @FXML
    private TableColumn<UserDTO, String> columnUsername;
    @FXML
    private TableColumn<UserDTO, String> columnSuccess;
    @FXML
    private TableColumn<UserDTO, String> columnTotalBets;
    /* Table Ranking Global */
    @FXML
    private TableView<UserDTO> tableUsersRanking;
    @FXML
    private TableColumn<UserDTO, String> columnUsernameRanking;
    @FXML
    private TableColumn<UserDTO, String> columnSuccessRanking;
    @FXML
    private TableColumn<UserDTO, String> columnTotalBetsRanking;
    // Global Result
    @FXML
    private Label textFinalResultDate;
    @FXML
    private Label textFinalResultTime;
    @FXML
    private Label textFinalResultPercentSuccess;
    @FXML
    private Label textFinalResultTotalBets;

    private void calculateMedianTotalBets() {
        List<UserDTO> allUsers = userViewModel.getAll();
        Integer numUsers = allUsers.size();

        List<UserDTO> sortedAllUsers = allUsers.stream()
                .sorted(Comparator.comparing(UserDTO::getTotalBets))
                .collect(Collectors.toList());

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
        medianValueTotalBets = medianValue;
    }

    private void calculateMedianTotalSuccess() {
        List<UserDTO> allUsers = userViewModel.getAll();
        Integer numUsers = allUsers.size();

        List<UserDTO> sortedAllUsers = allUsers.stream()
                .sorted(Comparator.comparing(UserDTO::getTotalBets))
                .collect(Collectors.toList());

        // Calcula la mediana de los valores seleccionados
        double medianValue;
        if (numUsers % 2 == 0) {
            int middle = numUsers / 2;
            double value1 = sortedAllUsers.get(middle - 1).getPercentReliable();
            double value2 = sortedAllUsers.get(middle).getPercentReliable();
            medianValue = (value1 + value2) / 2.0;
        } else {
            medianValue = sortedAllUsers.get(numUsers / 2).getPercentReliable();
        }
        medianValueTotalSuccess = medianValue;
    }

    public TableView<UserDTO> getTableUsers() {
        return tableUsers;
    }

    public void init(UserViewModel userViewModel) {
        logger.info("Initializing MainViewController");
        this.userViewModel = userViewModel;
        calculateMedianTotalBets();
        calculateMedianTotalSuccess();
        initBindings();
        initDetails();
        initEvents();
    }

    private void initBindings() {
        logger.info("Initializing Bindings");

        // Table Ranking Global
        List<UserDTO> filterTopUsersReliable = filterRakingUsersReliable(userViewModel.getAll());
        tableUsersRanking.setItems(FXCollections.observableArrayList(filterTopUsersReliable));
        orderByTotalSuccessBets(tableUsersRanking);
        listUsers(tableUsersRanking.getItems());

        columnUsernameRanking.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnSuccessRanking.setCellValueFactory(new PropertyValueFactory<>("percentReliable"));
        columnTotalBetsRanking.setCellValueFactory(new PropertyValueFactory<>("totalBets"));

        // ComboTime
        comboTime.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTime.getSelectionModel().select(0);

        // ComboTimeFilter
        comboTimeFilter.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTimeFilter.getSelectionModel().select(0);

        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnSuccess.setCellValueFactory(new PropertyValueFactory<>("percentReliable"));
        columnTotalBets.setCellValueFactory(new PropertyValueFactory<>("totalBets"));

        ToggleGroup toggleGroup = new ToggleGroup();
        radioButtonGood.setToggleGroup(toggleGroup);
        radioButtonBad.setToggleGroup(toggleGroup);

        ToggleGroup toggleGroupDaysOfWeek = new ToggleGroup();
        radioButtonNone.setToggleGroup(toggleGroupDaysOfWeek);
        radioButtonMonday.setToggleGroup(toggleGroupDaysOfWeek);
        radioButtonTuesday.setToggleGroup(toggleGroupDaysOfWeek);
        radioButtonWednesday.setToggleGroup(toggleGroupDaysOfWeek);
        radioButtonThursday.setToggleGroup(toggleGroupDaysOfWeek);
        radioButtonFriday.setToggleGroup(toggleGroupDaysOfWeek);
        radioButtonSaturday.setToggleGroup(toggleGroupDaysOfWeek);
        radioButtonSunday.setToggleGroup(toggleGroupDaysOfWeek);
    }

    private void initDetails() {
        radioButtonNone.setSelected(true);
        tableUsers.setSelectionModel(null);
        tableUsersRanking.setSelectionModel(null);

        centerAndFontTextTable();
        setColorsTable();

        comboTimeFilter.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm());
        comboTime.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm());
    }

    private void initEvents() {
        logger.info("Initializing Events");

        menuBackup.setOnAction(event -> onBackupMenuAction());

        buttonCleanSaveUsername.setOnAction(event -> textFieldUser.setText(null));

        buttonClearFilters.setOnAction(event -> {
            textSearchUserFilter.setText("");
            comboTimeFilter.getSelectionModel().select(0);
            radioButtonHideGreen.setSelected(false);
            radioButtonHideOrange.setSelected(false);
            radioButtonHideRed.setSelected(false);
            radioButtonMonday.setSelected(false);
            radioButtonTuesday.setSelected(false);
            radioButtonWednesday.setSelected(false);
            radioButtonThursday.setSelected(false);
            radioButtonFriday.setSelected(false);
            radioButtonSaturday.setSelected(false);
            radioButtonSunday.setSelected(false);
            updateAllTables();
        });

        DateFormatterUtils dateFormatterUtils = new DateFormatterUtils();

        menuDeleteData.setOnAction(event -> onDataGestorMenuAction());
        menuLeyenda.setOnAction(event -> {
            try {
                onLeyendaMenuAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        menuUpdateData.setOnAction(event -> {
            try {
                onUpdateMenuAction();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        buttonCreateUser.setOnAction(event -> {
            saveUser();
        });

        // Filters
        textSearchUserFilter.setOnKeyReleased(event -> updateAllTables());
        comboTimeFilter.getSelectionModel().selectedItemProperty().addListener(event -> updateAllTables());

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

        radioButtonHideGreen.setOnAction(event -> updateAllTables());
        radioButtonHideOrange.setOnAction(event -> updateAllTables());
        radioButtonHideRed.setOnAction(event -> updateAllTables());

        radioButtonNone.setOnAction(event -> updateAllTables());
        radioButtonMonday.setOnAction(event -> updateAllTables());
        radioButtonTuesday.setOnAction(event -> updateAllTables());
        radioButtonWednesday.setOnAction(event -> updateAllTables());
        radioButtonThursday.setOnAction(event -> updateAllTables());
        radioButtonFriday.setOnAction(event -> updateAllTables());
        radioButtonSaturday.setOnAction(event -> updateAllTables());
        radioButtonSunday.setOnAction(event -> updateAllTables());

        contextMenu = new ContextMenu();
        textFieldUser.textProperty().addListener((observable, oldValue, newValue) -> {
            contextMenu.getItems().clear();

            if (newValue != null) {
                List<String> listSuggestions = filterSuggestionsList(newValue.trim());

                // Agrega sugerencias al ContextMenu basadas en el valor del TextField
                for (String suggestion : listSuggestions) {
                    MenuItem menuItem = new MenuItem(suggestion);
                    //menuItem.setStyle("-fx-text-fill: white;"); // Cambia el color del texto a blanco
                    contextMenu.getItems().add(menuItem);
                }
            }

            contextMenu.show(textFieldUser, Side.BOTTOM, 0, 0);

            if (newValue == null || newValue.isEmpty()) {
                contextMenu.getItems().clear();
            }
        });

        textSearchUserFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            contextMenu.getItems().clear();

            List<String> listSuggestions = filterSuggestionsList(newValue.trim());

            // Agrega sugerencias al ContextMenu basadas en el valor del TextField
            for (String suggestion : listSuggestions) {
                MenuItem menuItem = new MenuItem(suggestion);
                //menuItem.setStyle("-fx-text-fill: white;"); // Cambia el color del texto a blanco
                contextMenu.getItems().add(menuItem);
            }

            contextMenu.show(textSearchUserFilter, Side.BOTTOM, 0, 0);

            if (newValue == null || newValue.isEmpty()) {
                contextMenu.getItems().clear();
            }
        });

        contextMenu.setOnAction(event -> {
            MenuItem selectedItem = (MenuItem) event.getTarget();

            if (textFieldUser.isFocused()) {
                textFieldUser.setText(selectedItem.getText());
            } else if (textSearchUserFilter.isFocused()) {
                textSearchUserFilter.setText(selectedItem.getText());
                updateAllTables();
            }
            contextMenu.hide();
        });

        // Al final para que se actualice la tabla principal
        buttonMainMiniView.setOnAction(event -> {
            logger.info("Initializing MainMiniView");
            RoutesManager routesManager = new RoutesManager();
            try {
                routesManager.initMainMiniView(this);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void onBackupMenuAction() {
        logger.info("Initializing Backup View");

/*        if (userViewModel.backupData().isEmpty()) {
            Alert alert = new Alert(ERROR);
            alert.setTitle("Backup");
            alert.setHeaderText("Backup");
            alert.setContentText("Error al realizar el backup");
            alert.showAndWait();
            return;
        }

        try {
            userViewModel.backupData().forEach(document -> {
                logger.error("Backup: " + document.toJson());
            });
        } catch (Exception e) {
            logger.error("Error al realizar el backup");
            Alert alert = new Alert(ERROR);
            alert.setTitle("Backup");
            alert.setHeaderText("Backup");
            alert.setContentText("Error al realizar el backup");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Backup");
        alert.setHeaderText("Backup");
        alert.setContentText("Backup realizado correctamente");
        alert.showAndWait();*/
    }

    private void onUpdateMenuAction() throws IOException {
        logger.info("Initializing Update View");
        RoutesManager routesManager = new RoutesManager();
        routesManager.initUpdateView(this);
    }

    private List<UserDTO> listUsers(List<UserDTO> filterTopUsersReliable) {
        // Asigna al nombre de los usuarios su posición en el ranking
        for (int i = 0; i < filterTopUsersReliable.size(); i++) {
            UserDTO user = filterTopUsersReliable.get(i);
            if (i == 0) {
                user.setUsername("     🥇 " + user.getUsername());
            } else if (i == 1) {
                user.setUsername("     \uD83E\uDD48 " + user.getUsername());
            } else if (i == 2) {
                user.setUsername("     \uD83E\uDD49  " + user.getUsername());
            } else {
                user.setUsername("        " + (i + 1) + ".  " + user.getUsername());
            }
        }
        return filterTopUsersReliable;
    }

    private List<UserDTO> filterRakingUsersReliable(List<UserDTO> usersToFilter) {

        return usersToFilter.stream()
                .filter(user -> user.getPercentReliable() >= PERCENT_SUCCESS_RANKING_TO_SHOW) // Filtra usuarios fiables
                // Buscando los datos con más apuestas
                .filter(user -> user.getTotalBets() >= medianValueTotalBets)
                // Filtramos aquellos que fallan mucho, aquellos que fallan el 50% de las apuestas o menos
                .filter(user -> (double) user.getTotalBets() / 2 <= (double) user.getTotalSuccess())
                .limit(NUM_USERS_TO_SHOW_RANKING)
                .collect(Collectors.toList());
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

        tableUsersRanking.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(UserDTO item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    setStyle("");
                } else {
                    if (getIndex() == 0) {
                        setStyle("-fx-background-color: #EFB810;");
                    } else if (getIndex() == 1) {
                        setStyle("-fx-background-color: #c9c9c9;");
                    } else if (getIndex() == 2) {
                        setStyle("-fx-background-color: #b38f34;");
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

        tableUsersRanking.getColumns().get(0).setStyle("-fx-font-family: 'Noto Color Emoji'; -fx-font-size: 14px; ");
        tableUsersRanking.getColumns().get(1).setStyle("-fx-alignment: CENTER;");
        tableUsersRanking.getColumns().get(2).setStyle("-fx-alignment: CENTER;");
    }

    private List<String> filterSuggestionsList(String input) {
        List<String> allUsernames = userViewModel.getAllUsernamesNoRepeat();
        List<String> filteredSuggestions = new ArrayList<>();

        // Crear una expresión regular para coincidir con el inicio del nombre de usuario
        String regex = "^" + input.toLowerCase() + ".*";

        for (String suggestion : allUsernames) {
            if (suggestion.toLowerCase().matches(regex)) {
                filteredSuggestions.add(suggestion);
            }
        }

        //suggestionListView.getItems().setAll(filteredSuggestions);
        return filteredSuggestions;
    }

    public void updateAllTables() {
        calculateMedianTotalBets();
        calculateMedianTotalSuccess();

        String newUsername = textSearchUserFilter.getText().toUpperCase();
        String newTime = comboTimeFilter.getSelectionModel().getSelectedItem();
        Integer newDateOfWeek = getNewDateOfWeek();

        setGlobalResult(newTime, newDateOfWeek);

        Boolean onFilterByDate = onFilterDataTableByDate(newUsername, newTime, newDateOfWeek);
        Boolean onFilterByTime = onFilterDataTableByTime(newUsername, newTime, newDateOfWeek);
        Boolean onFilterByDateTime = onFilterDataTableByDateTime(newUsername, newTime, newDateOfWeek);
        Boolean onFilterByUserDate = onFilterDataTableByUserDate(newUsername, newTime, newDateOfWeek);
        Boolean onFilterByUserTime = onFilterDataTableByUserTime(newUsername, newTime, newDateOfWeek);
        Boolean onFilterByUserDateTime = onFilterDataTableByUserDateTime(newUsername, newTime, newDateOfWeek);
        orderByTotalSuccessBets(tableUsers);

        List<UserDTO> filteredUsers = filterRakingUsersReliable(tableUsers.getItems());

        for (int i = filteredUsers.size() - 1; i >= 0; i--) {
            tableUsers.getItems().remove(filteredUsers.get(i));
            filteredUsers.get(i).setUsername("⭐ " + filteredUsers.get(i).getUsername());
            tableUsers.getItems().add(i, filteredUsers.get(i));
        }

        if (!onFilterByDate && !onFilterByTime && !onFilterByDateTime && !onFilterByUserDate && !onFilterByUserTime && !onFilterByUserDateTime) {
            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
        }

        // Actualizamos el ranking general (en caso de modificar datos)
        List<UserDTO> filterTopUsersReliable = filterRakingUsersReliable(userViewModel.getAll());
        tableUsersRanking.setItems(FXCollections.observableArrayList(filterTopUsersReliable));
        orderByTotalSuccessBets(tableUsersRanking);
        listUsers(tableUsersRanking.getItems());
    }

    private List<UserDTO> filterStarUsersReliable(ObservableList<UserDTO> items) {
        // TODO: Cuando se decida exactamente que queremos en un tio STAR
        return items;
    }


    private void orderByTotalSuccessBets(TableView<UserDTO> tableToSort) {
        // Ordenamos por el que tenga más aciertos de cómputo, ya que hemos eliminado aquellos que tienen muchos fallos previamente
        Comparator<UserDTO> customComparatorRanking = (user1, user2) -> {
            // Total de aciertos
            double totalSuccessUser1 = user1.getTotalBets() * user1.getPercentReliable() / 100;
            double totalSuccessUser2 = user2.getTotalBets() * user2.getPercentReliable() / 100;

            if (totalSuccessUser1 == totalSuccessUser2) {
                // Si el total de aciertos son iguales, ordénalos por percentSuccess
                return Double.compare(user2.getPercentReliable(), user1.getPercentReliable());
            } else {
                // Si los totalBets no son iguales, ordénalos por total de aciertos
                return Double.compare(totalSuccessUser2, totalSuccessUser1);
            }
        };

        tableToSort.getItems().sort(customComparatorRanking); // Aplicamos el comparador personalizado
    }

    public Integer getNewDateOfWeek() {
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

    private void setGlobalResult(String newTime, Integer newDateOfWeek) {
        logger.info("Setting global result");

        setGlobalTime(newTime, noDataTime);
        setGlobalDate(newDateOfWeek);

        // Recoger todas las apuestas y hacer la media con hora y día de la semana
        if (!newTime.equals(noDataTime) && newDateOfWeek != null) {
            Integer totalBets = userViewModel.getGlobalTotalBetsByDateTime(newTime, newDateOfWeek);
            double percentSuccess = userViewModel.getGlobalPercentSuccessByDateTime(newTime, newDateOfWeek);
            setDetailsGlobalResult(totalBets, percentSuccess);
        } else if (!newTime.equals(noDataTime)) {
            // Si no solo con la hora
            Integer totalBets = userViewModel.getGlobalTotalBetsByTime(newTime);
            double percentSuccess = userViewModel.getGlobalPercentSuccessByTime(newTime);
            setDetailsGlobalResult(totalBets, percentSuccess);
        } else if (newDateOfWeek != null) {
            // Solo con la fecha (todos los datos)
            Integer totalBets = userViewModel.getGlobalTotalBetsByDate(newDateOfWeek);
            double percentSuccess = userViewModel.getGlobalPercentSuccessByDate(newDateOfWeek);
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

    private void setGlobalDate(Integer newDateOfWeek) {
        if (newDateOfWeek == null) {
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

            if (newDateOfWeek == 1) {
                textFinalResultDate.setText("Lunes");
            }
            if (newDateOfWeek == 2) {
                textFinalResultDate.setText("Martes");
            }
            if (newDateOfWeek == 3) {
                textFinalResultDate.setText("Miércoles");
            }
            if (newDateOfWeek == 4) {
                textFinalResultDate.setText("Jueves");
            }
            if (newDateOfWeek == 5) {
                textFinalResultDate.setText("Viernes");
            }
            if (newDateOfWeek == 6) {
                textFinalResultDate.setText("Sábado");
            }
            if (newDateOfWeek == 7) {
                textFinalResultDate.setText("Domingo");
            }
        }
        textFinalResultDate.setTextFill(Color.WHITE);
    }

    private void onLeyendaMenuAction() throws IOException {
        logger.info("Initializing leyenda view");
        RoutesManager routesManager = new RoutesManager();
        routesManager.initLeyendaView();
    }

    private void onDataGestorMenuAction() {
        logger.info("Initializing data gestor view");
        RoutesManager routesManager = new RoutesManager();
        try {
            routesManager.intiDataGestorView(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    private Boolean onFilterDataTableByDate(String newUsername, String newTime, Integer newDateOfWeek) {
        if (newUsername == null || newUsername.isEmpty() && newTime.equals(noDataTime) && newDateOfWeek != null) {
            logger.info("Filtering all by date");
            List<UserDTO> usersToShow = userViewModel.getAllByDate(newDateOfWeek);

            extractedUserByRadioButtonFilter(usersToShow);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));

            return true;
        }
        return false;
    }

    private Boolean onFilterDataTableByTime(String newUsername, String newTime, Integer newDate) {
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

    private Boolean onFilterDataTableByDateTime(String newUsername, String newTime, Integer newDate) {
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
            if (user.getUsername().toUpperCase().matches(regex) || user.getUsername().equalsIgnoreCase(newUsername)) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }

    private Boolean onFilterDataTableByUserDate(String newUsername, String newTime, Integer newDate) {
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

    private Boolean onFilterDataTableByUserTime(String newUsername, String newTime, Integer newDate) {
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

    private Boolean onFilterDataTableByUserDateTime(String newUsername, String newTime, Integer newDate) {
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

        if (time == null || time.equals(noDataTime)) {
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

        userViewModel.saveUser(new UserEntity(textFieldUser.getText().trim().toUpperCase(), datePicker.getValue(), comboTime.getValue(), radioButtonGood.isSelected()));

        // Clean fields
        datePicker.setValue(null);
        comboTime.setValue(null);
        radioButtonGood.setSelected(false);
        radioButtonBad.setSelected(false);

        updateAllTables();
    }

    public UserViewModel getUserViewModel() {
        return userViewModel;
    }

    public Integer getIndexComboTimeFilter() {
        return comboTimeFilter.getSelectionModel().getSelectedIndex();
    }


}
