package com.example.quanlyquanan0609.Class;

public class ChiTietCaClass {
    Integer ca_Ma, kv_Ma;
    String ct_Ngay, nd_Ma, ct_VaoCa, ct_RaCa;

    public ChiTietCaClass(Integer ca_Ma, Integer kv_Ma, String ct_Ngay, String nd_Ma, String ct_VaoCa, String ct_RaCa) {
        this.ca_Ma = ca_Ma;
        this.kv_Ma = kv_Ma;
        this.ct_Ngay = ct_Ngay;
        this.nd_Ma = nd_Ma;
        this.ct_VaoCa = ct_VaoCa;
        this.ct_RaCa = ct_RaCa;
    }

    public ChiTietCaClass(){}

    public String getNd_Ma() {
        return nd_Ma;
    }

    public void setNd_Ma(String nd_Ma) {
        this.nd_Ma = nd_Ma;
    }

    public Integer getCa_Ma() {
        return ca_Ma;
    }

    public void setCa_Ma(Integer ca_Ma) {
        this.ca_Ma = ca_Ma;
    }

    public Integer getKv_Ma() {
        return kv_Ma;
    }

    public void setKv_Ma(Integer kv_Ma) {
        this.kv_Ma = kv_Ma;
    }

    public String getCt_Ngay() {
        return ct_Ngay;
    }

    public void setCt_Ngay(String ct_Ngay) {
        this.ct_Ngay = ct_Ngay;
    }

    public String getCt_VaoCa() {
        return ct_VaoCa;
    }

    public void setCt_VaoCa(String ct_VaoCa) {
        this.ct_VaoCa = ct_VaoCa;
    }

    public String getCt_RaCa() {
        return ct_RaCa;
    }

    public void setCt_RaCa(String ct_RaCa) {
        this.ct_RaCa = ct_RaCa;
    }
}
