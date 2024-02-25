package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.KhuyenMaiClass;
import com.example.quanlyquanan0609.R;

import java.util.ArrayList;

public class KhuyenMaiAdapter extends RecyclerView.Adapter<KhuyenMaiAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<KhuyenMaiClass> list;

    public KhuyenMaiAdapter(Context context, ArrayList<KhuyenMaiClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_khuyen_mai, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        KhuyenMaiClass khuyenMaiClass = list.get(position);
        holder.tvTen.setText(khuyenMaiClass.getKm_TieuDe());
        holder.tvPhanTram.setText(khuyenMaiClass.getKm_PhanTram() + "%");
        holder.tvNgay.setText("Từ:    " + khuyenMaiClass.getKm_NgayBd() + "\nĐến: " + khuyenMaiClass.getKm_NgayKt());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTen, tvPhanTram, tvNgay;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTen = itemView.findViewById(R.id.tvTenKm);
            tvPhanTram = itemView.findViewById(R.id.tvPhanTramKm);
            tvNgay = itemView.findViewById(R.id.tvNgayKm);
        }
    }
}
