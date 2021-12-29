package com.example.layananbaak.models;


public class Profile {
    public String foto;
    public String npm;
    public String nama;
    public String email;
    public String nomor_telp;
    public String jenis_kelamin;
    public String alamat;
    public String tempat_lahir;
    public String tanggal_lahir;
    public String key;

    public void setKey(String key) {
        this.key = key;
    }

    public Profile(){}

    public Profile(String foto){
        this.foto = foto;
    }

    public Profile(String foto, String npm){
        this.foto = foto;
        this.npm = npm;
    }

    public Profile(String foto,
                   String npm,
                   String nama,
                   String email,
                   String nomor_telp,
                   String jenis_kelamin,
                   String alamat,
                   String tempat_lahir,
                   String tanggal_lahir){
        this.foto = foto;
        this.npm = npm;
        this.nama = nama;
        this.email = email;
        this.nomor_telp = nomor_telp;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.tempat_lahir = tempat_lahir;
        this.tanggal_lahir = tanggal_lahir;
    }
}
