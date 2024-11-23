module com.thangqt.libman {
  requires javafx.fxml;
  requires java.sql;
  requires java.desktop;
  requires atlantafx.base;
  requires org.kordamp.ikonli.core;
  requires org.kordamp.ikonli.javafx;
  requires org.kordamp.ikonli.feather;
  requires dotenv.java;
  requires org.apache.httpcomponents.httpclient;
  requires org.apache.httpcomponents.httpcore;
  requires com.google.gson;
  requires jbcrypt;
  requires commons.validator;
  requires com.google.zxing;
  requires com.google.zxing.javase;
  requires opencv;

  opens com.thangqt.libman to
      javafx.fxml;
  opens com.thangqt.libman.controller to
      javafx.fxml;

  exports com.thangqt.libman;
  exports com.thangqt.libman.view;
  exports com.thangqt.libman.controller;
  exports com.thangqt.libman.view.GraphicalView;
}
