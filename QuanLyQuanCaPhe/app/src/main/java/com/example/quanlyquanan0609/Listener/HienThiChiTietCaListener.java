package com.example.quanlyquanan0609.Listener;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.quanlyquanan0609.Adapter.CaLamViecAdapter;
import com.example.quanlyquanan0609.Adapter.HienThiChiTietCaAdapter;
import com.example.quanlyquanan0609.ChiTietCaActivity;
import com.example.quanlyquanan0609.Class.CaLamViecClass;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.DangKyCaClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class HienThiChiTietCaListener implements ValueEventListener {
    private HienThiChiTietCaAdapter.MyViewHolder holder;
    private CaLamViecClass caLamViecClass;
    Integer kvMa;
    String ngay, acNguon;
    private Context context;

    public HienThiChiTietCaListener(HienThiChiTietCaAdapter.MyViewHolder holder, CaLamViecClass caLamViecClass, Integer kvMa, String ngay, String acNguon, Context context) {
        this.holder = holder;
        this.caLamViecClass = caLamViecClass;
        this.kvMa = kvMa;
        this.ngay = ngay;
        this.acNguon = acNguon;
        this.context = context;
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        Integer soLuong = 0;
        boolean daDangKy = false;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            ChiTietCaClass chiTietCaClass = dataSnapshot.getValue(ChiTietCaClass.class);
            if (chiTietCaClass.getKv_Ma().equals(kvMa) && chiTietCaClass.getCt_Ngay().equals(ngay)) {
                soLuong = soLuong + 1;
            }
            if(chiTietCaClass.getNd_Ma().equals(uId) && chiTietCaClass.getKv_Ma().equals(kvMa) && chiTietCaClass.getCt_Ngay().equals(ngay)){
                daDangKy = true;
            }

        }
        if(soLuong == caLamViecClass.getCa_SoLuong()){
            if(!acNguon.equals("HienThi")){
                holder.lnCa.setEnabled(false);
            }
            if(daDangKy){
                holder.tvDangKy.setText("Đã đăng ký");
                holder.tvDangKy.setTextColor(context.getColor(R.color.red));
            }else {
                holder.tvDangKy.setText("Đã đủ");
                holder.tvDangKy.setTextColor(context.getColor(R.color.red));
            }
        }else if(daDangKy){
            holder.lnCa.setEnabled(false);
            holder.tvDangKy.setText("Đã đăng ký");
            holder.tvDangKy.setTextColor(context.getColor(R.color.red));
        }
        holder.tvSl.setText("SL: "+ soLuong+"/" + caLamViecClass.getCa_SoLuong());

    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {

    }
}
