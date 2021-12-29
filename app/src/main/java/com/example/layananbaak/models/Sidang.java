package com.example.layananbaak.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Sidang {
    public String key;

    public String uid;
    public String judul;
    public String pembimbing;
    public String penguji1;
    public String penguji2;
    public String pembimbing_nidn;
    public String bimbingan_nosk;
    public String bimbingan_tanggal_sk;
    public String sidang_tanggal_acc;
    public String bimbingan_isi;
    public String file_sidang;
    public Date tanggal;


    public void setKey(String key) {
        this.key = key;
    }
    public Sidang() {}
    public Sidang(String file_sidang) {
        this.file_sidang = file_sidang;
    }
    public Sidang(String uid,
                  String judul,
                  String pembimbing,
                  String penguji1,
                  String penguji2,
                  String pembimbing_nidn,
                  String bimbingan_nosk,
                  String bimbingan_tanggal_sk,
                  String sidang_tanggal_acc,
                  String bimbingan_isi) {

        this.uid = uid;
        this.judul = judul;
        this.pembimbing = pembimbing;
        this.penguji1 = penguji1;
        this.penguji2 = penguji2;
        this.pembimbing_nidn = pembimbing_nidn;
        this.bimbingan_nosk = bimbingan_nosk;
        this.bimbingan_tanggal_sk = bimbingan_tanggal_sk;
        this.sidang_tanggal_acc = sidang_tanggal_acc;
        this.bimbingan_isi = bimbingan_isi;
    }
    public Sidang(String uid,
                  String judul,
                  String pembimbing,
                  String penguji1,
                  String penguji2,
                  String pembimbing_nidn,
                  String bimbingan_nosk,
                  String bimbingan_tanggal_sk,
                  String sidang_tanggal_acc,
                  String bimbingan_isi,
                  Date tanggal) {

        this.uid = uid;
        this.judul = judul;
        this.pembimbing = pembimbing;
        this.penguji1 = penguji1;
        this.penguji2 = penguji2;
        this.pembimbing_nidn = pembimbing_nidn;
        this.bimbingan_nosk = bimbingan_nosk;
        this.bimbingan_tanggal_sk = bimbingan_tanggal_sk;
        this.sidang_tanggal_acc = sidang_tanggal_acc;
        this.bimbingan_isi = bimbingan_isi;
        this.tanggal = tanggal;
    }
}
