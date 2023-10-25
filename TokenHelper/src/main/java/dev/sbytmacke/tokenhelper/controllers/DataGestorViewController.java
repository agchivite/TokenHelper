package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.models.UserEntity;
import dev.sbytmacke.tokenhelper.utils.DateFormatterUtils;
import dev.sbytmacke.tokenhelper.utils.TimeUtils;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

public class DataGestorViewController {
    private final String noDataTime = "--:--";
    Logger logger = LoggerFactory.getLogger(getClass());
    private UserViewModel userViewModel;
    private ContextMenu contextMenu;

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

    public void init(UserViewModel userViewModel) {
        logger.info("Initializing MainViewController");
        this.userViewModel = userViewModel;
        initEvents();
        initBindings();
        initDetails();
    }

    private void initEvents() {
        // Filters
        textSearchUserFilter.setOnKeyReleased(event -> onFilterDataTable());
        comboTimeFilter.getSelectionModel().selectedItemProperty().addListener(event -> onFilterDataTable());

        DateFormatterUtils dateFormatterUtils = new DateFormatterUtils();
        DateTimeFormatter dateFormatterDatePickerFilter = dateFormatterUtils.formatDate(datePickerFilter);
        datePickerFilter.valueProperty().addListener(event -> onFilterDataTable());
        datePickerFilter.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                // Comprobamos si el valor ya está asignado
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

        contextMenu = new ContextMenu();
        textSearchUserFilter.textProperty().addListener((observable, oldValue, newValue) -> {
            contextMenu.getItems().clear();

            List<String> listSuggestions = filterSuggestionsList(newValue);

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

            if (textSearchUserFilter.isFocused()) {
                textSearchUserFilter.setText(selectedItem.getText());
            }

            onFilterDataTable();
            contextMenu.hide();
        });
    }

    private List<String> filterSuggestionsList(String input) {
        List<String> allUsernames = userViewModel.getAllUsernamesNoRepeat();
        List<String> filteredSuggestions = new ArrayList<>();

        if (input != null) {
            // Crear una expresión regular para coincidir con el inicio del nombre de usuario
            String regex = "^" + input.toLowerCase() + ".*";

            for (String suggestion : allUsernames) {
                if (suggestion.toLowerCase().matches(regex)) {
                    filteredSuggestions.add(suggestion);
                }
            }
        }

        return filteredSuggestions;
    }

    private void initDetails() {
        setColorsTable();
        addLastColumnDeleteButton();
        centerAndFontTextTable();

        orderTableByDateTime();
        tableUsers.setSelectionModel(null);

        comboTimeFilter.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm());

        // Disable visual scroll bar
        tableUsers.getStylesheets().add(getClass().getResource("/dev/sbytmacke/tokenhelper/css/tableUsersDataGestor.css").toExternalForm());

        // Disable Scroll
        //tableUsers.addEventFilter(ScrollEvent.ANY, Event::consume);

