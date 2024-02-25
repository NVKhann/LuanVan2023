package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.CaLamViecClass;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;

public class DanhSachCaNhanVienTheoNgayAdapter extends RecyclerView.Adapter<DanhSachCaNhanVienTheoNgayAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<ChiTietCaClass> list;

    public DanhSachCaNhanVienTheoNgayAdapter(Context context, ArrayList<ChiTietCaClass> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_danh_sach_ca_theo_ngay, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        ChiTietCaClass chiTietCaClass = list.get(position);
        if(!chiTietCaClass.getCt_VaoCa().equals("") && chiTietCaClass.getCt_RaCa().equals("")){
            holder.tvVao.setText(chiTietCaClass.getCt_VaoCa().substring(chiTietCaClass.getCt_VaoCa().length() - 6));
        }else if(!chiTietCaClass.getCt_VaoCa().equals("") && !chiTietCaClass.getCt_RaCa().equals("")){
            holder.tvVao.setText(chiTietCaClass.getCt_VaoCa().substring(chiTietCaClass.getCt_VaoCa().length() - 6));
            holder.tvRa.setText(chiTietCaClass.getCt_RaCa().substring(chiTietCaClass.getCt_RaCa().length() - 6));
        }
        funcHienThiTen(chiTietCaClass, holder);
        funcHienThiTrangThaiCa(chiTietCaClass, holder);
        holder.lnCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogThongTinCa(chiTietCaClass);
            }
        });
    }

    private void funcTaoDialogThongTinCa(ChiTietCaClass chiTietCaClass) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_hien_thi_chi_tiet_ca_nhan_vien, null);
        LinearLayout lnVao, lnVaoTT, lnRa, lnRaTT;
        TextView tvNgay, tvKhuVuc, tvTenCa, tvThoiGian, tvVaoCa, tvVaoCaTT, tvRaCa, tvRaCaTT;
        Button btnOK;
        lnVao = view.findViewById(R.id.lnTTCTCVao);
        lnVaoTT = view.findViewById(R.id.lnTTCTCVaoTT);
        lnRa = view.findViewById(R.id.lnTTCTCRa);
        lnRaTT = view.findViewById(R.id.lnTTCTCRaTT);
        tvNgay = view.findViewById(R.id.tvTTCTCNgay);
        tvKhuVuc = view.findViewById(R.id.tvTTCTCKhuVuc);
        tvTenCa = view.findViewById(R.id.tvTTCTCCa);
        tvThoiGian = view.findViewById(R.id.tvTTCTCTG);
        tvVaoCa = view.findViewById(R.id.tvTTCTCVaoCa);
        tvVaoCaTT = view.findViewById(R.id.tvTTCTCVaoCaTT);
        tvRaCa = view.findViewById(R.id.tvTTCTCRaCa);
        tvRaCaTT = view.findViewById(R.id.tvTTCTCRaCaTT);
        btnOK = view.findViewById(R.id.btnOk);

        tvNgay.setText(chiTietCaClass.getCt_Ngay());

        if(chiTietCaClass.getCt_VaoCa().equals("")){
            lnVao.setVisibility(View.GONE);
            lnVaoTT.setVisibility(View.GONE);
        }else {
            tvVaoCa.setText(chiTietCaClass.getCt_VaoCa());
            funcHienThiTrangThaiTv(chiTietCaClass, tvVaoCaTT, "Vao");
        }

        if(chiTietCaClass.getCt_RaCa().equals("")){
            lnRa.setVisibility(View.GONE);
            lnRaTT.setVisibility(View.GONE);
        }else {
            tvRaCa.setText(chiTietCaClass.getCt_RaCa());
            funcHienThiTrangThaiTv(chiTietCaClass, tvRaCaTT, "Ra");

        }

        funcLayTenKV(chiTietCaClass.getKv_Ma(), tvKhuVuc);
        funcLayTenCa(chiTietCaClass.getCa_Ma(), tvTenCa);
        funcLayThoiGian(chiTietCaClass.getCa_Ma(), tvThoiGian);

        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void funcLayTenCa(Integer caMa, TextView tvTenCa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.orderByChild("ca_Ma").equalTo(caMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    tvTenCa.setText(caLamViecClass.getCa_Ten());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayTenKV(Integer kvMa, TextView tvKhuVuc) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    tvKhuVuc.setText(khuVucClass.getKv_Ten());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayThoiGian(Integer caMa, TextView tvThoiGian) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.orderByChild("ca_Ma").equalTo(caMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    tvThoiGian.setText(caLamViecClass.getCa_GioDB() + " ~ " + caLamViecClass.getCa_GioKT());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcHienThiTrangThaiTv(ChiTietCaClass chiTietCaClass, TextView textView, String act) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.orderByChild("ca_Ma").equalTo(chiTietCaClass.getCa_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    LocalTime start = LocalTime.parse(caLamViecClass.getCa_GioDB());
                    LocalTime end = LocalTime.parse(caLamViecClass.getCa_GioKT());

                    if(!chiTietCaClass.getCt_VaoCa().equals("") && act.equals("Vao")){
                        String gioDki = chiTietCaClass.getCt_VaoCa().substring(chiTietCaClass.getCt_VaoCa().length() - 5);
                        String ngayDKiVao = chiTietCaClass.getCt_VaoCa().substring(0, chiTietCaClass.getCt_VaoCa().length() - 6);
                        LocalTime gioBDDK = LocalTime.parse(gioDki);
                        long phutVao = funcTinhPhut(start, gioBDDK);
                        if(phutVao >= -5 && phutVao <= 5 && chiTietCaClass.getCt_Ngay().equals(ngayDKiVao)){
                            textView.setText("Hợp lệ");
                        }else {
                            textView.setText("Không hợp lệ");
                        }
                    }

                    if(!chiTietCaClass.getCt_RaCa().equals("") && act.equals("Ra")) {
                        String gioDki = chiTietCaClass.getCt_RaCa().substring(chiTietCaClass.getCt_RaCa().length() - 5);
                        String ngayDKiRa = chiTietCaClass.getCt_RaCa().substring(0, chiTietCaClass.getCt_RaCa().length() - 6);
                        LocalTime gioKTDK = LocalTime.parse(gioDki);
                        long phutRa = funcTinhPhut(end, gioKTDK);
                        if(phutRa >= -5 && phutRa <= 5 && chiTietCaClass.getCt_Ngay().equals(ngayDKiRa)){
                            textView.setText("Hợp lệ");
                        }else {
                            textView.setText("Không hợp lệ");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcHienThiTrangThaiCa(ChiTietCaClass chiTietCaClass, MyViewHolder holder) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.orderByChild("ca_Ma").equalTo(chiTietCaClass.getCa_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    LocalTime start = LocalTime.parse(caLamViecClass.getCa_GioDB());
                    LocalTime end = LocalTime.parse(caLamViecClass.getCa_GioKT());

                    if(!chiTietCaClass.getCt_VaoCa().equals("")){
                        String gioDki = chiTietCaClass.getCt_VaoCa().substring(chiTietCaClass.getCt_VaoCa().length() - 5);
                        String ngayDKiVao = chiTietCaClass.getCt_VaoCa().substring(0, chiTietCaClass.getCt_VaoCa().length() - 6);
                        LocalTime gioBDDK = LocalTime.parse(gioDki);
                        long phutVao = funcTinhPhut(start, gioBDDK);
                        if(phutVao >= -5 && phutVao <= 5 && chiTietCaClass.getCt_Ngay().equals(ngayDKiVao)){
                            holder.tvVao.setTextColor(context.getColor(R.color.dark_green));
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_green);
                            holder.tvVao.setBackground(drawable);
                        }else {
                            holder.tvVao.setTextColor(Color.RED);
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_red);
                            holder.tvVao.setBackground(drawable);
                        }
                    }

                    if(!chiTietCaClass.getCt_RaCa().equals("")) {
                        String gioDki = chiTietCaClass.getCt_RaCa().substring(chiTietCaClass.getCt_RaCa().length() - 5);
                        String ngayDKiRa = chiTietCaClass.getCt_RaCa().substring(0, chiTietCaClass.getCt_RaCa().length() - 6);
                        LocalTime gioKTDK = LocalTime.parse(gioDki);
                        long phutRa = funcTinhPhut(end, gioKTDK);
                        if(phutRa >= -5 && phutRa <= 5 && chiTietCaClass.getCt_Ngay().equals(ngayDKiRa)){
                            holder.tvRa.setTextColor(context.getColor(R.color.dark_green));
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_green);
                            holder.tvRa.setBackground(drawable);
                        }else {
                            holder.tvRa.setTextColor(Color.RED);
                            Drawable drawable = ContextCompat.getDrawable(context, R.drawable.border8_red);
                            holder.tvRa.setBackground(drawable);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public static long funcTinhPhut(LocalTime startTime, LocalTime endTime) {
        Duration duration = Duration.between(startTime, endTime);
        return duration.toMinutes();
    }

    private void funcHienThiTen(ChiTietCaClass chiTietCaClass, MyViewHolder holder) {
        DatabaseReference dataKhuVuc = FirebaseDatabase.getInstance().getReference("KhuVuc");
        DatabaseReference dataCa = FirebaseDatabase.getInstance().getReference("CaLamViec");
        dataCa.orderByChild("ca_Ma").equalTo(chiTietCaClass.getCa_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    holder.tvCa.setText(caLamViecClass.getCa_Ten());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        dataKhuVuc.orderByChild("kv_Ma").equalTo(chiTietCaClass.getKv_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    holder.tvKhuVuc.setText(khuVucClass.getKv_Ten());
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
        TextView tvKhuVuc, tvCa, tvVao, tvRa;
        LinearLayout lnCa;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvKhuVuc = itemView.findViewById(R.id.tvItemDSCaNVKhuVuc);
            tvCa = itemView.findViewById(R.id.tvItemDSCaNVCa);
            tvVao = itemView.findViewById(R.id.tvItemDSCaNVVao);
            tvRa = itemView.findViewById(R.id.tvItemDSCaNVRa);
            lnCa = itemView.findViewById(R.id.lnItemDSCaNV);
        }
    }
}
