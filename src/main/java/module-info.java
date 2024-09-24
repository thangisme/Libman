module com.thangqt.libman {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;


    opens com.thangqt.libman to javafx.fxml;
    exports com.thangqt.libman;
}