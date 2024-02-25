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

import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.KhuyenMaiClass;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.HoaDonActivity;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

public class ThongKeHoaDonAdapter extends RecyclerView.Adapter<ThongKeHoaDonAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<HoaDonClass> list;
    NumberFormat numberFormat = new DecimalFormat("#,###.##");

    public ThongKeHoaDonAdapter(Context context, ArrayList<HoaDonClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_thong_ke_hoa_don, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HoaDonClass hoaDonClass = list.get(position);
        holder.tvMaHD.setText("HD" + hoaDonClass.getHd_Ma());
        holder.tvNgay.setText(hoaDonClass.getHd_Ngay() + " " + hoaDonClass.getHd_GioVao().substring(0, 5));
        holder.tvTrangThai.setText("Đã thanh toán");
        holder.tvTien.setText(numberFormat.format(hoaDonClass.getHd_TongTien()) + " VNĐ");
        funcLayMaNguoiDung(hoaDonClass.getPgm_Ma(), holder, "");
        holder.lnThongKe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcLayMaNguoiDung(hoaDonClass.getPgm_Ma(), holder, "Chuyen");
            }
        });
    }

    private void funcLayMaNguoiDung(Integer pgmMa, MyViewHolder holder, String act) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ndMa = null;
                Integer maBan = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                   PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    ndMa = pgmClass.getNd_Ma();
                    maBan = pgmClass.getBan_Ma();
                }
                if(ndMa != null){
                    funcHienThiNguoiDung(ndMa, holder);
                }
                if(maBan != 0){
                    funcLayTenBan(maBan , holder);
                }
                if(act.equals("Chuyen")){
                    Intent intent = new Intent(context, HoaDonActivity.class);
                    intent.putExtra("maPGM", pgmMa);
                    intent.putExtra("maBan", maBan);
                    intent.putExtra("ac", "ThongKe");
                    context.startActivity(intent);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayTenBan(Integer maBan, MyViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByKey().equalTo(String.valueOf(maBan)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kvMa = 0;
                String banTen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    banTen = banAnClass.getBan_Ten();
                    kvMa = banAnClass.getKv_Ma();
                }
                if(banTen != null && kvMa != 0){
                    funcLayTenKV(kvMa, banTen, holder);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayTenKV(Integer kvMa, String banTen, MyViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByKey().equalTo(String.valueOf(kvMa)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String kvTen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    kvTen = khuVucClass.getKv_Ten();
                }
                if(kvTen != null){
                    holder.tvKhuVuc.setText(kvTen + " - " + banTen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiNguoiDung(String ndMa, MyViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByKey().equalTo(ndMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    if(!nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        holder.tvTen.setText("Khách lẻ");
                    }else {
                        holder.tvTen.setText(nguoiDungClass.getNd_Ten());
                    }
                }
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
        TextView tvMaHD, tvTen, tvNgay, tvTien, tvTrangThai, tvKhuVuc;
        LinearLayout lnThongKe;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaHD = itemView.findViewById(R.id.tvItemTKHDMaHd);
            tvTen = itemView.findViewById(R.id.tvItemTKHDTenKhach);
            tvNgay = itemView.findViewById(R.id.tvItemTKHDNgay);
            tvTien = itemView.findViewById(R.id.tvItemTKHDTien);
            tvTrangThai = itemView.findViewById(R.id.tvItemTKHDTrangThai);
            tvKhuVuc = itemView.findViewById(R.id.tvItemTKHDKhuVuc);
            lnThongKe = itemView.findViewById(R.id.lnItemTKHD);
        }
    }
}
