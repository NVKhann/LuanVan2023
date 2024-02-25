package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class DanhSachDangKyCaNhanVienAdapter extends RecyclerView.Adapter<DanhSachDangKyCaNhanVienAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<ChiTietCaClass> list;

    public DanhSachDangKyCaNhanVienAdapter(Context context, ArrayList<ChiTietCaClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_danh_sach_dkc_nhan_vien, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChiTietCaClass chiTietCaClass = list.get(position);
        holder.tvNgay.setText("Ngày: " + chiTietCaClass.getCt_Ngay());
        funcHienThiDanhSachCaTheoNgay(chiTietCaClass, holder);
    }

    private void funcHienThiDanhSachCaTheoNgay(ChiTietCaClass chiTietCaClass, MyViewHolder holder) {
        ArrayList<ChiTietCaClass> listCa = new ArrayList<>();
        holder.rvDanhSach.setHasFixedSize(true);
        holder.rvDanhSach.setLayoutManager(new LinearLayoutManager(context));
        DanhSachCaNhanVienTheoNgayAdapter danhSachCaTheoNgayAdapter = new DanhSachCaNhanVienTheoNgayAdapter(context, listCa);
        holder.rvDanhSach.setAdapter(danhSachCaTheoNgayAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        databaseReference.orderByChild("ct_Ngay").equalTo(chiTietCaClass.getCt_Ngay()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listCa != null){
                    listCa.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietCaClass chiTietNhanVien = dataSnapshot.getValue(ChiTietCaClass.class);
                    if(chiTietCaClass.getNd_Ma().equals(chiTietNhanVien.getNd_Ma())){
                        listCa.add(chiTietNhanVien);
                    }
                }
//                Collections.sort(list, new Comparator<ChiTietCaClass>() {
//                    @Override
//                    public int compare(ChiTietCaClass chiTietCaClass, ChiTietCaClass t1) {
//                        return chiTietCaClass.getCa_Ma().compareTo(t1.getCa_Ma()); // Đảo ngược kết quả trả về
//                    }
//                });
                danhSachCaTheoNgayAdapter.notifyDataSetChanged();
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
        TextView tvNgay;
        RecyclerView rvDanhSach;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNgay = itemView.findViewById(R.id.tvDSDKCNVNgay);
            rvDanhSach = itemView.findViewById(R.id.rvDSDKCNV);
        }
    }
}
