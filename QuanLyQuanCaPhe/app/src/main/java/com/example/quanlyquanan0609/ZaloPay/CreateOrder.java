package com.example.quanlyquanan0609.ZaloPay;

import com.example.quanlyquanan0609.ZaloPay.Helper.Helpers;


import org.json.JSONObject;

import java.util.Date;

import okhttp3.FormBody;
import okhttp3.RequestBody;

public class CreateOrder {
    private class CreateOrderData {
        String AppId;
        String AppUser;
        String AppTime;
        String Amount;
        String AppTransId;
        String EmbedData;
        String Items;
        String BankCode;
        String Description;
        String Mac;
        String MaHD, Ngay, BanTen;

        private CreateOrderData(String amount, String maHD, String ngay, String banTen) throws Exception {
            long appTime = new Date().getTime();
            AppId = String.valueOf(AppInfo.APP_ID);
            AppUser = "Android_Demo";
            AppTime = String.valueOf(appTime);
            Amount = amount;
            AppTransId = Helpers.getAppTransId();
            EmbedData = "{}";
            Items = "[]";
            BankCode = "zalopayapp";
//            Description = "Merchant pay for order #" + Helpers.getAppTransId();
            Description  = "Thanh toán HĐ" + maHD + ", "+ banTen + ", ngày " + ngay;
            MaHD = maHD;
            Ngay = ngay;
            BanTen = banTen;
            String inputHMac = String.format("%s|%s|%s|%s|%s|%s|%s",
                    this.AppId,
                    this.AppTransId,
                    this.AppUser,
                    this.Amount,
                    this.AppTime,
                    this.EmbedData,
                    this.Items);

            Mac = Helpers.getMac(AppInfo.MAC_KEY, inputHMac);
        }
    }

     public JSONObject createOrder(String amount, String maHD, String ngay, String banTen) throws Exception {
        CreateOrderData input = new CreateOrderData(amount, maHD, ngay, banTen);

        RequestBody formBody = new FormBody.Builder()
                .add("appid", input.AppId)
                .add("appuser", input.AppUser)
                .add("apptime", input.AppTime)
                .add("amount", input.Amount)
                .add("apptransid", input.AppTransId)
                .add("embeddata", input.EmbedData)
                .add("item", input.Items)
                .add("bankcode", input.BankCode)
                .add("description", input.Description)
                .add("mac", input.Mac)
                .build();

        JSONObject data = HttpProvider.sendPost(AppInfo.URL_CREATE_ORDER, formBody);
        return data;
    }
}

