package com.example.layananbaak.models;

import java.util.Date;

public class SIP {
    public String key;

    public String uid;
    public String ditujukan_kepada;
    public String istansi_perusahaan;
    public String alamat_istansi;
    public String tanggal_mulai_penelitian;
    public String tanggal_akhir_penelitian;
    public String judul_penelitian;
    public String file_sip;
    public String file_persyaratan;
    public Date tanggal;


    public void setKey(String key) {
        this.key = key;
    }
    public SIP() {}
    public SIP(String file_sip) {
        this.file_sip = file_sip;
    }
    public SIP(String uid,
               String ditujukan_kepada,
               String istansi_perusahaan,
               String alamat_istansi,
               String tanggal_mulai_penelitian,
               String tanggal_akhir_penelitian,
               String judul_penelitian,
               String file_persyaratan) {

        this.uid = uid;
        this.ditujukan_kepada = ditujukan_kepada;
        this.istansi_perusahaan = istansi_perusahaan;
        this.alamat_istansi = alamat_istansi;
        this.tanggal_mulai_penelitian = tanggal_mulai_penelitian;
        this.tanggal_akhir_penelitian = tanggal_akhir_penelitian;
        this.judul_penelitian = judul_penelitian;
        this.file_persyaratan = file_persyaratan;
    }
    public SIP(String uid,
               String ditujukan_kepada,
               String istansi_perusahaan,
               String alamat_istansi,
               String tanggal_mulai_penelitian,
               String tanggal_akhir_penelitian,
               String judul_penelitian,
               String file_persyaratan,
               Date tanggal) {

        this.uid = uid;
        this.ditujukan_kepada = ditujukan_kepada;
        this.istansi_perusahaan = istansi_perusahaan;
        this.alamat_istansi = alamat_istansi;
        this.tanggal_mulai_penelitian = tanggal_mulai_penelitian;
        this.tanggal_akhir_penelitian = tanggal_akhir_penelitian;
        this.judul_penelitian = judul_penelitian;
        this.file_persyaratan = file_persyaratan;
        this.tanggal = tanggal;
    }
}
