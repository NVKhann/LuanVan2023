package com.example.quanlyquanan0609.Class;

public class DangKyCaClass {
    Integer ca_Ma, kv_Ma;
    String nd_Ma, ct_Ngay, dkc_VaoCa, dkc_RaCa;

    public DangKyCaClass(Integer ca_Ma, Integer kv_Ma, String nd_Ma, String ct_Ngay, String dkc_VaoCa, String dkc_RaCa) {
        this.ca_Ma = ca_Ma;
        this.kv_Ma = kv_Ma;
        this.nd_Ma = nd_Ma;
        this.ct_Ngay = ct_Ngay;
        this.dkc_VaoCa = dkc_VaoCa;
        this.dkc_RaCa = dkc_RaCa;
    }
    public DangKyCaClass(){}

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

    public String getNd_Ma() {
        return nd_Ma;
    }

    public void setNd_Ma(String nd_Ma) {
        this.nd_Ma = nd_Ma;
    }

    public String getCt_Ngay() {
        return ct_Ngay;
    }

    public void setCt_Ngay(String ct_Ngay) {
        this.ct_Ngay = ct_Ngay;
    }

    public String getDkc_VaoCa() {
        return dkc_VaoCa;
    }

    public void setDkc_VaoCa(String dkc_VaoCa) {
        this.dkc_VaoCa = dkc_VaoCa;
    }

    public String getDkc_RaCa() {
        return dkc_RaCa;
    }

    public void setDkc_RaCa(String dkc_RaCa) {
        this.dkc_RaCa = dkc_RaCa;
    }
}
