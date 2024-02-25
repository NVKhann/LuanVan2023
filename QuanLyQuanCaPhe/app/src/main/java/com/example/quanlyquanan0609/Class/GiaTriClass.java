package com.example.quanlyquanan0609.Class;

public class GiaTriClass {
    Integer gt_Ma;
    String gt_Ten, gt_GiaTri;

    public GiaTriClass(Integer gt_Ma, String gt_Ten, String gt_GiaTri) {
        this.gt_Ma = gt_Ma;
        this.gt_Ten = gt_Ten;
        this.gt_GiaTri = gt_GiaTri;
    }
    public  GiaTriClass(){}

    public Integer getGt_Ma() {
        return gt_Ma;
    }

    public void setGt_Ma(Integer gt_Ma) {
        this.gt_Ma = gt_Ma;
    }

    public String getGt_Ten() {
        return gt_Ten;
    }

    public void setGt_Ten(String gt_Ten) {
        this.gt_Ten = gt_Ten;
    }

    public String getGt_GiaTri() {
        return gt_GiaTri;
    }

    public void setGt_GiaTri(String gt_GiaTri) {
        this.gt_GiaTri = gt_GiaTri;
    }
}
