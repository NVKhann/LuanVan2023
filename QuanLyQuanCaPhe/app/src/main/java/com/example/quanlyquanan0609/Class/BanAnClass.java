package com.example.quanlyquanan0609.Class;

public class BanAnClass {
    Integer ban_Ma, kv_Ma;
    String ban_Ten;

    public BanAnClass(Integer ban_Ma, Integer kv_Ma, String ban_Ten) {
        this.ban_Ma = ban_Ma;
        this.kv_Ma = kv_Ma;
        this.ban_Ten = ban_Ten;
    }
    public BanAnClass(){}

    public Integer getBan_Ma() {
        return ban_Ma;
    }

    public void setBan_Ma(Integer ban_Ma) {
        this.ban_Ma = ban_Ma;
    }

    public Integer getKv_Ma() {
        return kv_Ma;
    }

    public void setKv_Ma(Integer kv_Ma) {
        this.kv_Ma = kv_Ma;
    }

    public String getBan_Ten() {
        return ban_Ten;
    }

    public void setBan_Ten(String ban_Ten) {
        this.ban_Ten = ban_Ten;
    }
}
