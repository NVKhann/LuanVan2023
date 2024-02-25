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

public class XuatHoaDonAdapter extends RecyclerView.Adapter<XuatHoaDonAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<ChiTietPGMClass> list;

    public XuatHoaDonAdapter(Context context, ArrayList<ChiTietPGMClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_xuat_hoa_don, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NumberFormat numberFormat = new DecimalFormat("#,###.##");
        ChiTietPGMClass chiTietPGMClass = list.get(position);
        Integer maMon = chiTietPGMClass.getMon_Ma();
        String donGia = numberFormat.format(chiTietPGMClass.getCt_DonGia());
        String thanhTien = numberFormat.format(chiTietPGMClass.getCt_SoLuong() * chiTietPGMClass.getCt_DonGia());
        holder.tvDonGia.setText(donGia);
        holder.tvSl.setText(String.valueOf(chiTietPGMClass.getCt_SoLuong()));
        holder.tvThanhTien.setText(thanhTien);
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
                holder.tvMonTen.setText(tenMon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // End Hien thi ten mon
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvMonTen, tvSl, tvDonGia, tvGhiChu, tvThanhTien;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonTen = itemView.findViewById(R.id.tvItemXHDMonAn);
            tvSl = itemView.findViewById(R.id.tvItemXHDSoLuong);
            tvDonGia = itemView.findViewById(R.id.tvItemXHDDonGia);
            tvThanhTien = itemView.findViewById(R.id.tvItemXHDGia);
            tvGhiChu = itemView.findViewById(R.id.tvItemXHDGhiChu);
        }
    }
}
