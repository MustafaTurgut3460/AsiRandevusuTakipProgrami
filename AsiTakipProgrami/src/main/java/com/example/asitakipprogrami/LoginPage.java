package com.example.asitakipprogrami;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class LoginPage implements Initializable {

    // Database Connection
    DatabaseConnection connection = new DatabaseConnection();
    Statement statement = connection.getStatement();

    @FXML
    private Button loginButton;
    @FXML
    private Label newAccountLabel;
    @FXML
    private TextField TCInput;
    @FXML
    private PasswordField passwordInput;
    @FXML
    private Pane nursePane, patientPane;
    @FXML
    private ImageView mainIcon;
    @FXML
    private Label mainTitle, questionLabel, loginTitle;
    @FXML
    private AnchorPane loginPane;

    private String loginType = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // personel giris yapacak
        nursePane.setOnMouseClicked(mouseEvent -> {
            loginType = "nurse";
            loginTitle.setText("PERSONEL GİRİŞİ");
            TCInput.setPromptText("Kullanıcı Adı");
            setItemsVisibility("nurse");
        });

        // hasta giris yapacak
        patientPane.setOnMouseClicked(mouseEvent -> {
            loginType = "patient";
            loginTitle.setText("HASTA GİRİŞİ");
            TCInput.setPromptText("TC Kimlik No");
            setItemsVisibility("patient");
        });

        // kayıt ol sayfasına gidiyor
        newAccountLabel.setOnMouseClicked(mouseEvent -> {
            openPage("register_page.fxml");
        });

        loginButton.setOnMouseClicked(mouseEvent -> {
            String TC = TCInput.getText();
            String password = passwordInput.getText();

            // eger uzunluk dogru ise ve numeric ise kayıt yap
            if(TC.length() == 11 && isNumeric(TC)){
                loginUser(TC, password, loginType); // eger 11 haneli bir giris ise
            }
            else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText("Giriş Başarısız!");
                alert.setContentText("TC kimlik numarasını doğru girdiğinizden emin olunuz!");
                alert.show();
            }
        });
    }

    private void loginUser(String TC, String password, String loginType){
        try {
            if(loginType.equals("patient")){
                // kullanici girisi
                ResultSet resultSet = statement.executeQuery("SELECT tc, sifre FROM kullanicilar");
                while(resultSet.next()){
                    if(TC.equals(resultSet.getString("tc")) && password.equals(resultSet.getString("sifre"))){
                        // giris islemini yap
                        String resetQuery = "UPDATE kullanicilar SET durum='offline'"; // diger kullanıcıları offline yap
                        String updateQuery = "UPDATE kullanicilar SET durum='online' WHERE sifre='" + password + "'"; // giris yapani online yap
                        Statement statement1 = connection.getStatement();
                        statement1.execute(resetQuery);
                        statement1.execute(updateQuery);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Giriş Başarılı!");
                        alert.show();

                        openPage("home_page.fxml");
                        break;
                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Giriş Başarısız!");
                        alert.setContentText("Lütfen girilen bilgileri kontrol ediniz...");
                        alert.show();
                    }
                }
            }
            else{
                // personel girisi
                ResultSet resultSet = statement.executeQuery("SELECT kullaniciAdi, sifre FROM personeller");
                while(resultSet.next()){
                    if(TC.equals(resultSet.getString("kullaniciAdi")) && password.equals(resultSet.getString("sifre"))){
                        // giris islemini yap
                        String resetQuery = "UPDATE personeller SET durum='offline'";
                        String updateQuery = "UPDATE personeller SET durum='online' WHERE sifre='" + password + "'";
                        Statement statement1 = connection.getStatement();
                        statement1.execute(resetQuery);
                        statement1.execute(updateQuery);

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Giriş Başarılı!");
                        alert.show();

                        openPage("personel_page.fxml");
                        break;
                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Giriş Başarısız!");
                        alert.setContentText("Lütfen girilen bilgileri kontrol ediniz...");
                        alert.show();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openPage(String fxml){
        FXMLLoader FXML = new FXMLLoader(getClass().getResource(fxml));
        try {
            // show register page
            Stage stage = (Stage) newAccountLabel.getScene().getWindow();
            Scene scene = new Scene(FXML.load(), 1345, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setItemsVisibility(String type){

        if(type.equals("nurse")){
            nursePane.setVisible(false);
            patientPane.setVisible(false);
            newAccountLabel.setVisible(false);
            questionLabel.setVisible(false);

            mainIcon.setVisible(true);
            mainTitle.setVisible(true);
            loginPane.setVisible(true);
        }
        else{
            nursePane.setVisible(false);
            patientPane.setVisible(false);

            mainIcon.setVisible(true);
            mainTitle.setVisible(true);
            loginPane.setVisible(true);
        }
    }

    private boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            long l = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}






















