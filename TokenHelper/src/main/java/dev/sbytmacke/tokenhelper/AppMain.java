package dev.sbytmacke.tokenhelper;

import dev.sbytmacke.tokenhelper.controllers.MainViewController;
import dev.sbytmacke.tokenhelper.repositories.UserRepositoryImpl;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManagerImpl;
import dev.sbytmacke.tokenhelper.viewmodel.UserViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppMain extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(AppMain.class.getResource("main-view.fxml"));
        Parent root = fxmlLoader.load();
        MainViewController controller = fxmlLoader.getController(); // Obtenemos el controlador
        // El viewModel llamará a todas las funciones del repositorio a través de la vista y gracias al controlador
        UserViewModel userViewModel = new UserViewModel(new UserRepositoryImpl(new DatabaseManagerImpl()));
        controller.init(userViewModel); // Inyección de dependencias desde el DatabaseManager hasta el Controller

        Scene scene = new Scene(root, 1339, 744);
        stage.setResizable(false);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
