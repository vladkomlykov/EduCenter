module org.example.educenter {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires static lombok;
    requires java.sql;

    opens org.example.educenter to javafx.fxml;
    exports org.example.educenter;
    exports org.example.educenter.controllers;
    opens org.example.educenter.controllers to javafx.fxml;
}