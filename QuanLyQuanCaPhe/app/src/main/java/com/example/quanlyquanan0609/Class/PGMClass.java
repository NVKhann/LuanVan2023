package com.example.quanlyquanan0609.Class;

public class PGMClass {
    Integer pgm_Ma, ban_Ma;
    String nd_Ma, pgm_TrangThai;

    public PGMClass(Integer pgm_Ma, Integer ban_Ma, String nd_Ma, String pgm_TrangThai) {
        this.pgm_Ma = pgm_Ma;
        this.ban_Ma = ban_Ma;
        this.nd_Ma = nd_Ma;
        this.pgm_TrangThai = pgm_TrangThai;
    }
    public PGMClass(){}

    public Integer getPgm_Ma() {
        return pgm_Ma;
    }

    public void setPgm_Ma(Integer pgm_Ma) {
        this.pgm_Ma = pgm_Ma;
    }

    public Integer getBan_Ma() {
        return ban_Ma;
    }

    public void setBan_Ma(Integer ban_Ma) {
        this.ban_Ma = ban_Ma;
    }

    public String getNd_Ma() {
        return nd_Ma;
    }

    public void setNd_Ma(String nd_Ma) {
        this.nd_Ma = nd_Ma;
    }

    public String getPgm_TrangThai() {
        return pgm_TrangThai;
    }

    public void setPgm_TrangThai(String pgm_TrangThai) {
        this.pgm_TrangThai = pgm_TrangThai;
    }
}

