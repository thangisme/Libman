module com.thangqt.libman {
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires atlantafx.base;
    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;

    opens com.thangqt.libman to javafx.fxml;
    opens com.thangqt.libman.controller to javafx.fxml;
    exports com.thangqt.libman;
    exports com.thangqt.libman.view;
    exports com.thangqt.libman.controller;
}