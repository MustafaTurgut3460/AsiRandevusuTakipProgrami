package com.example.asitakipprogrami;

public class ModelRandevu {

    private String hastaAd, asi, birim, hastaneAd, asiOdasi, tarih, saat, tip;

    public ModelRandevu(){

    }

    public ModelRandevu(String tip, String hastaAd, String asi, String birim, String hastaneAd, String asiOdasi, String tarih, String saat){
        this.hastaAd = hastaAd;
        this.asi = asi;
        this.birim = birim;
        this.hastaneAd = hastaneAd;
        this.asiOdasi = asiOdasi;
        this.tarih = tarih;
        this.saat = saat;
        this.tip = tip;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public void setHastaAd(String hastaAd) {
        this.hastaAd = hastaAd;
    }

    public String getHastaAd() {
        return hastaAd;
    }

    public void setAsi(String asi) {
        this.asi = asi;
    }

    public String getAsi() {
        return asi;
    }

    public void setBirim(String birim) {
        this.birim = birim;
    }

    public String getBirim() {
        return birim;
    }

    public void setHastaneAd(String hastaneAd) {
        this.hastaneAd = hastaneAd;
    }

    public String getHastaneAd() {
        return hastaneAd;
    }

    public void setAsiOdasi(String asiOdasi) {
        this.asiOdasi = asiOdasi;
    }

    public String getAsiOdasi() {
        return asiOdasi;
    }

    public void setTarih(String tarih) {
        this.tarih = tarih;
    }

    public String getTarih() {
        return tarih;
    }

    public void setSaat(String saat) {
        this.saat = saat;
    }

    public String getSaat() {
        return saat;
    }
}
