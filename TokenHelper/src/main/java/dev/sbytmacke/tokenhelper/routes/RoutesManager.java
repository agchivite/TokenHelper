package dev.sbytmacke.tokenhelper.routes;

import dev.sbytmacke.tokenhelper.AppMain;
import dev.sbytmacke.tokenhelper.controllers.DataGestorViewController;
import dev.sbytmacke.tokenhelper.controllers.MainViewController;
import dev.sbytmacke.tokenhelper.repositories.UserRepositoryImpl;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManagerImpl;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class RoutesManager {

    // El viewModel llamará a todas las funciones del repositorio a través de la vista y gracias al controlador
    UserViewModel userViewModel = new UserViewModel(new UserRepositoryImpl(new DatabaseManagerImpl())); // Acoplamiento
    private Stage _mainStage;
    private Stage _activeStage;
    private Scene _activeScene;

    public static Locale getScene() {
        return null;
    }

    public void initMainView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();

        MainViewController controller = fxmlLoader.getController(); // Obtenemos el controlador
        controller.init(userViewModel); // Inyección de dependencias desde el DatabaseManager hasta el Controller

        Scene scene = new Scene(root, 1325, 810);
        stage.setResizable(false);
        stage.setTitle("TokenHelper");
        // Agregar un icono a la ventana
        stage.getIcons().add(new Image("/dev/sbytmacke/tokenhelper/icons/main_icon.png"));

        _mainStage = stage;
        _activeStage = stage;
        _activeScene = scene;

        stage.setScene(scene);
        stage.show();
    }

    public void intiDeleteView() throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("data-gestor-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        DataGestorViewController controller = fxmlLoader.getController(); // Obtenemos el controlador
        controller.init(userViewModel); // Inyección de dependencias desde el DatabaseManager hasta el Controller

        Stage stage = new Stage();
        stage.setTitle("Visualizador de datos");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/dev/sbytmacke/tokenhelper/icons/main_icon.png"));

        stage.setScene(scene);
        stage.initOwner(_mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.show();
    }

    public Stage getActiveStage() {
        return _activeStage;
    }
}
