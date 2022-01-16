package com.example.asitakipprogrami;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EskiRandevuItem implements Initializable {
    @FXML
    private Label dateText, hospitalText, randevuTipText, vaccineRoomText, vaccineText;

    private static int count = 0;

    private final List<ModelRandevu> gecmisRandevuList = new ArrayList<>();
    private List<ModelRandevu> aktifRandevuList = new ArrayList<>();

    private DatabaseConnection connection = new DatabaseConnection();
    private Statement statement = connection.getStatement();

    private String username;

    public EskiRandevuItem(){
        try {
            ResultSet resultSet = statement.executeQuery("SELECT name FROM kullanicilar WHERE durum='online'");
            while (resultSet.next()){
                username = resultSet.getString("name");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        try {
            ResultSet resultSet = statement.executeQuery("SELECT * FROM randevular WHERE hasta_ad='" + username + "'");
            while (resultSet.next()){
                String tip = resultSet.getString("tip");
                String asi = resultSet.getString("asi_tipi");
                String birim = resultSet.getString("birim");
                String hastaneAd = resultSet.getString("hastane_ad");
                String asiOdasi = resultSet.getString("asi_odasi");
                String tarih = resultSet.getString("tarih");
                String saat = resultSet.getString("saat");

                // modelimizi olusturup icini dolduruyoruz
                ModelRandevu randevu = new ModelRandevu(tip, username, asi, birim, hastaneAd, asiOdasi, tarih, saat);

                // eger önceki tarih ise gecmis randevulara eklenecek
                if(!tip.equals("aktif")){
                    // listeye bu modeli ekliyoruz
                    gecmisRandevuList.add(randevu);
                }
                else{
                    // degilse aktif randevu cekilecek
                    aktifRandevuList.add(randevu);
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(count == gecmisRandevuList.size())
            count = 0;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        count++;

        // item elemanlarına verileri ekliyoruz
        dateText.setText(gecmisRandevuList.get(count-1).getTarih() + " " + gecmisRandevuList.get(count-1).getSaat());

        if(gecmisRandevuList.get(count-1).getBirim().equals("Hastane"))
            hospitalText.setText(gecmisRandevuList.get(count-1).getHastaneAd());
        else
            hospitalText.setText(gecmisRandevuList.get(count-1).getBirim());

        vaccineRoomText.setText(gecmisRandevuList.get(count-1).getAsiOdasi());
        vaccineText.setText("Covid-19 Aşı Randevusu (" + gecmisRandevuList.get(count-1).getAsi() + ")");

        if(gecmisRandevuList.get(count-1).getTip().equals("gecmis")){
            randevuTipText.setText("Geçmiş Randevu");
            randevuTipText.setStyle("-fx-text-fill: green;");
        }
        else{
            randevuTipText.setText("İptal Randevu");
            randevuTipText.setStyle("-fx-text-fill: red;");
        }
    }

    public List<ModelRandevu> getGecmisRandevuList() {
        return gecmisRandevuList;
    }

    public List<ModelRandevu> getAktifRandevuList() {
        return aktifRandevuList;
    }
}































