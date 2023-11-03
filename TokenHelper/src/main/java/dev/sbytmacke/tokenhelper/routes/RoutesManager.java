package dev.sbytmacke.tokenhelper.routes;

import dev.sbytmacke.tokenhelper.AppMain;
import dev.sbytmacke.tokenhelper.controllers.DataGestorViewController;
import dev.sbytmacke.tokenhelper.controllers.MainMiniViewController;
import dev.sbytmacke.tokenhelper.controllers.MainViewController;
import dev.sbytmacke.tokenhelper.controllers.UpdateViewController;
import dev.sbytmacke.tokenhelper.repositories.UserRepositoryImpl;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManagerImpl;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class RoutesManager {

    private static Stage _mainStage;
    private static Stage _activeStage;
    private static Scene _activeScene;
    Logger logger = LoggerFactory.getLogger(getClass());
    UserViewModel userViewModel = new UserViewModel(new UserRepositoryImpl(new DatabaseManagerImpl())); // Acoplamiento

    public static Stage getMainStage() {
        return _mainStage;
    }

    public void initMainView(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();

        MainViewController controller = fxmlLoader.getController(); // Obtenemos el controlador
        controller.init(userViewModel); // Inyección de dependencias desde el DatabaseManager hasta el Controller

        Scene scene = new Scene(root, Control.USE_COMPUTED_SIZE, 810);
        stage.setResizable(false);
        stage.setTitle("TokenHelper");
        // Agregar un icono a la ventana
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm()));

        _mainStage = stage;
        _activeStage = stage;
        _activeScene = scene;

        stage.setScene(scene);
        stage.show();
    }

    public void intiDataGestorView(MainViewController mainViewController) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("data-gestor-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        DataGestorViewController controller = fxmlLoader.getController(); // Obtenemos el controlador
        controller.setMainController(mainViewController); // Para poder acceder a la función de actualizar las tablas
        controller.init(userViewModel); // Inyección de dependencias desde el DatabaseManager hasta el Controller

        Stage stage = new Stage();
        stage.setTitle("Visualizador de datos");
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm()));

        stage.setScene(scene);
        stage.initOwner(_mainStage);
        stage.initModality(Modality.NONE); // Para poder interactuar con la pantalla principal

        stage.show();
    }

    public Stage getActiveStage() {
        return _activeStage;
    }

    public void initLeyendaView() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("leyenda-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        Stage stage = new Stage();
        stage.setTitle("Leyenda");
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm()));

        stage.setScene(scene);
        stage.initOwner(_mainStage);
        stage.initModality(Modality.NONE); // Para poder interactuar con la pantalla principal

        stage.show();
    }

    public void initMainMiniView(MainViewController mainViewController) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("main-mini-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        MainMiniViewController controller = fxmlLoader.getController(); // Obtenemos el controlador
        controller.init(mainViewController);

        Stage stage = new Stage();
        stage.setTitle("Mini-view");
        stage.setResizable(true);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm()));

        // EventHandler para el evento de cierre de la ventana
        stage.setOnCloseRequest(event -> {
            _mainStage.show();
        });

        stage.setScene(scene);
        stage.initOwner(_mainStage);

        _mainStage.hide();
        stage.show();
    }

    public void initUpdateView(MainViewController mainViewController) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("update-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        UpdateViewController controller = fxmlLoader.getController();
        controller.init(mainViewController);

        Stage stage = new Stage();
        stage.setTitle("Actualizar datos");
        stage.setResizable(false);
        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/dev/sbytmacke/tokenhelper/css/comboBox.css")).toExternalForm()));

        stage.setScene(scene);
        stage.initOwner(_mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.show();
    }
}
