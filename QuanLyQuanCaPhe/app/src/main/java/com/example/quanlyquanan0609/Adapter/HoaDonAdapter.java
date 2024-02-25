package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.ChiTietPGMClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HoaDonAdapter extends RecyclerView.Adapter<HoaDonAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<ChiTietPGMClass> listChiTiet;

    public HoaDonAdapter(Context context, ArrayList<ChiTietPGMClass> listChiTiet) {
        this.context = context;
        this.listChiTiet = listChiTiet;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hoa_don, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NumberFormat numberFormat = new DecimalFormat("#,###.##");
        ChiTietPGMClass chiTietPGMClass = listChiTiet.get(position);
        Integer maMon = chiTietPGMClass.getMon_Ma();
        Integer thanhTien = chiTietPGMClass.getCt_DonGia() * chiTietPGMClass.getCt_SoLuong();
        holder.tvGia.setText(numberFormat.format(thanhTien));
        holder.tvSoLuong.setText(chiTietPGMClass.getCt_SoLuong() + " x " + numberFormat.format(chiTietPGMClass.getCt_DonGia()));
        if(!chiTietPGMClass.getCt_GhiChu().equals("")){
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText("Ghi chú: " + chiTietPGMClass.getCt_GhiChu());
        }
        // Hien thị ten mon
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        databaseReference.child(String.valueOf(maMon)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tenMon = null;
                if(snapshot.exists()){
                    tenMon = snapshot.child("mon_Ten").getValue(String.class);
                }
                holder.tvTenMon.setText(tenMon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // End Hien thi ten mon

    }

    @Override
    public int getItemCount() {
        return listChiTiet.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTenMon,tvGia, tvSoLuong, tvGhiChu;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tvItemHDMonAn);
            tvGhiChu = itemView.findViewById(R.id.tvItemHDGhiChu);
            tvGia = itemView.findViewById(R.id.tvItemHDGia);
            tvSoLuong = itemView.findViewById(R.id.tvItemHDSoLuong);
        }
    }
}
