package com.example.quanlyquanan0609.Listener;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.quanlyquanan0609.Adapter.BanAnAdapter;
import com.example.quanlyquanan0609.Adapter.GoiMonAdapter;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class BanListener implements ValueEventListener {
    private BanAnAdapter.MyViewHolder holder;
    private BanAnClass banAnClass;
    private Context context;

    public BanListener(BanAnAdapter.MyViewHolder holder, BanAnClass banAnClass, Context context) {
        this.holder = holder;
        this.banAnClass = banAnClass;
        this.context = context;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            PGMClass pgmClass1 = dataSnapshot.getValue(PGMClass.class);
            if (pgmClass1.getPgm_TrangThai().equals("1")) {
                int mau = ContextCompat.getColor(context, R.color.bg_color);
                holder.tvTenBan.setTextColor(Color.WHITE);
                holder.cardItem.setCardBackgroundColor(mau);
            } else if(pgmClass1.getPgm_TrangThai().equals("2")){
                int mau = ContextCompat.getColor(context, R.color.blue_sea);
                holder.tvTenBan.setTextColor(Color.WHITE);
                holder.cardItem.setCardBackgroundColor(mau);
            }else {
                int mau = ContextCompat.getColor(context, R.color.bg_color);
                holder.tvTenBan.setTextColor(mau);
                holder.cardItem.setCardBackgroundColor(Color.WHITE);
            }
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}

