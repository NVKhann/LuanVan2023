package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.Listener.BanListener;
import com.example.quanlyquanan0609.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BanAnAdapter extends RecyclerView.Adapter<BanAnAdapter.MyViewHolder> {
    Context context;
    ArrayList<BanAnClass> listBanAn;

    public BanAnAdapter(Context context, ArrayList<BanAnClass> listBanAn) {
        this.context = context;
        this.listBanAn = listBanAn;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(context).inflate(R.layout.item_ban_an, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        BanAnClass banAnClass = listBanAn.get(position);
        holder.tvTenBan.setText(banAnClass.getBan_Ten());
        funcHienThiMau(holder, banAnClass);
        holder.tvTenBan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
                databaseReference.orderByChild("ban_Ma").equalTo(banAnClass.getBan_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean coKhach = false;
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                            if(pgmClass.getPgm_TrangThai().equals("1")){
                                coKhach = true;
                                break;
                            }
                        }
                        if(!coKhach){
                            funcTaoBottomDialog(banAnClass);
                        }else {
                            Toast.makeText(context, "Bàn hiện đang có khách", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }
    private void funcHienThiMau(BanAnAdapter.MyViewHolder holder, BanAnClass banAnClass){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        Query query = databaseReference.orderByChild("ban_Ma").equalTo(banAnClass.getBan_Ma());
        ValueEventListener listener = new BanListener(holder, banAnClass, context);
        query.addValueEventListener(listener);
    }
    private  void funcTaoBottomDialog(BanAnClass banAnClass){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view = LayoutInflater.from(context).inflate(R.layout.bot_dialog, null, false);
        TextView tvSua, tvXoa;
        String maBan, tenBan, trangThaiBan;
        tvXoa = view.findViewById(R.id.sdTvXoa);
        tvSua = view.findViewById(R.id.sdTvSua);
        tvSua.setText("Chỉnh sửa bàn");
        tvXoa.setText("Xóa bàn");
        maBan = String.valueOf(banAnClass.getBan_Ma());
        tenBan = banAnClass.getBan_Ten();
        tvSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogSua(maBan, tenBan);
                bottomSheetDialog.dismiss();
            }
        });
        tvXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogXoa(maBan, tenBan);
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    private void funcTaoDialogXoa(String maBan, String tenBan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        TextView textView = view.findViewById(R.id.tvTextView);
        textView.setText("Xác nhận xóa " + tenBan);
        Button btnHuy, btnOk;
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
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
                FirebaseDatabase.getInstance().getReference("BanAn").child(maBan).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
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

    private void funcTaoDialogSua(String maBan, String tenBan) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_1_edit_text, null);
        EditText edTen = view.findViewById(R.id.edEditText);
        TextView textView = view.findViewById(R.id.tvTitle);
        Button btnHuy, btnOk;
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        edTen.setHint("Nhập tên bàn");
        edTen.setText(tenBan);
        textView.setText("Chỉnh sửa bàn");
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
                String tenMoi = edTen.getText().toString();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
                if(TextUtils.isEmpty(tenMoi)){
                    edTen.setError("Vui lòng nhập tên bàn");
                }else {
                    databaseReference.orderByChild("ban_Ten").equalTo(tenMoi).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChildren()){
                                edTen.setError("Bàn đã tồn tại");
                            }else {
                                databaseReference.child(maBan).child("ban_Ten").setValue(tenMoi).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(context, "Sửa thành công", Toast.LENGTH_SHORT).show();
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
        return listBanAn.size();
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
