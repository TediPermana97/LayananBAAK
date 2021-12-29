package com.example.layananbaak.models;

import java.util.Date;

public class STLS {
    public String key;

    public String uid;
    public String tahun_akademik;
    public String verifikasi_kode;
    public String file_stls;
    public String file_persyaratan;
    public Date tanggal;


    public void setKey(String key) {
        this.key = key;
    }
    public STLS() {}
    public STLS(String file_stls) {
        this.file_stls = file_stls;
    }
    public STLS(String uid,
                String tahun_akademik,
                String file_persyaratan,
                String verifikasi_kode) {

        this.uid = uid;
        this.tahun_akademik = tahun_akademik;
        this.file_persyaratan = file_persyaratan;
        this.verifikasi_kode = verifikasi_kode;
    }
    public STLS(String uid,
                String tahun_akademik,
                String file_persyaratan,
                String verifikasi_kode,
                Date tanggal) {

        this.uid = uid;
        this.tahun_akademik = tahun_akademik;
        this.file_persyaratan = file_persyaratan;
        this.verifikasi_kode = verifikasi_kode;
        this.tanggal = tanggal;
    }
}
