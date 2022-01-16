package com.example.asitakipprogrami;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class RandevuAlFrame implements Initializable {

    @FXML
    private ToggleButton biontechToggle, sinovacToggle, turkovacToggle;
    private ToggleButton selectedButton = new ToggleButton();
    @FXML
    private ChoiceBox<String> birimler, hastaneler, odalar, saatler, tarihler;
    @FXML
    private Pane hastanePane;
    @FXML
    private AnchorPane mainPane;
    @FXML
    private ListView<String> hospitalListview;
    @FXML
    private TextField searchTextField;
    @FXML
    private Button randevuAlButton;

    private final List<String> hospitalList = new ArrayList<>();

    // database variables
    DatabaseConnection connection = new DatabaseConnection();
    Statement statement = connection.getStatement();

    private EskiRandevuItem item = new EskiRandevuItem();
    DraggableMaker draggableMaker = new DraggableMaker();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // set default page design
        setDefaultPage();

        // hastane mi saglik ocagi mi?
        setBirimlerItems();
        // asi odalari item duzenle
        setOdalarItems();
        // odalardaki tarihleri duzenle
        setTarihlerItems();
        // tarihlerdeki saatler duzenle
        setSaatlerItems();
        // hastaneleri listeye ekle
        setHastanelerItems();

        draggableMaker.makeDraggable(hastanePane);

        biontechToggle.setOnMouseClicked(mouseEvent -> {
            // biontech secildi
            biontechToggle.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            sinovacToggle.setStyle("-fx-background-color: #DEDEDE; -fx-text-fill: black;");
            turkovacToggle.setStyle("-fx-background-color: #DEDEDE; -fx-text-fill: black;");
            selectedButton = biontechToggle;

            // hastane ya da saglik ocagi secimini aktif et
            birimler.setDisable(false);
        });

        sinovacToggle.setOnMouseClicked(mouseEvent -> {
            // sinovac secildi
            sinovacToggle.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            biontechToggle.setStyle("-fx-background-color: #DEDEDE; -fx-text-fill: black;");
            turkovacToggle.setStyle("-fx-background-color: #DEDEDE; -fx-text-fill: black;");
            selectedButton = sinovacToggle;

            // hastane ya da saglik ocagi secimini aktif et
            birimler.setDisable(false);
        });

        turkovacToggle.setOnMouseClicked(mouseEvent -> {
            // sinovac secildi
            sinovacToggle.setStyle("-fx-background-color: #DEDEDE; -fx-text-fill: black;");
            biontechToggle.setStyle("-fx-background-color: #DEDEDE; -fx-text-fill: black;");
            turkovacToggle.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            selectedButton = turkovacToggle;

            // hastane ya da saglik ocagi secimini aktif et
            birimler.setDisable(false);
        });

        hastaneler.setOnMouseClicked(mouseEvent -> {
            // hastane pane goster
            hastanePane.setVisible(true);
        });

        searchTextField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                // hastane ara
                hospitalListview.getItems().clear();
                hospitalListview.getItems().addAll(searchList(t1, hospitalList));
            }
        });

        // randevu al buton click
        randevuAlButton.setOnMouseClicked(mouseEvent -> {

            System.out.println(getUserVaccineValue());

            if(item.getAktifRandevuList().size() > 0){
                // eger aktif bir randevusu varsa randevu verme
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Randevu Kaydı Oluşturulamadı!");
                alert.setContentText("Zaten aktif bir aşı randevunuz bulunuyor. Bu durumda yeni bir randevu alamazsınız...");
                alert.show();
            }
            else if(getUserVaccineValue() >= 3){
                // aşı tamamlanmıştır yeni aşı alamaz
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Randevu Kaydı Oluşturulamadı!");
                alert.setContentText("Saygıdeğer vatandaşımız, aşı süreciniz tamamlanmıştır yeni bir randevu alınamıyor...");
                alert.show();
            }
            else if(vaccineDateControl()){
                // aradan 1 ay geçmesi gerekiyor yeni bir randevu için verme randevu
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("Randevu Kaydı Oluşturulamadı!");
                alert.setContentText("1 ay içine aşı olmuşsunuz, yeni bir aşı olabilmeniz için 1 ay geçmesi gerekiyor...");
                alert.show();
            }
            else{
                // randevu ver, verileri cekelim
                String hastaAd = "";
                try {
                    ResultSet resultSet = statement.executeQuery("SELECT name FROM kullanicilar WHERE durum='online'");
                    while (resultSet.next()){
                        hastaAd = resultSet.getString("name");
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                String asiTipi = selectedButton.getText();
                String birim = birimler.getSelectionModel().getSelectedItem();
                String hastaneAd = hastaneler.getSelectionModel().getSelectedItem();
                String asiOdasi = odalar.getSelectionModel().getSelectedItem();
                String tarih = tarihler.getSelectionModel().getSelectedItem();
                String saat = saatler.getSelectionModel().getSelectedItem();

                // itemlerin bos olma durumu kontrol
                boolean control = !asiTipi.isEmpty() && !birim.isEmpty() && !asiOdasi.isEmpty() && !tarih.isEmpty() && !saat.isEmpty();

                if(control)
                    randevuKaydet(hastaAd, asiTipi, birim, hastaneAd, asiOdasi, tarih, saat);
                else{
                    // alert dialog olustur
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Randevu Kaydı Oluşturulamadı!");
                    alert.setContentText("Lütfen tüm alanları doldurduğunuzdan emin olunuz!");
                    alert.show();
                }
            }
        });
    }

    private void randevuKaydet(String hastaAd, String asiTipi, String birim, String hastaneAd, String asiOdasi, String tarih, String saat) {
        try {
            // randevu bilgilerinini veritabanına kaydet
            String query = "INSERT INTO randevular (tip, hasta_ad, asi_tipi, birim, hastane_ad, asi_odasi, tarih, saat) VALUES ('aktif', '" + hastaAd + "', '" + asiTipi + "', '" + birim + "', '" + hastaneAd + "', '" + asiOdasi + "', '" + tarih + "', '" + saat  + "');";
            statement.execute(query);

            // alert dialog olustur
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Randevu Kaydı");
            alert.setContentText("Randevu kaydınız başarıyla oluşturuldu!");
            alert.show();

            openPage("home_page.fxml");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultPage() {
        hastanePane.setVisible(false);

        // bazi choice box devre disi birak simdilik
        birimler.setDisable(true);
        hastaneler.setDisable(true);
        odalar.setDisable(true);
        saatler.setDisable(true);
        tarihler.setDisable(true);
    }

    private void setBirimlerItems(){
        String[] birimStringArray = {"Hastane", "Aile Sağlığı Merkezi"};
        birimler.getItems().addAll(birimStringArray);

        birimler.setOnAction(event -> {
            // oda secimini aktif et
            odalar.setDisable(false);
            // eger saglik ocagi secilirse hastane secmeye gerek yok
            if(birimler.getSelectionModel().getSelectedItem().equals("Aile Sağlığı Merkezi"))
                hastaneler.setDisable(true);
            else
                hastaneler.setDisable(false);
        });
    }

    private void setOdalarItems(){
        String[] odalarStringArray = {"Aşı Odası 1", "Aşı Odası 2", "Aşı Odası 3", "Aşı Odası 4", "Aşı Odası 5"};
        odalar.getItems().addAll(odalarStringArray);

        odalar.setOnAction(event -> {
            tarihler.setDisable(false);
        });
    }

    private void setTarihlerItems(){
        List<String> tarihList = new ArrayList<>();
        for(int i=0 ; i<5 ; i++){
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            String tarih = sdf.format(calendar.getTime());
            try {
                calendar.setTime(sdf.parse(tarih));
                calendar.add(Calendar.DATE, i+1);
                tarihList.add(sdf.format(calendar.getTime()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        tarihler.getItems().addAll(tarihList);

        tarihler.setOnAction(event -> {
            saatler.setDisable(false);
        });
    }

    private void setSaatlerItems(){
        String[] saatlerStringArray = {"8.00", "8.15", "8.30", "8.45", "9.00", "9.15", "9.30", "9.45", "10.00",
                                        "10.15", "10.30", "10.45", "11.00", "11.15", "11.30", "11.45", "12.00",
                                        "12.15", "12.30", "12.45", "13.00"};

        saatler.getItems().addAll(saatlerStringArray);
    }

    private void setHastanelerItems(){
        // hastaneleri listenin icine atiyoruz
        setHospitalListItems();

        hospitalListview.getItems().addAll(hospitalList);

        hospitalListview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                // hastane secildiginde
                String hospital = hospitalListview.getSelectionModel().getSelectedItem();
                hastanePane.setVisible(false);
                hastaneler.setValue(hospital);
            }
        });
    }

    private List<String> searchList(String searchWords, List<String> listOfStrings) {

        List<String> searchWordsArray = Arrays.asList(searchWords.trim().split(" "));

        return listOfStrings.stream().filter(input -> {
            return searchWordsArray.stream().allMatch(word ->
                    input.toLowerCase().contains(word.toLowerCase()));
        }).collect(Collectors.toList());
    }

    private void setHospitalListItems(){

        try {

            ResultSet result = statement.executeQuery("SELECT * FROM hastaneler");
            while(result.next()){
                hospitalList.add(result.getString("ad"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int getUserVaccineValue() {
        String hastaAd = "";
        Statement statement1 = connection.getStatement();

        int value = 0;
        Statement statement = connection.getStatement();
        try {
            // hast adini cektik
            ResultSet resultSet1 = statement1.executeQuery("SELECT name FROM kullanicilar WHERE durum='online'");
            while(resultSet1.next()){
                hastaAd = resultSet1.getString("name");
            }
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

    private boolean vaccineDateControl(){
        boolean returnValue = false;
        String hastaAd = "";
        ArrayList<LocalDate> vaccineDateList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        try {
            // hast adini cektik
            Statement statement1 = connection.getStatement();
            ResultSet resultSet1 = statement1.executeQuery("SELECT name FROM kullanicilar WHERE durum='online'");
            while(resultSet1.next()){
                hastaAd = resultSet1.getString("name");
            }

            Statement statement = connection.getStatement();
            ResultSet resultSet = statement.executeQuery("SELECT tarih FROM randevular WHERE hasta_ad='" + hastaAd + "' AND tip='gecmis'");
            while (resultSet.next()){
                String dateString = resultSet.getString("tarih");
                vaccineDateList.add(LocalDate.parse(dateString, formatter));
            }

            LocalDate date = LocalDate.parse(tarihler.getSelectionModel().getSelectedItem(), formatter);

            for(int i=0 ; i<vaccineDateList.size() ; i++){
                Period diff = Period.between(date, vaccineDateList.get(i));
                System.out.println("Yıl:" + diff.getYears());
                System.out.println("Ay:" + diff.getMonths());
                System.out.println("Gün:" + diff.getDays());
                if(Math.abs(diff.getMonths()) >= 1 || Math.abs(diff.getYears()) >= 1){
                    // eger en az 1 ay geçmediyse randevu verilmeyecek
                    returnValue = false;
                }
                else{
                    returnValue = true;
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    private void openPage(String fxml){
        FXMLLoader FXML = new FXMLLoader(getClass().getResource(fxml));
        try {
            // aynı stage üzerine farklı bir fxml file actik
            Stage stage = (Stage) sinovacToggle.getScene().getWindow();
            Scene scene = new Scene(FXML.load(), 1345, 720);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}





















