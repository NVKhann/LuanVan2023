package com.example.quanlyquanan0609.Class;

public class DonGiaClass {
    String ngay, dg_Gia;
    Integer mon_Ma;

    public DonGiaClass(String ngay, Integer mon_Ma, String dg_Gia) {
        this.ngay = ngay;
        this.mon_Ma = mon_Ma;
        this.dg_Gia = dg_Gia;
    }
    public DonGiaClass(){}

    public String getNgay() {
        return ngay;
    }

    public void setNgay(String ngay) {
        this.ngay = ngay;
    }

    public Integer getMon_Ma() {
        return mon_Ma;
    }

    public void setMon_Ma(Integer mon_Ma) {
        this.mon_Ma = mon_Ma;
    }

    public String getDg_Gia() {
        return dg_Gia;
    }

    public void setDg_Gia(String dg_Gia) {
        this.dg_Gia = dg_Gia;
    }
}
