package com.example.layananbaak.models;

import java.util.Date;

public class PindahKelas {
    public String key;

    public String uid;
    public String kelas_sebelumnya;
    public String alasan_pindah;
    public String file_pindahkelas;
    public String file_persyaratan;
    public Date tanggal;

    public void setKey(String key) {
        this.key = key;
    }
    public PindahKelas() {}
    public PindahKelas(String file_pindahkelas) {this.file_pindahkelas = file_pindahkelas;}
    public PindahKelas(String uid,
                       String kelas_sebelumnya,
                       String alasan_pindah,
                       String file_persyaratan) {
        this.uid = uid;
        this.kelas_sebelumnya = kelas_sebelumnya;
        this.alasan_pindah = alasan_pindah;
        this.file_persyaratan = file_persyaratan;
    }
    public PindahKelas(String uid,
                       String kelas_sebelumnya,
                       String alasan_pindah,
                       String file_persyaratan,
                       Date tanggal) {
        this.uid = uid;
        this.kelas_sebelumnya = kelas_sebelumnya;
        this.alasan_pindah = alasan_pindah;
        this.file_persyaratan = file_persyaratan;
        this.tanggal = tanggal;
    }
}
