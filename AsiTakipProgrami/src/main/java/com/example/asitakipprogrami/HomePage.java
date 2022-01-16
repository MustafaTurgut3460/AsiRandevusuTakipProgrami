package com.example.asitakipprogrami;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomePage implements Initializable{

    @FXML
    private AnchorPane sideMenu;
    @FXML
    private Pane mainPane;
    @FXML
    private AnchorPane iconMenu;
    @FXML
    private Label randevuBilgiLabel, randevuAlLabel, kullaniciBilgiLabel;
    @FXML
    private ImageView logout;

    private final AnchorPane randevuBilgiFrame = new FXMLLoader(getClass().getResource("randevu_bilgi_frame.fxml")).load();
    private final AnchorPane randevuAlFrame = new FXMLLoader(getClass().getResource("randevu_al_frame.fxml")).load();
    //private final AnchorPane covidBilgiFrame = new FXMLLoader(getClass().getResource("covid_bilgi_frame.fxml")).load();
    private final AnchorPane kullaniciBilgiFrame = new FXMLLoader(getClass().getResource("kullanici_bilgi_frame.fxml")).load();

    // fxml yükleme sırasında hata meydana gelebilirse diye exception fırlatmak için
    public HomePage() throws IOException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // randevu bilgi frame gözükecek default olarak
        mainPane.getChildren().add(randevuBilgiFrame);

        // side menu sakla
        sideMenu.setTranslateX(-244);

        logout.setOnMouseClicked(mouseEvent -> {
            // logout
            ButtonType ok = new ButtonType("Evet", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Hayır", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Çıkış yapmak istiyor musunuz?", ok, cancel);
            alert.setHeaderText("Çıkış Yap");

            Optional<ButtonType> result = alert.showAndWait();

            //alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ok);
            alert.getButtonTypes().add(cancel);

            if(result.get().equals(cancel)){
                alert.close();
            }
            else if(result.get().equals(ok)){
                // giris sayfasini ac
                openPage("login_page.fxml");
            }
        });

        iconMenu.setOnMouseEntered(mouseEvent -> {
            openSideMenu();
        });

        sideMenu.setOnMouseExited(mouseEvent -> {
            closeSideMenu();
        });

        // menu onclick
        randevuBilgiLabel.setOnMouseClicked(mouseEvent -> {
            // set randevu bilgi frame
            setFrame(randevuBilgiFrame);
            closeSideMenu();
        });

        randevuAlLabel.setOnMouseClicked(mouseEvent -> {
            // set randevu al frame
            setFrame(randevuAlFrame);
            closeSideMenu();
        });

        /*covidBilgiLabel.setOnMouseClicked(mouseEvent -> {
            // set covid bilgi frame
            setFrame(covidBilgiFrame);
            closeSideMenu();
        });*/

        kullaniciBilgiLabel.setOnMouseClicked(mouseEvent -> {
            // set kullanici bilgi frame
            setFrame(kullaniciBilgiFrame);
            closeSideMenu();
        });
    }

    private void setFrame(AnchorPane newFrame){
        //içindeki frameleri temizle ve istenileni koy
        mainPane.getChildren().clear();
        mainPane.getChildren().add(newFrame);
    }

    private void openSideMenu(){
        // side bar kaydır ve göster
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.3));
        slide.setNode(sideMenu);

        slide.setToX(0);
        slide.play();

        sideMenu.setTranslateX(-244);

        // menu açıldığında arka planın rengini daha soluk yap
        mainPane.setOpacity(0.5);
    }

    private void closeSideMenu(){
        // side bar kaydır ve gizle
        TranslateTransition slide = new TranslateTransition();
        slide.setDuration(Duration.seconds(0.3));
        slide.setNode(sideMenu);

        slide.setToX(-244);
        slide.play();

        sideMenu.setTranslateX(0);

        // main pane arkaplanını normale dönder
        mainPane.setOpacity(1);
    }

    private void openPage(String fxml){
        FXMLLoader FXML = new FXMLLoader(getClass().getResource(fxml));
        try {
            // show register page
            Stage stage = (Stage) iconMenu.getScene().getWindow();
            Scene scene = new Scene(FXML.load(), 1345, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






















