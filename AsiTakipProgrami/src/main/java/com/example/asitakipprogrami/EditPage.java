package com.example.asitakipprogrami;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EditPage implements Initializable {

    @FXML
    private Button saveButton;

    @FXML
    private DatePicker editDateInput;

    @FXML
    private TextField editNameInput, editTCInput, editTelInput;

    @FXML
    private RadioButton editRadioFemale, editRadioMale;

    private DatabaseConnection connection = new DatabaseConnection();
    private Statement statement = connection.getStatement();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setDataToFields();

        saveButton.setOnMouseClicked(mouseEvent -> {
            // yeni verileri kaydet
            String name = editNameInput.getText();
            String tc = editTCInput.getText();
            String tel = editTelInput.getText();
            String tarih = editDateInput.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
            String cinsiyet = null;
            if(editRadioMale.isSelected())
                cinsiyet = "Erkek";
            else
                cinsiyet = "Kadın";

            saveData(name, tc, tel, tarih, cinsiyet);

            // ana sayfayi ac
            openPage("home_page.fxml");
        });

    }

    private void saveData(String name, String tc, String tel, String tarih, String cinsiyet) {
        try {
            // sql sorgusu
            String sqlQuery = "UPDATE kullanicilar SET name='" + name + "', tc='" + tc + "', tel_no='"
                            + tel + "', dogum_tarihi='" + tarih + "', cinsiyet='" + cinsiyet + "' WHERE durum='online'";

            statement.execute(sqlQuery);
            statement.close();

            // kullaniciya mesaj verildi
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Güncelleme Tamamlandı!");
            alert.setContentText("Verileriniz başarıyla güncellendi...");
            alert.show();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setDataToFields(){
        try {
            // sql sorgusu hangi kullanici online ise verileri cektik
            ResultSet resultSet = statement.executeQuery("SELECT * FROM kullanicilar WHERE durum='online'");
            while(resultSet.next()){
                editNameInput.setText(resultSet.getString("name"));
                editTCInput.setText(resultSet.getString("tc"));
                editTelInput.setText(resultSet.getString("tel_no"));
                editDateInput.setValue(LocalDate.parse(resultSet.getString("dogum_tarihi"), DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                if(resultSet.getString("cinsiyet").equals("Erkek"))
                    editRadioMale.setSelected(true);
                else
                    editRadioFemale.setSelected(true);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openPage(String fxml){
        FXMLLoader FXML = new FXMLLoader(getClass().getResource(fxml));
        try {
            // aynı stage üzerine farklı bir fxml file actik
            Stage stage = (Stage) saveButton.getScene().getWindow();
            Scene scene = new Scene(FXML.load(), 1345, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}






















