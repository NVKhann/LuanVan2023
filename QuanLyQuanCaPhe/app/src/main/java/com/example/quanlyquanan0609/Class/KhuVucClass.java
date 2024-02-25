package com.example.quanlyquanan0609.Class;

public class KhuVucClass {
    Integer kv_Ma;
    String kv_Ten, kv_TrangThai;

    public KhuVucClass(Integer kv_Ma, String kv_Ten, String kv_TrangThai) {
        this.kv_Ma = kv_Ma;
        this.kv_Ten = kv_Ten;
        this.kv_TrangThai = kv_TrangThai;
    }

    public  KhuVucClass(){}

    public Integer getKv_Ma() {
        return kv_Ma;
    }

    public void setKv_Ma(Integer kv_Ma) {
        this.kv_Ma = kv_Ma;
    }

    public String getKv_Ten() {
        return kv_Ten;
    }

    public void setKv_Ten(String kv_Ten) {
        this.kv_Ten = kv_Ten;
    }

    public String getKv_TrangThai() {
        return kv_TrangThai;
    }

    public void setKv_TrangThai(String kv_TrangThai) {
        this.kv_TrangThai = kv_TrangThai;
    }
}
