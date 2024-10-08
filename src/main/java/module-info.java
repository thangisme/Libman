module com.thangqt.libman {
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires atlantafx.base;


    opens com.thangqt.libman to javafx.fxml;
    exports com.thangqt.libman;
}