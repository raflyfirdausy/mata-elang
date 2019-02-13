package com.firdausy.rafly.mataelang.Model;

public class IbuModel {
    private String alamatLengkap;
    private String email;
    private String namaLengkap;
    private String nomerHp;
    private String keyIbu;

    public IbuModel(){}

    public IbuModel(String alamatLengkap, String email, String namaLengkap, String nomerHp, String keyIbu) {
        this.alamatLengkap = alamatLengkap;
        this.email = email;
        this.namaLengkap = namaLengkap;
        this.nomerHp = nomerHp;
        this.keyIbu = keyIbu;
    }

    public String getAlamatLengkap() {
        return alamatLengkap;
    }

    public void setAlamatLengkap(String alamatLengkap) {
        this.alamatLengkap = alamatLengkap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNamaLengkap() {
        return namaLengkap;
    }

    public void setNamaLengkap(String namaLengkap) {
        this.namaLengkap = namaLengkap;
    }

    public String getNomerHp() {
        return nomerHp;
    }

    public void setNomerHp(String nomerHp) {
        this.nomerHp = nomerHp;
    }

    public String getKeyIbu() {
        return keyIbu;
    }

    public void setKeyIbu(String keyIbu) {
        this.keyIbu = keyIbu;
    }
}
