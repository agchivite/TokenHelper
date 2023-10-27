package dev.sbytmacke.tokenhelper.routes;

import dev.sbytmacke.tokenhelper.AppMain;
import dev.sbytmacke.tokenhelper.controllers.DataGestorViewController;
import dev.sbytmacke.tokenhelper.controllers.MainMiniViewController;
import dev.sbytmacke.tokenhelper.controllers.MainViewController;
import dev.sbytmacke.tokenhelper.dto.UserDTO;
import dev.sbytmacke.tokenhelper.repositories.UserRepositoryImpl;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManagerImpl;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;

public class RoutesManager {

    private static Stage _mainStage;
    private static Stage _activeStage;
    private static Scene _activeScene;
    UserViewModel userViewModel = new UserViewModel(new UserRepositoryImpl(new DatabaseManagerImpl())); // Acoplamiento

    public static Locale getScene() {
        return null;
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
        stage.getIcons().add(new Image("/dev/sbytmacke/tokenhelper/icons/main_icon.png"));

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
        stage.getIcons().add(new Image("/dev/sbytmacke/tokenhelper/icons/main_icon.png"));

        stage.setScene(scene);
        stage.initOwner(_mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.show();
    }

    public Stage getMainStage() {
        return _mainStage;
    }

    public Stage getActiveStage() {
        return _activeStage;
    }

    public void initLeyendaView() {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("leyenda-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Stage stage = new Stage();
        stage.setTitle("Leyenda");
        stage.setResizable(false);
        stage.getIcons().add(new Image("/dev/sbytmacke/tokenhelper/icons/main_icon.png"));

        stage.setScene(scene);
        stage.initOwner(_mainStage);
        stage.initModality(Modality.WINDOW_MODAL);

        stage.show();
    }

    public void initMainMiniView(TableView<UserDTO> tableUsers) {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("main-mini-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MainMiniViewController controller = fxmlLoader.getController(); // Obtenemos el controlador
        controller.init(tableUsers);

        Stage stage = new Stage();
        stage.setTitle("Mini-view");
        stage.setResizable(true);
        stage.getIcons().add(new Image("/dev/sbytmacke/tokenhelper/icons/main_icon.png"));

        // EventHandler para el evento de cierre de la ventana
        stage.setOnCloseRequest(event -> {
            _mainStage.show();
        });

        stage.setScene(scene);
        stage.initOwner(_mainStage);

        _mainStage.hide();
        stage.show();
    }
}
