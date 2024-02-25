package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.BanAnActivity;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class KhuVucAdapter extends RecyclerView.Adapter<KhuVucAdapter.MyViewHolder>{
    Context context;
    ArrayList<KhuVucClass> listKhuVuc;

    public KhuVucAdapter(Context context, ArrayList<KhuVucClass> listKhuVuc) {
        this.context = context;
        this.listKhuVuc = listKhuVuc;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_khu_vuc, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        KhuVucClass khuVucClass = listKhuVuc.get(position);
        holder.tvTenKv.setText(khuVucClass.getKv_Ten());
        holder.trangThaiSw(khuVucClass.getKv_TrangThai());
        if(khuVucClass.getKv_TrangThai().equals("Có khách")){
            holder.swTrangThai.setEnabled(false);

        }
        FirebaseDatabase.getInstance().getReference("BanAn").orderByChild("kv_Ma").equalTo(khuVucClass.getKv_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                if(count != 0){
                    holder.tvSoLuong.setText("Số lượng: " + count);
                    holder.tvSoLuong.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        holder.swTrangThai.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    Toast.makeText(context, "Hiển thị "  + khuVucClass.getKv_Ten(), Toast.LENGTH_SHORT).show();
                    khuVucClass.setKv_TrangThai("Hiển thị");
                }else {
                    Toast.makeText(context, "Ẩn " + khuVucClass.getKv_Ten(), Toast.LENGTH_SHORT).show();
                    khuVucClass.setKv_TrangThai("Ẩn");
                }
                FirebaseDatabase.getInstance().getReference("KhuVuc").child(String.valueOf(khuVucClass.getKv_Ma())).child("kv_TrangThai").setValue(khuVucClass.getKv_TrangThai());
            }
        });
        holder.ivSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoBottomDialog(khuVucClass);
            }
        });
        holder.lnItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BanAnActivity.class);
                intent.putExtra("maKhuVuc", khuVucClass.getKv_Ma());
                intent.putExtra("tenKhuVuc", khuVucClass.getKv_Ten());
                context.startActivity(intent);
            }
        });
    }
    private void funcTaoBottomDialog(KhuVucClass khuVucClass){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bot_dialog, null, false);
        TextView tvBotSua, tvBotXoa;
        String tenKhuVuc;
        Integer maKhuVuc;
        tvBotSua = view.findViewById(R.id.sdTvSua);
        tvBotXoa = view.findViewById(R.id.sdTvXoa);
        tvBotXoa.setText("Xóa khu vực");
        tvBotSua.setText("Chỉnh sửa khu vực");
        maKhuVuc = khuVucClass.getKv_Ma();
        tenKhuVuc = khuVucClass.getKv_Ten();
        tvBotSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!khuVucClass.getKv_TrangThai().equals("Có khách")){
                    funcTaoDialogSua(maKhuVuc, tenKhuVuc);
                    bottomSheetDialog.dismiss();
                }else {
                    Toast.makeText(context, "Khu vực đang có khách", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();

                }
            }
        });
        tvBotXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!khuVucClass.getKv_TrangThai().equals("Có khách")){
                    funcTaoDialogXoa(maKhuVuc, tenKhuVuc);
                    bottomSheetDialog.dismiss();
                }else {
                    Toast.makeText(context, "Khu vực đang có khách", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();

                }
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
    private void funcTaoDialogXoa(Integer maKhuVuc, String strTenKhuVuc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        TextView textView = view.findViewById(R.id.tvTextView);
        Button btnHuy, btnOk;
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        textView.setText("Xác nhận xóa " + strTenKhuVuc + " và tất cả các bàn thuộc " + strTenKhuVuc);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference("KhuVuc").child(String.valueOf(maKhuVuc)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
                            databaseReference.orderByChild("kv_Ma").equalTo(maKhuVuc).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                        dataSnapshot.getRef().removeValue();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                        }else {
                            try {
                                task.getException();
                            }catch (Exception e){
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                        alertDialog.dismiss();

                    }
                });
            }
        });
        alertDialog.show();

    }

    private void funcTaoDialogSua(Integer maKhuVuc, String strTenKhuVuc) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_1_edit_text, null);
        EditText edTenKv = view.findViewById(R.id.edEditText);
        TextView textView = view.findViewById(R.id.tvTitle);
        Button btnHuy, btnOk;
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        edTenKv.setHint("Nhập tên khu vực");
        edTenKv.setText(strTenKhuVuc);
        textView.setText("Chỉnh sửa khu vực");
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenKhuVuc = edTenKv.getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
                if(TextUtils.isEmpty(tenKhuVuc)){
                    edTenKv.setError("Vui lòng nhập tên khu vực");
                }else {
                    databaseReference.orderByChild("kv_Ten").equalTo(tenKhuVuc).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                edTenKv.setError("Khu vực đã tồn tại");
                            }else {
                                databaseReference.child(String.valueOf(maKhuVuc)).child("kv_Ten").setValue(tenKhuVuc).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(context, "Chỉnh sửa thành công", Toast.LENGTH_SHORT).show();
                                        }else {
                                            try {
                                                throw task.getException();
                                            }catch (Exception e){
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                        alertDialog.dismiss();
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return listKhuVuc.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvTenKv, tvSoLuong;
        Switch swTrangThai;
        ImageView ivSua;
        LinearLayout lnItem;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTenKv = itemView.findViewById(R.id.tvItemKvTen);
            tvSoLuong = itemView.findViewById(R.id.tvItemKvSoLuong);
            ivSua = itemView.findViewById(R.id.ivItemKvSua);
            swTrangThai = itemView.findViewById(R.id.swItemKv);
            lnItem = itemView.findViewById(R.id.lnItemKv);
        }
        public void trangThaiSw(String trangThai) {
            swTrangThai.setChecked(trangThai.equals("Hiển thị") || trangThai.equals("Có khách"));
        }

    }
}