        // Disable horizontal scrolling
        tableUsers.addEventFilter(ScrollEvent.ANY, event -> {
            if (event.getDeltaX() != 0) {
                event.consume();
            }
        });
    }

    private void orderTableByDateTime() {
        // Ordena la tabla por 'la fecha'
        tableUsers.getSortOrder().setAll(columnDate);

        // Y para aquellos que tengan la misma fecha, ordena por 'hora'
        Comparator<UserEntity> customComparator = (user1, user2) -> {
            if (user1.getDateBet().isEqual(user2.getDateBet())) {
                // Si las fechas son iguales, ordénalos por hora
                return user1.getTimeBet().compareTo(user2.getTimeBet());
            } else {
                // Si las fechas son diferentes, mantén el orden actual
                return 0;
            }
        };

        tableUsers.getItems().sort(customComparator); // Aplicamos el comparador personalizado
    }

    private void initBindings() {
        logger.info("Initializing Bindings");

        // ComboTimeFilter
        comboTimeFilter.setItems(FXCollections.observableArrayList(TimeUtils.getAllSliceHours()));
        comboTimeFilter.getSelectionModel().select(25);

        // Table, mediante el State hacerlo
        tableUsers.setItems(FXCollections.observableArrayList(userViewModel.getAllEntity()));

        columnUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        columnDate.setCellValueFactory(new PropertyValueFactory<>("dateBet"));
        columnTime.setCellValueFactory(new PropertyValueFactory<>("timeBet"));
        columnReliable.setCellValueFactory(new PropertyValueFactory<>("reliable"));

        radioButtonReliableFilter.setOnAction(event -> onFilterDataTable());
        radioButtonNoReliableFilter.setOnAction(event -> onFilterDataTable());
    }

    private void setColorsTable() {
        tableUsers.setRowFactory(tv -> new TableRow<>() {
            @Override
            public void updateItem(UserEntity item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null) {
                    setStyle("");
                } else {
                    if (!item.getReliable()) {
                        setStyle("-fx-background-color: #ff6161; -fx-margin: 15px");
                    } else if (item.getReliable()) {
                        setStyle("-fx-background-color: #53db78; -fx-margin: 15px");
                    } else {
                        setStyle("-fx-background-color: #ffffff; -fx-margin: 15px");
                    }
                }
            }
        });
    }

    private void centerAndFontTextTable() {
        for (int i = 0; i < tableUsers.getColumns().size(); i++) {
            tableUsers.getColumns().get(i).setStyle("-fx-alignment: CENTER; -fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 14px;");
            // Última columna, quitamos el color
            if (i == tableUsers.getColumns().size() - 1) {
                tableUsers.getColumns().get(i).setStyle("-fx-background-color: #313338; -fx-alignment: CENTER; -fx-font-family: 'Segoe UI Emoji'; -fx-font-size: 14px;");
            }
        }
        tableUsers.setStyle("-fx-background-color: #313338;");
    }

    private void addLastColumnDeleteButton() {
        // Crear la columna de botones
        TableColumn<UserEntity, Void> columnDelete = new TableColumn<>("Borrado");
        columnDelete.setStyle("-fx-color: red");
        columnDelete.setCellFactory(param -> new TableCell<>() {
            private final HBox buttonContainer = new HBox();
            private final Button deleteButton = new Button("❌");

            {
                // Configurar el manejo del evento del botón
                deleteButton.setOnAction(event -> {
                    UserEntity user = getTableView().getItems().get(getIndex());

                    // Crear una imagen y agregarla al diálogo
                    Image image = new Image(getClass().getResource("/dev/sbytmacke/tokenhelper/icons/borrar.png").toExternalForm()); // Reemplaza con la ruta de tu imagen
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(100); // Ajusta el alto de la imagen según tus necesidades
                    imageView.setFitWidth(100); // Ajusta el ancho de la imagen según tus necesidades

                    Dialog dialog = new Dialog();
                    dialog.setGraphic(imageView);
                    dialog.setTitle("Eliminar datos");
                    dialog.setContentText("¿Deseas eliminar el dato?" +
                            "\nNO SE PODRÁ RECUPERAR!" +
                            " \n\nNombre de usuario: " + user.getUsername() +
                            " \nFecha: " + user.getDateBet() +
                            " \nHora: " + user.getTimeBet() +
                            " \nFiabilidad: " + user.getReliable());

                    dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                    dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
                    // Establecer el ancho del diálogo
                    dialog.getDialogPane().setMinWidth(300); // Establece el ancho mínimo
                    dialog.getDialogPane().setPrefWidth(300); // Establece el ancho preferido

                    dialog.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            userViewModel.deleteUser(user);
                            tableUsers.getItems().remove(user);
                        }
                    });
                });

                deleteButton.setStyle("-fx-color: red;");
                Insets rightMargin = new Insets(0, 2, 0, 0);
                buttonContainer.setPadding(rightMargin);
                buttonContainer.setAlignment(Pos.CENTER);
                buttonContainer.getChildren().addAll(deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonContainer);
                }
            }
        });

        columnDelete.setPrefWidth(75);
        columnDelete.sortableProperty().set(false);
        tableUsers.getColumns().add(columnDelete);
    }

    private void extractedUserByRadioButtonFilter(List<UserEntity> usersToShow) {
        if (radioButtonReliableFilter.isSelected()) {
            usersToShow.removeIf(user -> user.getReliable());
        }

        if (radioButtonNoReliableFilter.isSelected()) {
            usersToShow.removeIf(user -> !user.getReliable());
        }
    }

    private void onFilterDataTable() {
        String newUsername = textSearchUserFilter.getText().toUpperCase();
        String newTime = comboTimeFilter.getSelectionModel().getSelectedItem();
        LocalDate newDate = datePickerFilter.getValue();

        Boolean onFilterByDate = onFilterDataTableByDate(newUsername, newTime, newDate);
        Boolean onFilterByTime = onFilterDataTableByTime(newUsername, newTime, newDate);
        Boolean onFilterByUsername = onFilterDataTableByUsername(newUsername, newTime, newDate);
        Boolean onFilterByDateTime = onFilterDataTableByDateTime(newUsername, newTime, newDate);
        Boolean onFilterByUserDate = onFilterDataTableByUserDate(newUsername, newTime, newDate);
        Boolean onFilterByUserTime = onFilterDataTableByUserTime(newUsername, newTime, newDate);
        Boolean onFilterByUserDateTime = onFilterDataTableByUserDateTime(newUsername, newTime, newDate);

        if (!onFilterByUsername && !onFilterByDate && !onFilterByTime && !onFilterByDateTime && !onFilterByUserDate && !onFilterByUserTime && !onFilterByUserDateTime) {
            // Si no hay ningún filtro gordo, muestra todos los datos
            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();

            // Puede haber filtro en los radioButton
            List<UserEntity> usersToShow = userViewModel.getAllEntity();
            extractedUserByRadioButtonFilter(usersToShow);
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
        }

        orderTableByDateTime();
    }

    private Boolean onFilterDataTableByUsername(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername != null && !newUsername.isEmpty() && newTime.equals(noDataTime) && newDate == null) {
            logger.info("Filtering all entity by username");
            List<UserEntity> usersToShow = userViewModel.getAllEntity();

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

    private Boolean onFilterDataTableByDate(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername == null || newUsername.isEmpty() && newTime.equals(noDataTime) && newDate != null) {
            logger.info("Filtering all entity by date");
            List<UserEntity> usersToShow = userViewModel.getAllEntityByDate(newDate);

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
            logger.info("Filtering all entity by time");
            List<UserEntity> usersToShow = userViewModel.getAllEntityByTime(newTime);

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
            logger.info("Filtering all entity by date time");

            List<UserEntity> usersToShow = userViewModel.getAllEntityByDateTime(newTime, newDate);

            extractedUserByRadioButtonFilter(usersToShow);

            tableUsers.setSelectionModel(null);
            tableUsers.getItems().clear();
            tableUsers.setItems(FXCollections.observableArrayList(usersToShow));
            return true;
        }
        return false;
    }

    private Boolean onFilterDataTableByUserDate(String newUsername, String newTime, LocalDate newDate) {
        if (newUsername != null && !newUsername.isEmpty() && newTime.equals(noDataTime) && newDate != null) {
            logger.info("Filtering all entity by user & date");
            List<UserEntity> usersToShow = userViewModel.getAllEntityByDate(newDate);

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
            logger.info("Filtering entity by user & time");

            List<UserEntity> usersToShow = userViewModel.getAllEntityByTime(newTime);

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
            logger.info("Filtering entity by user & date time");

            List<UserEntity> usersToShow = userViewModel.getAllEntityByDateTime(newTime, newDate);

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

    private List<UserEntity> filterUsersByPartialUsername(List<UserEntity> users, String newUsername) {
        List<UserEntity> filteredUsers = new ArrayList<>();

        String regex = "^" + newUsername + ".*";

        for (UserEntity user : users) {
            if (user.getUsername().toUpperCase().matches(regex)) {
                filteredUsers.add(user);
            }
        }

        return filteredUsers;
    }
}
