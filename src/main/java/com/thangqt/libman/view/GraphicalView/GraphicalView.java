package com.thangqt.libman.view.GraphicalView;

import atlantafx.base.theme.PrimerLight;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GraphicalView extends Application {
  public static void main(String[] args) {
    launch();
  }

  @Override
  public void start(Stage stage) throws IOException {
    Application.setUserAgentStylesheet(new PrimerLight().getUserAgentStylesheet());

    FXMLLoader loginLoader =
        new FXMLLoader(getClass().getResource("/com/thangqt/libman/fxml/login.fxml"));
    Scene loginScene = new Scene(loginLoader.load(), 1360, 760);
    stage.setScene(loginScene);
    stage.setTitle("Login");
    stage.show();
  }
}
