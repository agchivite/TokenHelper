package dev.sbytmacke.tokenhelper;

import dev.sbytmacke.tokenhelper.models.User;
import dev.sbytmacke.tokenhelper.repositories.Repository;
import dev.sbytmacke.tokenhelper.repositories.UserRepositoryImpl;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManager;
import dev.sbytmacke.tokenhelper.services.database.DatabaseManagerImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

public class HelloApplication extends Application {
    public static void main(String[] args) {
        DatabaseManager databaseManager = new DatabaseManagerImpl();
        Repository<User, String> repository = new UserRepositoryImpl(databaseManager);

        User userToTest = new User("Angelito", LocalDate.now(), LocalTime.now(), true);
        repository.addItem(userToTest);
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }
}
