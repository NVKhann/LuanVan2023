package com.example.quanlyquanan0609.Class;

public class ChiTietPGMClass {
    Integer pgm_Ma, mon_Ma, ct_DonGia, ct_SoLuong;
    String ct_GhiChu;

    public ChiTietPGMClass(Integer pgm_Ma, Integer mon_Ma, Integer ct_DonGia, Integer ct_SoLuong, String ct_GhiChu) {
        this.pgm_Ma = pgm_Ma;
        this.mon_Ma = mon_Ma;
        this.ct_DonGia = ct_DonGia;
        this.ct_SoLuong = ct_SoLuong;
        this.ct_GhiChu = ct_GhiChu;
    }
    public ChiTietPGMClass(){}

    public Integer getPgm_Ma() {
        return pgm_Ma;
    }

    public void setPgm_Ma(Integer pgm_Ma) {
        this.pgm_Ma = pgm_Ma;
    }

    public Integer getMon_Ma() {
        return mon_Ma;
    }

    public void setMon_Ma(Integer mon_Ma) {
        this.mon_Ma = mon_Ma;
    }

    public Integer getCt_DonGia() {
        return ct_DonGia;
    }

    public void setCt_DonGia(Integer ct_DonGia) {
        this.ct_DonGia = ct_DonGia;
    }

    public Integer getCt_SoLuong() {
        return ct_SoLuong;
    }

    public void setCt_SoLuong(Integer ct_SoLuong) {
        this.ct_SoLuong = ct_SoLuong;
    }

    public String getCt_GhiChu() {
        return ct_GhiChu;
    }

    public void setCt_GhiChu(String ct_GhiChu) {
        this.ct_GhiChu = ct_GhiChu;
    }
}
