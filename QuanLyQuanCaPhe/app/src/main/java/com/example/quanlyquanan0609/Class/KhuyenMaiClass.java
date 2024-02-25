package com.example.quanlyquanan0609.Class;

public class KhuyenMaiClass {
    Integer km_Ma, km_Muc, km_PhanTram;
    String km_TieuDe, km_NgayBd, km_NgayKt, km_NoiDung;

    public KhuyenMaiClass(Integer km_Ma, Integer km_Muc, Integer km_PhanTram, String km_TieuDe, String km_NgayBd, String km_NgayKt, String km_NoiDung) {
        this.km_Ma = km_Ma;
        this.km_Muc = km_Muc;
        this.km_PhanTram = km_PhanTram;
        this.km_TieuDe = km_TieuDe;
        this.km_NgayBd = km_NgayBd;
        this.km_NgayKt = km_NgayKt;
        this.km_NoiDung = km_NoiDung;
    }
    public KhuyenMaiClass(){}

    public Integer getKm_Ma() {
        return km_Ma;
    }

    public void setKm_Ma(Integer km_Ma) {
        this.km_Ma = km_Ma;
    }

    public Integer getKm_Muc() {
        return km_Muc;
    }

    public void setKm_Muc(Integer km_Muc) {
        this.km_Muc = km_Muc;
    }

    public Integer getKm_PhanTram() {
        return km_PhanTram;
    }

    public void setKm_PhanTram(Integer km_PhanTram) {
        this.km_PhanTram = km_PhanTram;
    }

    public String getKm_TieuDe() {
        return km_TieuDe;
    }

    public void setKm_TieuDe(String km_TieuDe) {
        this.km_TieuDe = km_TieuDe;
    }

    public String getKm_NgayBd() {
        return km_NgayBd;
    }

    public void setKm_NgayBd(String km_NgayBd) {
        this.km_NgayBd = km_NgayBd;
    }

    public String getKm_NgayKt() {
        return km_NgayKt;
    }

    public void setKm_NgayKt(String km_NgayKt) {
        this.km_NgayKt = km_NgayKt;
    }

    public String getKm_NoiDung() {
        return km_NoiDung;
    }

    public void setKm_NoiDung(String km_NoiDung) {
        this.km_NoiDung = km_NoiDung;
    }
}
