package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
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
import com.example.quanlyquanan0609.Class.DonGiaClass;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class MonAnTrangChuAdapter extends RecyclerView.Adapter<MonAnTrangChuAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<MonAnClass> list;

    public MonAnTrangChuAdapter(Context context, ArrayList<MonAnClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_mon_an, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        MonAnClass monAnClass = list.get(position);
        NumberFormat numberFormat = new DecimalFormat("#,###.##");
        holder.tvTenMon.setText(monAnClass.getMon_Ten());
        if(!monAnClass.getMon_HinhAnh().equals("Đường dẫn")){
            Glide.with(holder.itemView.getContext()).load(monAnClass.getMon_HinhAnh()).apply(new RequestOptions().centerCrop()).into(holder.ivHinhMon);
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("DonGia");
        databaseReference.orderByChild("mon_Ma").equalTo(monAnClass.getMon_Ma()).limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer gia = null;
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    DonGiaClass donGiaClass = dataSnapshot.getValue(DonGiaClass.class);
                    gia = Integer.valueOf(donGiaClass.getDg_Gia());
                }
                String giaF = numberFormat.format(gia);
                if(gia!=null){
                    holder.tvGiaMon.setText(giaF);

                }else {
                    holder.tvGiaMon.setText("0");
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
        TextView tvTenMon, tvGiaMon;
        ImageView ivHinhMon;
        LinearLayout lnMonAn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenMon = itemView.findViewById(R.id.tvItemMonAn);
            tvGiaMon = itemView.findViewById(R.id.tvItemMonAnGia);
            ivHinhMon = itemView.findViewById(R.id.ivItemMonAn);
            lnMonAn = itemView.findViewById(R.id.lnMonAn);
        }
    }
}
