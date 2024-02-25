package com.example.quanlyquanan0609.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.Interface.OnItemClickListener;
import com.example.quanlyquanan0609.Listener.PhieuGoiMonListener;
import com.example.quanlyquanan0609.PhieuGoiMonActivity;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GoiMonAdapter extends RecyclerView.Adapter<GoiMonAdapter.MyViewHolder> {
    Context context;
    ArrayList<BanAnClass> listBan;
    String hienThi, act;
    androidx.appcompat.widget.Toolbar toolbar;
    OnItemClickListener listener;

    public GoiMonAdapter(Context context, ArrayList<BanAnClass> listBan, androidx.appcompat.widget.Toolbar toolbar, String hienThi, String act) {
        this.context = context;
        this.listBan = listBan;
        this.toolbar = toolbar;
        this.hienThi = hienThi;
        this.act = act;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ban_an,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        BanAnClass banAnClass = listBan.get(position);
        holder.tvTenBan.setText(banAnClass.getBan_Ten());
        funcHienThiMau(holder, banAnClass);
        if(hienThi.equals("Có khách")){
            DatabaseReference dataPGM = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
            dataPGM.orderByChild("ban_Ma").equalTo(banAnClass.getBan_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    boolean coKhach = false;
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                        if(pgmClass.getPgm_TrangThai().equals("1") && pgmClass.getBan_Ma().equals(banAnClass.getBan_Ma())){
                            coKhach = true;
                            break;
                        }
                    }
                    if(!coKhach){
                        holder.cardItem.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        holder.cardItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(act.equals("Hiển thị")){
                    Intent intent = new Intent(context, PhieuGoiMonActivity.class);
                    intent.putExtra("maBan", banAnClass.getBan_Ma());
                    intent.putExtra("tenBan", banAnClass.getBan_Ten());
                    context.startActivity(intent);
                } else if(act.equals("Chuyển từ")){
                    if(listener != null){
                        listener.onItemClick(position, banAnClass.getBan_Ma());
                    }
                } else if (act.equals("Chuyển đến")) {
                    if(listener != null){
                        listener.onItemClick(position, banAnClass.getBan_Ma());
                    }
                }

            }
        });

    }

    private void funcHienThiMau(GoiMonAdapter.MyViewHolder holder, BanAnClass banAnClass){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        Query query = databaseReference.orderByChild("ban_Ma").equalTo(banAnClass.getBan_Ma());
        ValueEventListener listener = new PhieuGoiMonListener(holder, banAnClass, context);
        query.addValueEventListener(listener);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return listBan.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView tvTenBan;
        public CardView cardItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenBan = itemView.findViewById(R.id.tvBanTen);
            cardItem = itemView.findViewById(R.id.cardBanAn);
        }
    }
}
