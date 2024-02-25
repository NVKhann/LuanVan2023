package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.ChiTietPGMClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class PgmAdapter extends RecyclerView.Adapter<PgmAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<ChiTietPGMClass> listCTPgm;

    public PgmAdapter(Context context, ArrayList<ChiTietPGMClass> listCTPgm) {
        this.context = context;
        this.listCTPgm = listCTPgm;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pgm, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        NumberFormat numberFormat = new DecimalFormat("#,###.##");
        ChiTietPGMClass pgmClass = listCTPgm.get(position);
        String ghiChu = pgmClass.getCt_GhiChu();
        Integer maMon = pgmClass.getMon_Ma();
        Integer soLuong = pgmClass.getCt_SoLuong();
        Integer giaMon = pgmClass.getCt_DonGia();
        Integer thanhTien = giaMon*soLuong;
        holder.tvGia.setText(numberFormat.format(thanhTien));
        holder.tvSL.setText("SL: " + soLuong + " x " + numberFormat.format(giaMon));
        if(ghiChu.equals("") || ghiChu.equals(null)){
            holder.tvGhiChu.setText(ghiChu);
        }else {
            holder.tvGhiChu.setVisibility(View.VISIBLE);
            holder.tvGhiChu.setText("Ghi chú: " + ghiChu);
        }
        // Hien thị ten mon
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("MonAn");
        databaseReference.child(String.valueOf(maMon)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String tenMon = null;
                if(snapshot.exists()){
                    tenMon = snapshot.child("mon_Ten").getValue(String.class);
                }
                holder.tvMonAn.setText(tenMon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // End Hien thi ten mon

        holder.ivXoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            String tenMon = holder.tvMonAn.getText().toString();
            funcTaoDialogXoaMon(pgmClass, tenMon);
            }
        });
        holder.lnMonAn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tenMon = holder.tvMonAn.getText().toString();
                funcTaoDialogSuaMon(pgmClass, tenMon);
            }
        });
    }

    private void funcTaoDialogSuaMon(ChiTietPGMClass pgmClass, String tenMon) {
        NumberFormat numberFormat = new DecimalFormat("#,###.##");
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_them_pgm, null);
        TextView tvTitle, tvGia;
        EditText edSl, edGhiChu;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvGia = view.findViewById(R.id.tvTPGMGia);
        edSl = view.findViewById(R.id.edTPGMSoLuong);
        edGhiChu = view.findViewById(R.id.edTPGMGhiChu);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        tvTitle.setText(tenMon);
        tvGia.setText(String.valueOf(numberFormat.format(pgmClass.getCt_DonGia())));
        edSl.setText(String.valueOf(pgmClass.getCt_SoLuong()));
        edGhiChu.setText(pgmClass.getCt_GhiChu());
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer soLuong;
                if(edSl.getText().toString() == null){
                    soLuong = 1;
                }else {
                    soLuong = Integer.valueOf(edSl.getText().toString());
                }
                String ghiChu = edGhiChu.getText().toString();
                funCapNhatMon(pgmClass.getPgm_Ma(), pgmClass.getMon_Ma(), soLuong, ghiChu, alertDialog);
            }
        });
        alertDialog.show();
    }

    private void funCapNhatMon(Integer pgmMa, Integer monMa, Integer soLuong, String ghiChu, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        databaseReference.child(pgmMa+"_"+monMa).child("ct_SoLuong").setValue(soLuong);
        databaseReference.child(pgmMa+"_"+monMa).child("ct_GhiChu").setValue(ghiChu);
        alertDialog.dismiss();
        Toast.makeText(context, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
    }

    private void funcTaoDialogXoaMon(ChiTietPGMClass pgmClass, String tenMon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNoiDung;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        tvTitle.setText("Xóa " + tenMon.toLowerCase());
        tvNoiDung.setText("Xác nhận xóa " + pgmClass.getCt_SoLuong() + " " + tenMon.toLowerCase());
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcXoaMon(pgmClass.getPgm_Ma(), pgmClass.getMon_Ma(), alertDialog);
            }
        });
        alertDialog.show();
    }

    private void funcXoaMon(Integer pgmMa, Integer monMa, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        Query query = databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Integer maMonAn = dataSnapshot.child("mon_Ma").getValue(Integer.class);
                    if (monMa == maMonAn){
                        dataSnapshot.getRef().removeValue();
                        alertDialog.dismiss();
                        Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Lỗi!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listCTPgm.size();
    }

    public static  class MyViewHolder extends RecyclerView.ViewHolder{
        TextView tvMonAn, tvSL, tvGia, tvGhiChu;
        ImageView ivXoa;
        LinearLayout lnMonAn;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMonAn = itemView.findViewById(R.id.tvItemPGMMonAn);
            tvSL = itemView.findViewById(R.id.tvItemPGMSL);
            tvGia = itemView.findViewById(R.id.tvItemPGMGia);
            tvGhiChu = itemView.findViewById(R.id.tvItemPGMGhiChu);
            ivXoa = itemView.findViewById(R.id.ivItemPGMXoa);
            lnMonAn = itemView.findViewById(R.id.lnMonAn);
        }
    }
}
