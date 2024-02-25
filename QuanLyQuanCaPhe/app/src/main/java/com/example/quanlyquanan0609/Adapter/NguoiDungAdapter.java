package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.R;
import com.example.quanlyquanan0609.ThemMonAnActivity;
import com.example.quanlyquanan0609.ThongTinTaiKhoanActivity;


import java.util.ArrayList;

public class NguoiDungAdapter extends RecyclerView.Adapter<NguoiDungAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<NguoiDungClass> list;

    public NguoiDungAdapter(Context context, ArrayList<NguoiDungClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_nguoi_dung, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NguoiDungClass nguoiDungClass = list.get(position);
        holder.tvTen.setText(nguoiDungClass.getNd_Ten());
        holder.tvMail.setText(nguoiDungClass.getNd_Email());
        if(!nguoiDungClass.getNd_HinhAnh().equals("Đường dẫn")){
            Glide.with(holder.itemView.getContext()).load(nguoiDungClass.getNd_HinhAnh()).apply(new RequestOptions().centerCrop()).into(holder.imageView);
        }
        if(nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
            holder.tvDTL.setText("ĐTL: " + nguoiDungClass.getNd_DiemTL());
        }else {
            holder.tvDTL.setText(nguoiDungClass.getNd_Quyen());
        }
        holder.lnNguoiDung.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ThongTinTaiKhoanActivity.class);
                intent.putExtra("uId", nguoiDungClass.getNd_Ma());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTen, tvMail, tvDTL;
        ImageView imageView;
        LinearLayout lnNguoiDung;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMail = itemView.findViewById(R.id.tvItemEmail);
            tvTen = itemView.findViewById(R.id.tvItemTen);
            tvDTL = itemView.findViewById(R.id.tvItemDTL);
            imageView = itemView.findViewById(R.id.ivItemNguoiDung);
            lnNguoiDung = itemView.findViewById(R.id.lnNguoiDung);
        }
    }

}
