package com.example.quanlyquanan0609.Listener;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.quanlyquanan0609.Adapter.DangKyCaAdapter;
import com.example.quanlyquanan0609.Adapter.HienThiChiTietCaAdapter;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.DangKyCaClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class DangKyCaListener implements ValueEventListener {
    private DangKyCaAdapter.MyViewHolder holder;
    private DangKyCaClass dKCaClass;
    private Context context;

    public DangKyCaListener(DangKyCaAdapter.MyViewHolder holder, DangKyCaClass dangKyCaClass, Context context) {
        this.holder = holder;
        this.dKCaClass = dangKyCaClass;
        this.context = context;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            DangKyCaClass dangKyCaClass = dataSnapshot.getValue(DangKyCaClass.class);
            if(uId.equals(dangKyCaClass.getNd_Ma())){
                if(dangKyCaClass.getDkc_VaoCa().equals("") || dangKyCaClass.getDkc_VaoCa().equals(null)){
                    holder.tvVao.setEnabled(false);
                }else {
                    holder.tvVao.setEnabled(true);

                }
            }

        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
