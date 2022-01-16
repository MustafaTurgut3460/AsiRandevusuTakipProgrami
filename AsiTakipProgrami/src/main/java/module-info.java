module com.example.asitakipprogrami {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.asitakipprogrami to javafx.fxml;
    exports com.example.asitakipprogrami;
}