package com.example.quanlyquanan0609.Class;

public class NguoiDungClass {
    String Nd_Ma, Nd_Ten, Nd_DiaChi, Nd_Sdt, Nd_Email, Nd_Quyen, Nd_HinhAnh;
    Integer Nd_DiemTL;

    public NguoiDungClass(String nd_Ma, String nd_Ten, String nd_DiaChi, String nd_Sdt, String nd_Email, String nd_Quyen, String nd_HinhAnh, Integer nd_DiemTL) {
        Nd_Ma = nd_Ma;
        Nd_Ten = nd_Ten;
        Nd_DiaChi = nd_DiaChi;
        Nd_Sdt = nd_Sdt;
        Nd_Email = nd_Email;
        Nd_Quyen = nd_Quyen;
        Nd_HinhAnh = nd_HinhAnh;
        Nd_DiemTL = nd_DiemTL;
    }
    public NguoiDungClass(){}

    public String getNd_Ma() {
        return Nd_Ma;
    }

    public void setNd_Ma(String nd_Ma) {
        Nd_Ma = nd_Ma;
    }

    public String getNd_Ten() {
        return Nd_Ten;
    }

    public void setNd_Ten(String nd_Ten) {
        Nd_Ten = nd_Ten;
    }

    public String getNd_DiaChi() {
        return Nd_DiaChi;
    }

    public void setNd_DiaChi(String nd_DiaChi) {
        Nd_DiaChi = nd_DiaChi;
    }

    public String getNd_Sdt() {
        return Nd_Sdt;
    }

    public void setNd_Sdt(String nd_Sdt) {
        Nd_Sdt = nd_Sdt;
    }

    public String getNd_Quyen() {
        return Nd_Quyen;
    }

    public void setNd_Quyen(String nd_Quyen) {
        Nd_Quyen = nd_Quyen;
    }

    public String getNd_HinhAnh() {
        return Nd_HinhAnh;
    }

    public String getNd_Email() {
        return Nd_Email;
    }

    public void setNd_Email(String nd_Email) {
        Nd_Email = nd_Email;
    }

    public void setNd_HinhAnh(String nd_HinhAnh) {
        Nd_HinhAnh = nd_HinhAnh;
    }

    public Integer getNd_DiemTL() {
        return Nd_DiemTL;
    }

    public void setNd_DiemTL(Integer nd_DiemTL) {
        Nd_DiemTL = nd_DiemTL;
    }
}
