package com.example.quanlyquanan0609.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlyquanan0609.Class.CaLamViecClass;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.DangKyCaClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Listener.HienThiChiTietCaListener;
import com.example.quanlyquanan0609.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HienThiChiTietCaAdapter extends RecyclerView.Adapter<HienThiChiTietCaAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<CaLamViecClass> list;
    String kvTen, ngay, acNguon;

    public HienThiChiTietCaAdapter(Context context, ArrayList<CaLamViecClass> list, String kvTen, String ngay, String acNguon) {
        this.context = context;
        this.list = list;
        this.kvTen = kvTen;
        this.ngay = ngay;
        this.acNguon = acNguon;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hien_thi_list_ca, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CaLamViecClass caLamViecClass = list.get(position);
        holder.tvTenCa.setText(caLamViecClass.getCa_Ten());
        holder.tvGio.setText("(Bắt đầu: " + caLamViecClass.getCa_GioDB() + " ~ " + caLamViecClass.getCa_GioKT() + ")");
        holder.tvKhuVuc.setText(kvTen);
        holder.tvNgay.setText("Ngày: " + ngay);
        funcLayKhuVucTheoTenHienThiSoLuong(caLamViecClass, holder, kvTen, ngay);
        funcMaKhuVuc(holder, caLamViecClass, kvTen, ngay, acNguon);
        if(acNguon.equals("HienThi")){
            holder.tvDangKy.setVisibility(View.GONE);
        }
        holder.lnCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(acNguon.equals("HienThi")){
                    funcTaoDialogHienThiThongTin(caLamViecClass, ngay, kvTen);
                }else {
                    String khuVuc = holder.tvKhuVuc.getText().toString();
                    funcKiemTraDangKi(caLamViecClass, khuVuc, ngay);
                }

            }
        });

    }
    //Hien thi

    private void funcTaoDialogHienThiThongTin(CaLamViecClass caLamViecClass, String ngay, String kvTen) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_hien_thi_chi_tiet_ca_dki, null);
        TextView tvNgay, tvKhuVuc, tvCa, tvGio, tvSoLuong;
        RecyclerView rvListDangKy;
        Button btnOK;
        tvNgay = view.findViewById(R.id.tvHTCTCNgay);
        tvKhuVuc = view.findViewById(R.id.tvHTCTCKhuVuc);
        tvCa = view.findViewById(R.id.tvHTCTCCa);
        tvGio = view.findViewById(R.id.tvHTCTCGio);
        tvSoLuong = view.findViewById(R.id.tvHTCTCSoLuong);
        rvListDangKy = view.findViewById(R.id.rvListChiTietCaDki);
        btnOK = view.findViewById(R.id.btnOk);

        tvNgay.setText(ngay);
        tvKhuVuc.setText(kvTen);
        tvCa.setText(caLamViecClass.getCa_Ten());
        tvGio.setText(caLamViecClass.getCa_GioDB() + " ~ " + caLamViecClass.getCa_GioKT());
        funcLayKhuVucMa(caLamViecClass, rvListDangKy, tvSoLuong, ngay, kvTen);
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

    private void funcLayKhuVucMa(CaLamViecClass caLamViecClass, RecyclerView rvListDangKy, TextView tvSoLuong, String ngay, String kvTen) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ten").equalTo(kvTen).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kvMa = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    kvMa = khuVucClass.getKv_Ma();
                }
                funcHienThiDanhSachDangKy(caLamViecClass, rvListDangKy, tvSoLuong, ngay, kvMa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiDanhSachDangKy(CaLamViecClass caLamViecClass, RecyclerView rvListDangKy, TextView tvSoLuong, String ngay, Integer kvMa) {
        rvListDangKy.setHasFixedSize(true);
        rvListDangKy.setLayoutManager(new LinearLayoutManager(context));
        ArrayList<ChiTietCaClass> listDangKy = new ArrayList<>();
        HienThiListDKiAdapter hienThiListDKiAdapter = new HienThiListDKiAdapter(context, listDangKy);
        rvListDangKy.setAdapter(hienThiListDKiAdapter);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        databaseReference.orderByChild("ca_Ma").equalTo(caLamViecClass.getCa_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer soLuong = 0;
                for ( DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietCaClass chiTietCaClass = dataSnapshot.getValue(ChiTietCaClass.class);
                    if(chiTietCaClass.getCt_Ngay().equals(ngay) && chiTietCaClass.getKv_Ma().equals(kvMa)){
                        listDangKy.add(chiTietCaClass);
                        soLuong = soLuong + 1;
                    }
                }
                hienThiListDKiAdapter.notifyDataSetChanged();
                tvSoLuong.setText("Nhân viên ("+soLuong+"/"+caLamViecClass.getCa_SoLuong()+"):");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // Dang ky
    private void funcMaKhuVuc(MyViewHolder holder, CaLamViecClass caLamViecClass, String tenKhuVuc, String ngay, String acNguon) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ten").equalTo(tenKhuVuc).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kvMa = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    kvMa = khuVucClass.getKv_Ma();
                }
                funcHienThiDangKi(holder, caLamViecClass, kvMa, ngay, acNguon);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcHienThiDangKi(HienThiChiTietCaAdapter.MyViewHolder holder, CaLamViecClass caLamViecClass, Integer kvMa, String ngay, String acNguon) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        Query query = databaseReference.orderByChild("ca_Ma").equalTo(caLamViecClass.getCa_Ma());
        ValueEventListener listener = new HienThiChiTietCaListener(holder, caLamViecClass, kvMa, ngay, acNguon, context);
        query.addValueEventListener(listener);
    }
    private void funcKiemTraDangKi(CaLamViecClass caLamViecClass, String tenKhuVuc, String ngay) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        databaseReference.orderByChild("nd_Ma").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean daDangKi = false;
                Integer kvMaDangKy = 0;
                String ngayDangKy = "";
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietCaClass chiTietCaClass = dataSnapshot.getValue(ChiTietCaClass.class);
                    if(caLamViecClass.getCa_Ma().equals(chiTietCaClass.getCa_Ma()) && ngay.equals(chiTietCaClass.getCt_Ngay())){
                        daDangKi = true;
                        kvMaDangKy = chiTietCaClass.getKv_Ma();
                        ngayDangKy = chiTietCaClass.getCt_Ngay();
                        break;
                    }
                }
                if (daDangKi){
                    funcLayKhuVucTheoMa(caLamViecClass, tenKhuVuc, kvMaDangKy, ngayDangKy);
                }else {
                    funcTaoDialogDangKyCa(caLamViecClass, tenKhuVuc, ngay);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayKhuVucTheoMa(CaLamViecClass caLamViecClass, String tenKhuVuc, Integer kvMaDaDangKy, String ngayDangKy) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMaDaDangKy).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String kvTenDaDangKy = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    kvTenDaDangKy = khuVucClass.getKv_Ten();
                }
                if(kvTenDaDangKy != null){
                    funcTaoDialogXacNhanDangKi(caLamViecClass, tenKhuVuc, kvTenDaDangKy, ngayDangKy);
                }else {
                    Toast.makeText(context, "Lỗi !!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTaoDialogXacNhanDangKi(CaLamViecClass caLamViecClass, String tenKhuVucMoi, String kvTenDaDangKy, String ngayDangKy) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNoiDung;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        btnHuy.setVisibility(View.GONE);
        tvTitle.setText("Lưu ý");
        tvNoiDung.setText("Bạn đã đăng ký "+ caLamViecClass.getCa_Ten().toLowerCase() +" tại "+ kvTenDaDangKy.toLowerCase() + ", ngày " + ngayDangKy + ". Nên bạn không đăng ký " + caLamViecClass.getCa_Ten().toLowerCase() + " tại " + tenKhuVucMoi.toLowerCase() + ", ngày " + ngayDangKy + ".");
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void funcTaoDialogDangKyCa(CaLamViecClass caLamViecClass, String tenKhuVuc, String ngay) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNoiDung;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        tvTitle.setText("Đăng ký ca");
        tvNoiDung.setText("Xác nhận đăng ký "+ caLamViecClass.getCa_Ten().toLowerCase() +", "+ tenKhuVuc.toLowerCase() + ", ngày " + ngay + " ?");
        builder.setView(view);
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
                funcLayKhuVucTheoTen(caLamViecClass, tenKhuVuc, ngay, alertDialog);
            }
        });
        alertDialog.show();
    }

    private void funcLayKhuVucTheoTen(CaLamViecClass caLamViecClass, String tenKhuVuc, String ngay, AlertDialog alertDialog) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ten").equalTo(tenKhuVuc).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kvMa = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    kvMa = khuVucClass.getKv_Ma();
                }
                funcDangKyCaLamViec(caLamViecClass, kvMa, ngay, alertDialog);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayKhuVucTheoTenHienThiSoLuong(CaLamViecClass caLamViecClass, MyViewHolder holder, String kvTen, String ngay) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ten").equalTo(kvTen).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kvMa = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    kvMa = khuVucClass.getKv_Ma();
                }
                funcHienThiSoLuong(caLamViecClass, holder, kvMa, ngay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcDangKyCaLamViec(CaLamViecClass caLamViecClass, Integer kvMa, String ngay, AlertDialog alertDialog) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        ChiTietCaClass chiTietCaClass = new ChiTietCaClass(caLamViecClass.getCa_Ma(), kvMa, ngay, uId, "", "");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        String key = ngay.replaceAll("/", "") + kvMa + caLamViecClass.getCa_Ma() + uId.substring(uId.length()-3);
        databaseReference.child(key).setValue(chiTietCaClass).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }else {
                    try {
                        throw task.getException();
                    }catch (Exception e){
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void funcHienThiSoLuong(CaLamViecClass caLamViecClass, HienThiChiTietCaAdapter.MyViewHolder holder, Integer kvMa, String ngay) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        databaseReference.orderByChild("ca_Ma").equalTo(caLamViecClass.getCa_Ma()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer soLuong = 0;
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String uId = firebaseUser.getUid();
                boolean daDangKy = false;
                ChiTietCaClass chiTietCaClass = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    chiTietCaClass = dataSnapshot.getValue(ChiTietCaClass.class);
                    if (chiTietCaClass.getKv_Ma().equals(kvMa) && chiTietCaClass.getCt_Ngay().equals(ngay)) {
                        soLuong = soLuong + 1;
                    }
                    if(chiTietCaClass.getNd_Ma().equals(uId) && chiTietCaClass.getKv_Ma().equals(kvMa) && chiTietCaClass.getCt_Ngay().equals(ngay)){
                        daDangKy = true;
                    }

                }
                holder.tvSl.setText("SL: "+ soLuong+"/" + caLamViecClass.getCa_SoLuong());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    // End Dang ky


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        public LinearLayout lnCa;
        public TextView tvKhuVuc, tvNgay, tvTenCa, tvGio, tvSl, tvDangKy;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            lnCa = itemView.findViewById(R.id.lnItemListCa);
            tvKhuVuc = itemView.findViewById(R.id.tvItemListCaKhuVuc);
            tvNgay = itemView.findViewById(R.id.tvItemListCaNgay);
            tvTenCa = itemView.findViewById(R.id.tvItemListCaTen);
            tvGio = itemView.findViewById(R.id.tvItemListCaGio);
            tvSl = itemView.findViewById(R.id.tvItemListCaSL);
            tvDangKy = itemView.findViewById(R.id.tvItemListCaDangKy);
        }
    }

}
