package com.example.quanlyquanan0609.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.CaLamViecClass;
import com.example.quanlyquanan0609.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CtcCaAdapter extends RecyclerView.Adapter<CtcCaAdapter.MyViewholder>{
    private Context context;
    private ArrayList<CaLamViecClass> list;
    private ArrayList<CaLamViecClass> selectedItems = new ArrayList<>();

    public CtcCaAdapter(Context context, ArrayList<CaLamViecClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_ctc_ca, parent, false);
        return new MyViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewholder holder, @SuppressLint("RecyclerView") int position) {
        CaLamViecClass caLamViecClass = list.get(position);
        holder.textView.setText(caLamViecClass.getCa_Ten() + " (" + caLamViecClass.getCa_GioDB() + " - " + caLamViecClass.getCa_GioKT() + ")");
        holder.checkBox.setChecked(selectedItems.contains(caLamViecClass));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.contains(caLamViecClass)) {
                    selectedItems.remove(caLamViecClass);
                } else {
                    selectedItems.add(caLamViecClass);
                }
                Collections.sort(selectedItems, new Comparator<CaLamViecClass>() {
                    @Override
                    public int compare(CaLamViecClass item1, CaLamViecClass item2) {
                        // Sử dụng compareTo để so sánh chuỗi Ca_Ten
                        return item1.getCa_Ten().compareTo(item2.getCa_Ten());
                    }
                });
            }
        });

    }
    public ArrayList<CaLamViecClass> getSelectedItems() {
        return selectedItems;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewholder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        TextView textView;
        public MyViewholder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}
