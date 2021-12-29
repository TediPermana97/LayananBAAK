package com.example.layananbaak.models;

import java.util.Date;

public class SIKP {
    public String key;

    public String uid;
    public String ditujukan_kepada;
    public String istansi_perusahaan;
    public String alamat_istansi;
    public String alamat_istansi_kota;
    public String alamat_istansi_kodepos;
    public String alamat_istansi_provinsi;
    public String alamat_istansi_negara;
    public String tanggal_mulai;
    public String tanggal_akhir;
    public String keterangan;
    public String verifikasi_kode;
    public String file_sikp;
    public String file_persyaratan;
    public Date tanggal;


    public void setKey(String key) {
        this.key = key;
    }
    public SIKP() {}
    public SIKP(String file_sikp) {
        this.file_sikp = file_sikp;
    }
    public SIKP(String uid,
                String ditujukan_kepada,
                String istansi_perusahaan,
                String alamat_istansi,
                String alamat_istansi_kota,
                String alamat_istansi_kodepos,
                String alamat_istansi_provinsi,
                String alamat_istansi_negara,
                String tanggal_mulai,
                String tanggal_akhir,
                String keterangan,
                String file_persyaratan,
                String verifikasi_kode) {

        this.uid = uid;
        this.ditujukan_kepada = ditujukan_kepada;
        this.istansi_perusahaan = istansi_perusahaan;
        this.alamat_istansi = alamat_istansi;
        this.alamat_istansi_kota = alamat_istansi_kota;
        this.alamat_istansi_kodepos = alamat_istansi_kodepos;
        this.alamat_istansi_provinsi = alamat_istansi_provinsi;
        this.alamat_istansi_negara = alamat_istansi_negara;
        this.tanggal_mulai = tanggal_mulai;
        this.tanggal_akhir = tanggal_akhir;
        this.keterangan = keterangan;
        this.file_persyaratan = file_persyaratan;
        this.verifikasi_kode = verifikasi_kode;
    }
    public SIKP(String uid,
                String ditujukan_kepada,
                String istansi_perusahaan,
                String alamat_istansi,
                String alamat_istansi_kota,
                String alamat_istansi_kodepos,
                String alamat_istansi_provinsi,
                String alamat_istansi_negara,
                String tanggal_mulai,
                String tanggal_akhir,
                String keterangan,
                String file_persyaratan,
                String verifikasi_kode,
                Date tanggal) {

        this.uid = uid;
        this.ditujukan_kepada = ditujukan_kepada;
        this.istansi_perusahaan = istansi_perusahaan;
        this.alamat_istansi = alamat_istansi;
        this.alamat_istansi_kota = alamat_istansi_kota;
        this.alamat_istansi_kodepos = alamat_istansi_kodepos;
        this.alamat_istansi_provinsi = alamat_istansi_provinsi;
        this.alamat_istansi_negara = alamat_istansi_negara;
        this.tanggal_mulai = tanggal_mulai;
        this.tanggal_akhir = tanggal_akhir;
        this.keterangan = keterangan;
        this.file_persyaratan = file_persyaratan;
        this.verifikasi_kode = verifikasi_kode;
        this.tanggal = tanggal;
    }
}
