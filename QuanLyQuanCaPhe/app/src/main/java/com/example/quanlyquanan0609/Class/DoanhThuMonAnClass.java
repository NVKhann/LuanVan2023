package com.example.quanlyquanan0609.Class;

public class DoanhThuMonAnClass {
    public Integer maMon;
    public String ngay;
    public Double tongTien;

    public DoanhThuMonAnClass(Integer maMon, String ngay, Double tongTien) {
        this.maMon = maMon;
        this.ngay = ngay;
        this.tongTien = tongTien;
    }

    public DoanhThuMonAnClass(){}

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public Double getTongTien() {
        return tongTien;
    }

    public void setTongTien(Double tongTien) {
        this.tongTien = tongTien;
    }

    public Integer getMaMon() {
        return maMon;
    }

    public void setMaMon(Integer maMon) {
        this.maMon = maMon;
    }
}
