package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.DangKyCaClass;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HienThiListDKiAdapter extends RecyclerView.Adapter<HienThiListDKiAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<ChiTietCaClass> list;

    public HienThiListDKiAdapter(Context context, ArrayList<ChiTietCaClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_dki_ca, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChiTietCaClass chiTietCaClass = list.get(position);
        funcLayTenTuMa(chiTietCaClass, holder);
    }

    private void funcLayTenTuMa(ChiTietCaClass chiTietCaClass, MyViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByKey().equalTo(chiTietCaClass.getNd_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    holder.textView.setText(nguoiDungClass.getNd_Ten());
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
        TextView textView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvListDkiCa);
        }
    }
}
