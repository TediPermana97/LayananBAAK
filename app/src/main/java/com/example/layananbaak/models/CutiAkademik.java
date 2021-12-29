package com.example.layananbaak.models;

import java.util.Date;

public class CutiAkademik {
    public String key;

    public String uid;
    public String semester;
    public String tahun_akademik;
    public String alasan_cuti;
    public String file_cutiakademik;
    public String file_persyaratan;
    public Date tanggal;

    public void setKey(String key) {
        this.key = key;
    }
    public CutiAkademik() {}
    public CutiAkademik(String file_cutiakademik) {
        this.file_cutiakademik = file_cutiakademik;
    }
    public CutiAkademik(String uid,
                        String semester,
                        String tahun_akademik,
                        String alasan_cuti,
                        String file_persyaratan) {
        this.uid = uid;
        this.semester = semester;
        this.tahun_akademik = tahun_akademik;
        this.alasan_cuti = alasan_cuti;
        this.file_persyaratan = file_persyaratan;
    }
    public CutiAkademik(String uid,
                        String semester,
                        String tahun_akademik,
                        String alasan_cuti,
                        String file_persyaratan,
                        Date tanggal) {
        this.uid = uid;
        this.semester = semester;
        this.tahun_akademik = tahun_akademik;
        this.alasan_cuti = alasan_cuti;
        this.file_persyaratan = file_persyaratan;
        this.tanggal = tanggal;
    }
}
