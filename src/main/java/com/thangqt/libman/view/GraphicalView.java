package com.thangqt.libman.view;

import atlantafx.base.theme.PrimerLight;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GraphicalView extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/thangqt/libman/fxml/dashboard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1360, 760);
        scene.getStylesheets().add(getClass().getResource("/com/thangqt/libman/style.css").toExternalForm());

        stage.setTitle("Libman");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
