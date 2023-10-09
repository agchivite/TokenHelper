module dev.sbytmacke.tokenhelper {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;
    requires org.slf4j;


    opens dev.sbytmacke.tokenhelper to javafx.fxml;
    exports dev.sbytmacke.tokenhelper;

    opens dev.sbytmacke.tokenhelper.controllers to javafx.fxml;
    exports dev.sbytmacke.tokenhelper.controllers;
}
