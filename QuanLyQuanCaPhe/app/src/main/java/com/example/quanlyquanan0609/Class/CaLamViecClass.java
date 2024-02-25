package com.example.quanlyquanan0609.Class;

import android.content.Intent;

public class CaLamViecClass {
    Integer ca_Ma, ca_SoLuong;
    String ca_Ten, ca_GioDB, ca_GioKT;

    public CaLamViecClass(Integer ca_Ma, Integer ca_SoLuong, String ca_Ten, String ca_GioDB, String ca_GioKT) {
        this.ca_Ma = ca_Ma;
        this.ca_SoLuong = ca_SoLuong;
        this.ca_Ten = ca_Ten;
        this.ca_GioDB = ca_GioDB;
        this.ca_GioKT = ca_GioKT;
    }
    public CaLamViecClass(){}
    public Integer getCa_SoLuong() {
        return ca_SoLuong;
    }

    public void setCa_SoLuong(Integer ca_SoLuong) {
        this.ca_SoLuong = ca_SoLuong;
    }

    public Integer getCa_Ma() {
        return ca_Ma;
    }

    public void setCa_Ma(Integer ca_Ma) {
        this.ca_Ma = ca_Ma;
    }

    public String getCa_Ten() {
        return ca_Ten;
    }

    public void setCa_Ten(String ca_Ten) {
        this.ca_Ten = ca_Ten;
    }

    public String getCa_GioDB() {
        return ca_GioDB;
    }

    public void setCa_GioDB(String ca_GioDB) {
        this.ca_GioDB = ca_GioDB;
    }

    public String getCa_GioKT() {
        return ca_GioKT;
    }

    public void setCa_GioKT(String ca_GioKT) {
        this.ca_GioKT = ca_GioKT;
    }
}
