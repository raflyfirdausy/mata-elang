package com.firdausy.rafly.mataelang.Model;

public class BayiModel {
    private String jenisKelamin;
    private String namaLengkapBayi;
    private String tanggalLahir;
    private String tempatLahir;
    private String keyBayi;
    private String keyIbu;

    public BayiModel(){}

    public BayiModel(String jenisKelamin, String namaLengkapBayi, String tanggalLahir, String tempatLahir, String keyBayi, String keyIbu) {
        this.jenisKelamin = jenisKelamin;
        this.namaLengkapBayi = namaLengkapBayi;
        this.tanggalLahir = tanggalLahir;
        this.tempatLahir = tempatLahir;
        this.keyBayi = keyBayi;
        this.keyIbu = keyIbu;
    }

    public String getJenisKelamin() {
        return jenisKelamin;
    }

    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }

    public String getNamaLengkapBayi() {
        return namaLengkapBayi;
    }

    public void setNamaLengkapBayi(String namaLengkapBayi) {
        this.namaLengkapBayi = namaLengkapBayi;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getTempatLahir() {
        return tempatLahir;
    }

    public void setTempatLahir(String tempatLahir) {
        this.tempatLahir = tempatLahir;
    }

    public String getKeyBayi() {
        return keyBayi;
    }

    public void setKeyBayi(String keyBayi) {
        this.keyBayi = keyBayi;
    }

    public String getKeyIbu() {
        return keyIbu;
    }

    public void setKeyIbu(String keyIbu) {
        this.keyIbu = keyIbu;
    }
}
