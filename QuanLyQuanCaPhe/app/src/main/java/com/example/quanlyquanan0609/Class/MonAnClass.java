package com.example.quanlyquanan0609.Class;

public class MonAnClass {
    Integer mon_Ma;
    String mon_Ten, mon_Loai, mon_HinhAnh;

    public MonAnClass(Integer mon_Ma, String mon_Ten, String mon_Loai, String mon_HinhAnh) {
        this.mon_Ma = mon_Ma;
        this.mon_Ten = mon_Ten;
        this.mon_Loai = mon_Loai;
        this.mon_HinhAnh = mon_HinhAnh;
    }
    public MonAnClass(){}

    public Integer getMon_Ma() {
        return mon_Ma;
    }

    public void setMon_Ma(Integer mon_Ma) {
        this.mon_Ma = mon_Ma;
    }

    public String getMon_Ten() {
        return mon_Ten;
    }

    public void setMon_Ten(String mon_Ten) {
        this.mon_Ten = mon_Ten;
    }

    public String getMon_Loai() {
        return mon_Loai;
    }

    public void setMon_Loai(String mon_Loai) {
        this.mon_Loai = mon_Loai;
    }

    public String getMon_HinhAnh() {
        return mon_HinhAnh;
    }

    public void setMon_HinhAnh(String mon_HinhAnh) {
        this.mon_HinhAnh = mon_HinhAnh;
    }
}
