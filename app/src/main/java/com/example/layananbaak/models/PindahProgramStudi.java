package com.example.layananbaak.models;

import java.util.Date;

public class PindahProgramStudi {
    public String key;

    public String uid;
    public String pindahke_programstudi;
    public String alasan_pindah;
    public String file_pindahstudi;
    public String file_persyaratan;
    public Date tanggal;

    public void setKey(String key) {
        this.key = key;
    }
    public PindahProgramStudi() {}
    public PindahProgramStudi(String file_pindahstudi) {this.file_pindahstudi = file_pindahstudi;}
    public PindahProgramStudi(String uid,
                              String pindahke_programstudi,
                              String alasan_pindah,
                              String file_persyaratan) {
        this.uid = uid;
        this.pindahke_programstudi = pindahke_programstudi;
        this.alasan_pindah = alasan_pindah;
        this.file_persyaratan = file_persyaratan;
    }
    public PindahProgramStudi(String uid,
                              String pindahke_programstudi,
                              String alasan_pindah,
                              String file_persyaratan,
                              Date tanggal) {
        this.uid = uid;
        this.pindahke_programstudi = pindahke_programstudi;
        this.alasan_pindah = alasan_pindah;
        this.file_persyaratan = file_persyaratan;
        this.tanggal = tanggal;
    }
}
