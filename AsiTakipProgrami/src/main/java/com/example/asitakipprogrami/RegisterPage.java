package com.example.asitakipprogrami;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RegisterPage implements Initializable {

    private DatabaseConnection connection = new DatabaseConnection();
    private Statement statement = connection.getStatement();

    @FXML
    private RadioButton femaleBox;

    @FXML
    private RadioButton maleBox;

    @FXML
    private DatePicker registerBirthdayInput;

    @FXML
    private Button registerButton;

    @FXML
    private TextField registerNameInput, registerTCInput, registerTelNo;

    @FXML
    private PasswordField registerPasswordAgainInput, registerPasswordInput;
    @FXML
    private ImageView backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // radio button settings
        maleBox.setOnMouseClicked(mouseEvent -> {
            if(maleBox.isSelected())
                femaleBox.setSelected(false);
        });

        femaleBox.setOnMouseClicked(mouseEvent -> {
            if(femaleBox.isSelected())
                maleBox.setSelected(false);
        });
        //**********************



        registerButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                boolean nameControl = !registerNameInput.getText().isEmpty();
                boolean TcControl = !registerTCInput.getText().isEmpty();
                boolean passwordsControl = !registerPasswordInput.getText().isEmpty() && !registerPasswordAgainInput.getText().isEmpty();
                boolean telControl = !registerTelNo.getText().isEmpty();
                boolean dateControl = false;
                if(registerBirthdayInput.getValue() != null)
                    dateControl = !registerBirthdayInput.getValue().toString().isEmpty();
                boolean boxesControl = maleBox.isSelected() || femaleBox.isSelected();

                if(nameControl && TcControl && passwordsControl && telControl && dateControl && boxesControl){
                    // register user
                    // kullanicinin verileri
                    String TC = registerTCInput.getText();
                    String name = registerNameInput.getText();
                    String password = registerPasswordInput.getText();
                    String number = registerTelNo.getText();
                    String date = registerBirthdayInput.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    String gender = null;
                    if(maleBox.isSelected())
                        gender = "Erkek";
                    else
                        gender = "Kadın";

                    // numeric oldugunu kontrol
                    if(isNumeric(TC) && isNumeric(number)){
                        // uzunluk kontrol
                        if(TC.length() == 11 && (number.length() == 11 || number.length() == 10)){
                            // bu veriler ile kullaniciyi kaydet
                            registerUser(TC, name, password, number, date, gender);
                        }
                        else{
                            Alert alert = new Alert(Alert.AlertType.WARNING);
                            alert.setHeaderText("Kayıt Olma İşlemi Başarısız!");
                            alert.setContentText("Lütfen bilgileri doğru girdiğinizden emin olunuz!");
                            alert.show();
                        }
                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setHeaderText("Kayıt Olma İşlemi Başarısız!");
                        alert.setContentText("Lütfen bilgileri doğru girdiğinizden emin olunuz!");
                        alert.show();
                    }

                }
                else{
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText("Kayıt Olma İşlemi Başarısız!");
                    alert.setContentText("Lütfen tüm alanların dolu olduğundan emin olunuz!");
                    alert.show();
                }
            }
        });

        backButton.setOnMouseClicked(mouseEvent -> {
            // giris yap sayfasina geri don
            openPage("login_page.fxml");
        });
    }

    private void registerUser(String tc, String name, String password, String number, String date, String gender) {
        try {
            String sqlQuery = "INSERT INTO kullanicilar (name, tc, sifre, tel_no, dogum_tarihi, cinsiyet, durum, asi_durum) VALUES ('"
                    + name + "', '" + tc + "', '" + password + "', '"
                     +number + "', '" + date + "', '" + gender + "', 'online', 'asi yapilmadi');";

            statement.execute(sqlQuery);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Kayıt Tamamlandı!");
            alert.setContentText("Kaydınız başarıyla oluşturuldu, ana ekrana yönlendirileceksiniz!");
            alert.show();

            // diger kullanicilari offline yap
            String resetQuery = "UPDATE kullanicilar SET durum='offline'";
            Statement statement1 = connection.getStatement();
            statement1.execute(resetQuery);

            openPage("home_page.fxml");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void openPage(String fxml){
        FXMLLoader FXML = new FXMLLoader(getClass().getResource(fxml));
        try {
            // show register page
            Stage stage = (Stage) femaleBox.getScene().getWindow();
            Scene scene = new Scene(FXML.load(), 1345, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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















