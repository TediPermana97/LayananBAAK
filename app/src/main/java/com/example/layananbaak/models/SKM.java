package com.example.layananbaak.models;

import java.util.Date;

public class SKM {
    public String key;

    public String uid;
    public String nik_ortu;
    public String nama_ortu;
    public String pekerjaan_ortu;
    public String keperluan_surat;
    public String file_skm;
    public String file_persyaratan;
    public Date tanggal;

    public void setKey(String key) {
        this.key = key;
    }
    public SKM() {}
    public SKM(String file_skm) {
        this.file_skm = file_skm;
    }
    public SKM(String uid,
               String nik_ortu,
               String nama_ortu,
               String pekerjaan_ortu,
               String keperluan_surat,
               String file_persyaratan) {
        this.uid = uid;
        this.nik_ortu = nik_ortu;
        this.nama_ortu = nama_ortu;
        this.pekerjaan_ortu = pekerjaan_ortu;
        this.keperluan_surat = keperluan_surat;
        this.file_persyaratan = file_persyaratan;
    }
    public SKM(String uid,
               String nik_ortu,
               String nama_ortu,
               String pekerjaan_ortu,
               String keperluan_surat,
               String file_persyaratan,
               Date tanggal) {
        this.uid = uid;
        this.nik_ortu = nik_ortu;
        this.nama_ortu = nama_ortu;
        this.pekerjaan_ortu = pekerjaan_ortu;
        this.keperluan_surat = keperluan_surat;
        this.file_persyaratan = file_persyaratan;
        this.tanggal = tanggal;
    }
}
