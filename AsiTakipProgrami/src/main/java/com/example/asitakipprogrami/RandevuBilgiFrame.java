package com.example.asitakipprogrami;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class RandevuBilgiFrame implements Initializable {

    @FXML
    private VBox gecmisRandevuVBox;
    @FXML
    private ImageView gecmisEmptyImage;
    @FXML
    private Label gecmisEmptyLabel;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Button cancelButton;
    @FXML
    private Label dateText, hospitalText, roomText, vaccineText;

    @FXML
    private Pane aktifRandevuPane;
    @FXML
    private ImageView aktifEmptyImage;
    @FXML
    private Label aktifEmptyLabel;

    private final EskiRandevuItem item = new EskiRandevuItem();

    private final DatabaseConnection connection = new DatabaseConnection();
    private String currentUsername = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setAktifRandevuItem();

        cancelButton.setOnMouseClicked(mouseEvent -> {
            ButtonType ok = new ButtonType("Evet", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancel = new ButtonType("Hayır", ButtonBar.ButtonData.CANCEL_CLOSE);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Randevunuzu iptal etmek istiyor musunuz?", ok, cancel);
            alert.setHeaderText("Randevu İptal");

            Optional<ButtonType> result = alert.showAndWait();

            //alert.getButtonTypes().clear();
            alert.getButtonTypes().add(ok);
            alert.getButtonTypes().add(cancel);

            if(result.get().equals(cancel)){
                alert.close();
            }
            else if(result.get().equals(ok)){
                // randevu tipini iptal randevu olarak degistir
                try {
                    Statement statement = connection.getStatement();
                    ResultSet resultSet = statement.executeQuery("SELECT name FROM kullanicilar WHERE durum='online'");
                    while (resultSet.next()){
                        currentUsername = resultSet.getString("name");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                updateRandevuTip(currentUsername, item.getAktifRandevuList().get(0).getTarih());

                openPage("home_page.fxml");
            }
        });

        // eger gecmis bir randevusu yoksa gorunur yap
        if(item.getGecmisRandevuList().size()==0){
            gecmisEmptyLabel.setVisible(true);
            gecmisEmptyImage.setVisible(true);
            scrollPane.setVisible(false);
        }
        else{
            gecmisEmptyLabel.setVisible(false);
            gecmisEmptyImage.setVisible(false);
            scrollPane.setVisible(true);
        }

        Node[] nodes = new Node[item.getGecmisRandevuList().size()];

        for(int i=0 ; i< nodes.length ; i++){
            try {

                nodes[i] = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("eski_randevu_item.fxml")));
                gecmisRandevuVBox.getChildren().add(nodes[i]);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void updateRandevuTip(String currentUsername, String tarih) {
        Statement statement = connection.getStatement();
        try {
            statement.execute("UPDATE randevular SET tip='iptal' WHERE hasta_ad='" + currentUsername + "' AND tarih='" + tarih + "'");
            statement.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Randevu İptal Edildi!");
            alert.setContentText("Randevunuz başarıyla iptal edildi...");
            alert.show();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setAktifRandevuItem() {

        if(item.getAktifRandevuList().size() == 0){
            // aktif randevu item gorunmez yap
            aktifRandevuPane.setVisible(false);
            aktifEmptyImage.setVisible(true);
            aktifEmptyLabel.setVisible(true);
        }
        else{
            aktifRandevuPane.setVisible(true);
            aktifEmptyImage.setVisible(false);
            aktifEmptyLabel.setVisible(false);

            ModelRandevu aktifRandevu = item.getAktifRandevuList().get(0);
            // aktif randevu var, verileri field ların icine at
            dateText.setText(aktifRandevu.getTarih() + " " + aktifRandevu.getSaat());

            if(aktifRandevu.getBirim().equals("Hastane"))
                hospitalText.setText(aktifRandevu.getHastaneAd());
            else
                hospitalText.setText(aktifRandevu.getBirim());

            roomText.setText(aktifRandevu.getAsiOdasi());
            vaccineText.setText("Covid-19 Aşı Randevusu (" + aktifRandevu.getAsi() + ")");
        }
    }

    private void openPage(String fxml){
        FXMLLoader FXML = new FXMLLoader(getClass().getResource(fxml));
        try {
            // aynı stage üzerine farklı bir fxml file actik
            Stage stage = (Stage) gecmisRandevuVBox.getScene().getWindow();
            Scene scene = new Scene(FXML.load(), 1345, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


















