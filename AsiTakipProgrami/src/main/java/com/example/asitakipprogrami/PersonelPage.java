package com.example.asitakipprogrami;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;
import java.util.ResourceBundle;

public class PersonelPage implements Initializable {

    @FXML
    private Button button;
    @FXML
    private TableColumn<ModelRandevu, String> clockColumn;
    @FXML
    private TableColumn<ModelRandevu, String> dateColumn;
    @FXML
    private TableColumn<ModelRandevu, String> nameColumn;
    @FXML
    private TableColumn<ModelRandevu, String> roomColumn;
    @FXML
    private TableView<ModelRandevu> tableView;
    @FXML
    private TableColumn<ModelRandevu, String> typeColumn;
    @FXML
    private Button exitButton;
    @FXML
    private Label hospitalText, nameText, usernameText;

    private String userHospital;

    ObservableList<ModelRandevu> randevuList = FXCollections.observableArrayList();

    DatabaseConnection connection = new DatabaseConnection();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // personelin bilgilerini aktar
        Statement statement1 = connection.getStatement();
        try {
            ResultSet resultSet1 = statement1.executeQuery("SELECT * FROM personeller WHERE durum='online'");
            while (resultSet1.next()){
                String name = resultSet1.getString("ad");
                String username = resultSet1.getString("kullaniciAdi");
                String hospital = resultSet1.getString("hastane");

                hospitalText.setText(hospital);
                nameText.setText(name);
                usernameText.setText(username);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // tablo sutunları
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("hastaAd"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("tarih"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("asi"));
        clockColumn.setCellValueFactory(new PropertyValueFactory<>("saat"));
        roomColumn.setCellValueFactory(new PropertyValueFactory<>("asiOdasi"));

        getUserHospital();

        try {

            if(userHospital.equals("Aile Sağlığı Merkezi")){
                // sağlık ocağı randevuları
                Statement statement2 = connection.getStatement();
                ResultSet resultSet2 = statement2.executeQuery("SELECT * FROM randevular WHERE tip='aktif' AND birim='Aile Sağlığı Merkezi'");

                while (resultSet2.next()){
                    String name = resultSet2.getString("hasta_ad");
                    String type = resultSet2.getString("asi_tipi");
                    String room = resultSet2.getString("asi_odasi");
                    String date = resultSet2.getString("tarih");
                    String clock = resultSet2.getString("saat");

                    ModelRandevu randevu = new ModelRandevu("", name, type, "Aile Sağlığı Merkezi", "", room, date, clock);
                    randevuList.add(randevu);
                }
            }
            else{
                // kendi hastanesindekileri ekliyor
                Statement statement = connection.getStatement();
                ResultSet resultSet = statement.executeQuery("SELECT * FROM randevular WHERE tip='aktif' AND hastane_ad='" + userHospital + "'");
                while (resultSet.next()){
                    String name = resultSet.getString("hasta_ad");
                    String type = resultSet.getString("asi_tipi");
                    String hospital = resultSet.getString("hastane_ad");
                    String room = resultSet.getString("asi_odasi");
                    String date = resultSet.getString("tarih");
                    String clock = resultSet.getString("saat");

                    ModelRandevu randevu = new ModelRandevu("", name, type, "Hastane", hospital, room, date, clock);
                    randevuList.add(randevu);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tableView.setItems(randevuList);

        button.setOnMouseClicked(mouseEvent -> {
            if(tableView.getSelectionModel().getSelectedItem() != null){
                // emin misin sor
                ButtonType ok = new ButtonType("Evet", ButtonBar.ButtonData.OK_DONE);
                ButtonType cancel = new ButtonType("Hayır", ButtonBar.ButtonData.CANCEL_CLOSE);

                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Tamamlandı olarak işaretlenecek, emin misiniz?", ok, cancel);
                alert.setHeaderText("Randevuyu Tamamla");

                Optional<ButtonType> result = alert.showAndWait();

                alert.getButtonTypes().add(ok);
                alert.getButtonTypes().add(cancel);

                if(result.get().equals(cancel)){
                    alert.close();
                }
                else if(result.get().equals(ok)){
                    // randevuyu gecmis olarak guncelle
                    ModelRandevu randevu = new ModelRandevu();
                    randevu = tableView.getSelectionModel().getSelectedItem();
                    String saat = randevu.getSaat();

                    Statement statement = connection.getStatement();
                    try {
                        statement.execute("UPDATE randevular SET tip='gecmis' WHERE saat='" + saat + "'");
                        statement.close();

                        updateUserVaccine(randevu.getHastaAd());

                        randevuList.remove(randevu);

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            else{
                // item seçilmemiş
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("HATA!");
                alert.setContentText("Lütfen bir hasta seçiniz!");
                alert.show();
            }
        });

        exitButton.setOnMouseClicked(mouseEvent -> {
            // çıkış yap
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
    }

    private void updateUserVaccine(String hastaAd) {
        Statement statement = connection.getStatement();
        try{
            if(getUserVaccineValue(hastaAd) == 0){
                // BURADA KALDIM
                statement.execute("UPDATE ");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void getUserHospital() {
        // hangi personelin giris yaptgini tespit ediyoruz ve hastane bilgisini cekiyoruz
        Statement statement = connection.getStatement();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT hastane FROM personeller WHERE durum='online'");
            while (resultSet.next()){
                userHospital = resultSet.getString("hastane");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getUserVaccineValue(String hastaAd) {

        int value = 0;
        Statement statement = connection.getStatement();
        try {
            // hasta adi ile eslesme
            ResultSet resultSet = statement.executeQuery("SELECT tip FROM randevular WHERE hasta_ad='" + hastaAd + "'");
            while (resultSet.next()){
                String tip = resultSet.getString("tip");
                if(tip.equals("gecmis"))
                    value++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return value;
    }

    private void openPage(String fxml){
        FXMLLoader FXML = new FXMLLoader(getClass().getResource(fxml));
        try {
            // show register page
            Stage stage = (Stage) button.getScene().getWindow();
            Scene scene = new Scene(FXML.load(), 1345, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}




















