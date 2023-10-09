module dev.sbytmacke.tokenhelper {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.mongodb.driver.sync.client;
    requires org.mongodb.bson;


    opens dev.sbytmacke.tokenhelper to javafx.fxml;
    exports dev.sbytmacke.tokenhelper;
}
