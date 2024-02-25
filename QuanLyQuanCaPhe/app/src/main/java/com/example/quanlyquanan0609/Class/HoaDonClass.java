package com.example.quanlyquanan0609.Class;

public class HoaDonClass {
    Integer hd_Ma, hd_TongTien, pgm_Ma;
    String hd_Ngay, hd_GioVao, hd_GioRa, hd_HinhThuc, hd_TrangThai;

    public HoaDonClass(Integer hd_Ma, Integer pgm_Ma, String hd_Ngay, String hd_GioVao, String hd_GioRa, Integer hd_TongTien, String hd_HinhThuc, String hd_TrangThai) {
        this.hd_Ma = hd_Ma;
        this.pgm_Ma = pgm_Ma;
        this.hd_Ngay = hd_Ngay;
        this.hd_GioVao = hd_GioVao;
        this.hd_GioRa = hd_GioRa;
        this.hd_TongTien = hd_TongTien;
        this.hd_HinhThuc = hd_HinhThuc;
        this.hd_TrangThai = hd_TrangThai;
    }
    public HoaDonClass(){}

    public Integer getHd_Ma() {
        return hd_Ma;
    }

    public void setHd_Ma(Integer hd_Ma) {
        this.hd_Ma = hd_Ma;
    }

    public Integer getPgm_Ma() {
        return pgm_Ma;
    }

    public void setPgm_Ma(Integer pgm_Ma) {
        this.pgm_Ma = pgm_Ma;
    }

    public String getHd_Ngay() {
        return hd_Ngay;
    }

    public void setHd_Ngay(String hd_Ngay) {
        this.hd_Ngay = hd_Ngay;
    }

    public String getHd_GioVao() {
        return hd_GioVao;
    }

    public void setHd_GioVao(String hd_GioVao) {
        this.hd_GioVao = hd_GioVao;
    }

    public String getHd_GioRa() {
        return hd_GioRa;
    }

    public void setHd_GioRa(String hd_GioRa) {
        this.hd_GioRa = hd_GioRa;
    }

    public String getHd_HinhThuc() {
        return hd_HinhThuc;
    }

    public void setHd_HinhThuc(String hd_HinhThuc) {
        this.hd_HinhThuc = hd_HinhThuc;
    }

    public String getHd_TrangThai() {
        return hd_TrangThai;
    }

    public void setHd_TrangThai(String hd_TrangThai) {
        this.hd_TrangThai = hd_TrangThai;
    }

    public Integer getHd_TongTien() {
        return hd_TongTien;
    }

    public void setHd_TongTien(Integer hd_TongTien) {
        this.hd_TongTien = hd_TongTien;
    }
}
