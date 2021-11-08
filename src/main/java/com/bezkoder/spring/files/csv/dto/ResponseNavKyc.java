package com.bezkoder.spring.files.csv.dto;

import java.util.Date;

public class ResponseNavKyc {
    private Date tglTransaksi;
    private String noKontrak;
    private String noKtp;
    private String cif;
    private int clientId;
    private int kodeProduk;

    public Date getTglTransaksi() {
        return tglTransaksi;
    }

    public void setTglTransaksi(Date tglTransaksi) {
        this.tglTransaksi = tglTransaksi;
    }

    public String getNoKontrak() {
        return noKontrak;
    }

    public void setNoKontrak(String noKontrak) {
        this.noKontrak = noKontrak;
    }

    public String getNoKtp() {
        return noKtp;
    }

    public void setNoKtp(String noKtp) {
        this.noKtp = noKtp;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getKodeProduk() {
        return kodeProduk;
    }

    public void setKodeProduk(int kodeProduk) {
        this.kodeProduk = kodeProduk;
    }
}
