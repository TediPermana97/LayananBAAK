package com.example.layananbaak.models;

import java.util.Date;

public class AktifAkademik {
    public String key;

    public String uid;
    public String alasan_aktif;
    public String file_aktifakademik;
    public String file_persyaratan;
    public Date tanggal;

    public void setKey(String key) {
        this.key = key;
    }
    public AktifAkademik() {}
    public AktifAkademik(String file_aktifakademik) {
        this.file_aktifakademik = file_aktifakademik;
    }
    public AktifAkademik(String uid,
                         String alasan_aktif,
                         String file_persyaratan) {
        this.uid = uid;
        this.alasan_aktif = alasan_aktif;
        this.file_persyaratan = file_persyaratan;
    }
    public AktifAkademik(String uid,
                         String a,
                         String file_persyaratan,
                         Date tanggal) {
        this.uid = uid;
        this.alasan_aktif = alasan_aktif;
        this.file_persyaratan = file_persyaratan;
        this.tanggal = tanggal;
    }
}
