package com.bezkoder.spring.files.csv.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "nasabah_bareksa_kyc")
public class DataKyc {


    @Column(name = "TGL_TRX")
    private String tglTransaksi;

    @Column(name = "NO_KONTRAK")
    private String noKontrak;

    @Id
    @Column(name = "NO_KTP")
    private String noKtp;

    @Column(name = "CIF")
    private String cif;

    @Column(name = "CLIENT_ID")
    private int clientId;

    @Column(name = "KODE_PRODUK")
    private int kodeProduk;

    @Column(name = "FILE_PATH_KTP")
    private String pathKtp;

    @Column(name = "FILE_PATH_SELFIE")
    private String pathSelfie;

    private String tanggalAwal;

    private String tanggalAkhir;

    private boolean foto;

    public DataKyc(){}

    public DataKyc(String tglTransaksi, String noKontrak, String noKtp, String cif, int clientId, int kodeProduk, String pathKtp, String pathSelfie) {
        this.tglTransaksi = tglTransaksi;
        this.noKontrak = noKontrak;
        this.noKtp = noKtp;
        this.cif = cif;
        this.clientId = clientId;
        this.kodeProduk = kodeProduk;
        this.pathKtp = pathKtp;
        this.pathSelfie = pathSelfie;
    }

    public String getTglTransaksi() {
        return tglTransaksi;
    }

    public void setTglTransaksi(String tglTransaksi) {
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

    public String getPathKtp() {
        return pathKtp;
    }

    public void setPathKtp(String pathKtp) { this.pathKtp = pathKtp; }

    public String getPathSelfie() {
        return pathSelfie;
    }

    public void setPathSelfie(String pathSelfie) {
        this.pathSelfie = pathSelfie;
    }

    public String getTanggalAwal() {
        return tanggalAwal;
    }

    public void setTanggalAwal(String tanggalAwal) {
        this.tanggalAwal = tanggalAwal;
    }

    public String getTanggalAkhir() {
        return tanggalAkhir;
    }

    public void setTanggalAkhir(String tanggalAkhir) {
        this.tanggalAkhir = tanggalAkhir;
    }

    public boolean isFoto() {
        return foto;
    }

    public void setFoto(boolean foto) {
        this.foto = foto;
    }
}
