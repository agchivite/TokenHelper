package dev.sbytmacke.tokenhelper;

import dev.sbytmacke.tokenhelper.routes.RoutesManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class AppMain extends Application {
    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        RoutesManager routesManager = new RoutesManager();
        routesManager.initMainView(stage);
    }
}
