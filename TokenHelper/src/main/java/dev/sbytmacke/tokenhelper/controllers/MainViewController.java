package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.routes.RoutesManager;
import dev.sbytmacke.tokenhelper.utils.DateFormatterUtils;
import dev.sbytmacke.tokenhelper.utils.TimeUtils;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.collections.FXCollections;
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
    private final String noDataTime = "--:--";
    Logger logger = LoggerFactory.getLogger(getClass());
    // Almacenar la fecha actual y la fecha anterior
    LocalDate savedDate = null;
    private UserViewModel userViewModel;

    @FXML
    private Button buttonMainMiniView;
    // Menu
    @FXML
    private MenuItem menuDeleteData;
    @FXML
    private MenuItem menuLeyenda;
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

    public TableView<UserDTO> getTableUsers() {
        return tableUsers;
    }

    public void init(UserViewModel userViewModel) {
        logger.info("Initializing MainViewController");
        this.userViewModel = userViewModel;
        initBindings();
        initDetails();
        initEvents();
    }

    private void initEvents() {
        logger.info("Initializing Events");

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
        menuLeyenda.setOnAction(event -> onLeyendaMenuAction());

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

            List<String> listSuggestions = filterSuggestionsList(newValue.trim());

            // Agrega sugerencias al ContextMenu basadas en el valor del TextField
            for (String suggestion : listSuggestions) {
                MenuItem menuItem = new MenuItem(suggestion);
                //menuItem.setStyle("-fx-text-fill: white;"); // Cambia el color del texto a blanco
                contextMenu.getItems().add(menuItem);
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
            routesManager.initMainMiniView(this);
        });
    }

    private void initBindings() {
        logger.info("Initializing Bindings");

        // Table Ranking Global
        tableUsersRanking.setItems(FXCollections.observableArrayList(filterTopUsersReliable(5)));

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

    private List<UserDTO> filterTopUsersReliable(int numberUsersToShow) {
        List<UserDTO> usersToFilter = userViewModel.getAll();

        return usersToFilter.stream()
                .filter(user -> user.getPercentReliable() > 49.00) // Filtra usuarios fiables
                .sorted(Comparator.comparing(UserDTO::getTotalBets).reversed()) // Ordena por totalBets en orden descendente, buscando los datos más reales
                .limit(numberUsersToShow)
                //.sorted(Comparator.comparing(UserDTO::getPercentReliable).reversed()) // Ordena por percentReliable en orden descendente, en caso de quererlo
                .collect(Collectors.toList());
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

        for (int i = 0; i < tableUsersRanking.getColumns().size(); i++) {
            tableUsersRanking.getColumns().get(i).setStyle("-fx-alignment: CENTER;");
        }
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

        // Ordena la tabla por 'percentSucces'
        tableUsers.getSortOrder().setAll(columnSuccess);

        // Y para aquellos que tengan el mismo percentSuccess, ordena por 'totalBets'
        Comparator<UserDTO> customComparator = (user1, user2) -> {
            if (user1.getPercentReliable() != user2.getPercentReliable()) {
                // Si los percentSuccess son diferentes, ordénalos por percentSuccess
                return Double.compare(user2.getPercentReliable(), user1.getPercentReliable());
            } else {
                // Si los percentSuccess son iguales, ordénalos por totalBets
                return Integer.compare(user2.getTotalBets(), user1.getTotalBets());
            }
        };
        tableUsers.getItems().sort(customComparator); // Aplicamos el comparador personalizado

        if (!onFilterByDate && !onFilterByTime && !onFilterByDateTime && !onFilterByUserDate && !onFilterByUserTime && !onFilterByUserDateTime) {
            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
        }

        // Actualizamos el ranking general
        tableUsersRanking.setItems(FXCollections.observableArrayList(filterTopUsersReliable(5)));
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

    private void onLeyendaMenuAction() {
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

            setTopicUsers(usersToShow);
            extractedUserByRadioButtonFilter(usersToShow);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));

            return true;
        }
        return false;
    }

    private void setTopicUsers(List<UserDTO> usersToShow) {
        List<UserDTO> usersFiltered = usersToShow.stream()
                .filter(user -> user.getPercentReliable() > 65.00)
                .sorted(Comparator.comparing(UserDTO::getTotalBets).reversed()) // Ordena por totalBets en orden descendente, buscando los datos más
                //.sorted(Comparator.comparing(UserDTO::getPercentReliable).reversed()) // Ordena por percentReliable en orden descendente, en caso de quererlo
                .limit(1)
                .collect(Collectors.toList());

        if (usersFiltered.isEmpty()) {
            //Filtramos por naranjas
            usersFiltered = usersToShow.stream()
                    .filter(user -> user.getPercentReliable() > 49.00)
                    .sorted(Comparator.comparing(UserDTO::getTotalBets).reversed()) // Ordena por totalBets en orden descendente, buscando los datos más
                    //.sorted(Comparator.comparing(UserDTO::getPercentReliable).reversed()) // Ordena por percentReliable en orden descendente, en caso de quererlo
                    .limit(1)
                    .collect(Collectors.toList());
        }

        if (!usersFiltered.isEmpty()) {
            for (UserDTO user : usersFiltered) {
                // Lo ponemos el primero de la lista
                usersToShow.remove(user);
                user.setUsername(user.getUsername() + " ⭐");
                usersToShow.add(0, user);
            }
        }
    }

    private Boolean onFilterDataTableByTime(String newUsername, String newTime, Integer newDate) {
        if (newUsername == null || newUsername.isEmpty() && !newTime.equals(noDataTime) && newDate == null) {
            logger.info("Filtering all by time");
            List<UserDTO> usersToShow = userViewModel.getAllByTime(newTime);

            setTopicUsers(usersToShow);
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

            setTopicUsers(usersToShow);
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

            setTopicUsers(usersToShow);
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

            setTopicUsers(usersToShow);
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

            setTopicUsers(usersToShow);
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
}
