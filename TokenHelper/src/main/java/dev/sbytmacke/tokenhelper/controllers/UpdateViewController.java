package dev.sbytmacke.tokenhelper.controllers;

import dev.sbytmacke.tokenhelper.routes.RoutesManager;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UpdateViewController {
    private MainViewController mainViewController;
    @FXML
    private TextField textFieldOldUsername;
    @FXML
    private TextField textFieldNewUsername;
    @FXML
    private Button buttonConfirmUpdate;

    public void init(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
        initEvents();
    }


    private void initEvents() {
        buttonConfirmUpdate.setOnAction(event -> {
            // Filtro
            if (textFieldOldUsername.getText().isEmpty() || textFieldNewUsername.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al actualizar los datos");
                alert.setContentText("No se puede actualizar los datos porque hay campos vacíos");
                alert.showAndWait();
                return;
            }

            // Filtrar que el oldUserName no exista en la base de datos
            if (!mainViewController.getUserViewModel().getAllUsernamesNoRepeat().contains(textFieldOldUsername.getText().toUpperCase().trim())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al actualizar los datos");
                alert.setContentText("El nombre de usuario antiguo no existe en la base de datos");
                alert.showAndWait();
                return;
            }

            // Crear una imagen y agregarla al diálogo
            Image image = new Image(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/icons/update.png")).toExternalForm());
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            Dialog dialog = new Dialog();
            dialog.setGraphic(imageView);
            dialog.setTitle("Actualizar datos");
            dialog.setContentText("¿Deseas actualizar los datos?" +
                    " \n\nAntiguo nombre de usuario: " + "\n" + textFieldOldUsername.getText() +
                    " \nNuevo nombre de usuario: " + "\n" + textFieldNewUsername.getText());

            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
            // Establecer el ancho del diálogo
            dialog.getDialogPane().setMinWidth(300); // Establece el ancho mínimo
            dialog.getDialogPane().setPrefWidth(300); // Establece el ancho preferido

            // Heredar el ícono de la ventana principal
            Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
            dialogStage.getIcons().addAll(RoutesManager.getMainStage().getIcons());

            dialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    mainViewController.getUserViewModel().updateUsername(textFieldOldUsername.getText().toUpperCase().trim(), textFieldNewUsername.getText().toUpperCase().trim());
                    mainViewController.updateMainTable();
                    textFieldOldUsername.setText("");
                    textFieldNewUsername.setText("");
                }
            });
        });

        final ContextMenu contextMenu = new ContextMenu();
        textFieldOldUsername.textProperty().addListener((observable, oldValue, newValue) -> {
            contextMenu.getItems().clear();

            List<String> listSuggestions = filterSuggestionsList(newValue);

            // Agrega sugerencias al ContextMenu basadas en el valor del TextField
            for (String suggestion : listSuggestions) {
                MenuItem menuItem = new MenuItem(suggestion);
                //menuItem.setStyle("-fx-text-fill: white;"); // Cambia el color del texto a blanco
                contextMenu.getItems().add(menuItem);
            }

            contextMenu.show(textFieldOldUsername, Side.BOTTOM, 0, 0);

            if (newValue == null || newValue.isEmpty()) {
                contextMenu.getItems().clear();
            }
        });
        contextMenu.setOnAction(event -> {
            MenuItem selectedItem = (MenuItem) event.getTarget();

            if (textFieldOldUsername.isFocused()) {
                textFieldOldUsername.setText(selectedItem.getText());
            }
            contextMenu.hide();
        });
    }

    private List<String> filterSuggestionsList(String input) {
        List<String> allUsernames = mainViewController.getUserViewModel().getAllUsernamesNoRepeat();
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

}
