package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.HoaDonActivity;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class HoaDonKhachHangAdapter extends RecyclerView.Adapter<HoaDonKhachHangAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<HoaDonClass> list;
    NumberFormat numberFormat = new DecimalFormat("#,###.##");

    public HoaDonKhachHangAdapter(Context context, ArrayList<HoaDonClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hoa_don_khach_hang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HoaDonClass hoaDonClass = list.get(position);
        holder.tvMa.setText("HĐ" + hoaDonClass.getHd_Ma());
        holder.tvNgay.setText("Ngày: " + hoaDonClass.getHd_Ngay());
        holder.tvGia.setText(numberFormat.format(hoaDonClass.getHd_TongTien()) + " VNĐ");
        holder.lnList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcLayMaBan(hoaDonClass);
            }
        });
    }

    private void funcLayMaBan(HoaDonClass hoaDonClass) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("pgm_Ma").equalTo(hoaDonClass.getPgm_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer maBan = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    maBan = pgmClass.getBan_Ma();
                }
                Intent intent = new Intent(context, HoaDonActivity.class);
                intent.putExtra("maPGM", hoaDonClass.getPgm_Ma());
                intent.putExtra("maBan", maBan);
                intent.putExtra("ac", "ThongKe");
                context.startActivity(intent);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvMa, tvNgay, tvGia;
        LinearLayout lnList;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMa = itemView.findViewById(R.id.tvItemHDKHMa);
            tvNgay = itemView.findViewById(R.id.tvItemHDKHNgay);
            tvGia = itemView.findViewById(R.id.tvItemHDKHTien);
            lnList = itemView.findViewById(R.id.lnItemHDKH);
        }
    }
}
