package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.HoaDonAdapter;
import com.example.quanlyquanan0609.Adapter.KhuyenMaiAdapter;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.ChiTietPGMClass;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.KhuyenMaiClass;
import com.example.quanlyquanan0609.Class.NgayClass;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.ZaloPay.CreateOrder;
import com.example.quanlyquanan0609.databinding.ActivityHoaDonBinding;
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

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

//import vn.zalopay.sdk.Environment;
//import vn.zalopay.sdk.ZaloPayError;
//import vn.zalopay.sdk.ZaloPaySDK;
//import vn.zalopay.sdk.listeners.PayOrderListener;

public class HoaDonActivity extends AppCompatActivity {
    ActivityHoaDonBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    HoaDonAdapter hoaDonAdapter;
    ArrayList<ChiTietPGMClass> listChiTiet;
    RecyclerView recyclerView, rvKhuyenMai;
    KhuyenMaiAdapter khuyenMaiAdapter;
    ArrayList<KhuyenMaiClass> listKm;
    Integer pgmMa, banMa;
    NumberFormat numberFormat;
    Integer soLuong = 0;
    String mNgayVao, nguoiDungMa, hdNgayLap, ac;
    String quyen = null;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHoaDonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        funcHienThiButton();
        toolbar = binding.toolbar;
        progressBar = binding.progressBar;
        numberFormat = new DecimalFormat("#,###.##");
        // ZLPay
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // ZaloPay SDK Init
//        ZaloPaySDK.init(553, Environment.SANDBOX);

        //
        setSupportActionBar(toolbar);
        pgmMa = getIntent().getIntExtra("maPGM", 0);
        banMa = getIntent().getIntExtra("maBan", 0);
        ac = getIntent().getStringExtra("ac");
        if(ac.equals("ThongKe")){
            binding.lnHDBtn.setVisibility(View.GONE);
        }
        funcLayNguoiDungTheoPgm(pgmMa);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.tvHDTTThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcHienThiBotDialogThongTin(pgmMa);
            }
        });
        binding.btnHDThanhToan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quyen.equals("Khách hàng")){
//                    funcTaoDialogHinhThuc();
                    funcHienThiBotDialogThanhToan();
                }else {
                    funcTaoDialogXacNhan("thanhToan");

                }
            }
        });
        binding.btnHDThanhToanIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogXacNhan("in");
            }
        });
        binding.tvHDMucGiamGia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogHienThiKhuyenMai();
            }
        });
        funcHienThiDanhSach(pgmMa);
        funcLayHdNgay(pgmMa, "hienThi");
        funcTextChanged();
    }

    private void funcHienThiBotDialogThanhToan() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.bot_dialog_thanh_toan, null);
        TextView tvTienMat, tvZalo;
        tvTienMat = view.findViewById(R.id.sdTTTienMat);
        tvZalo = view.findViewById(R.id.sdTTZalo);
        bottomSheetDialog.setContentView(view);
        tvTienMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcCapNhatTrangThaiPgm(pgmMa);
                bottomSheetDialog.dismiss();
            }
        });
        tvZalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogInHd();
                bottomSheetDialog.dismiss();
            }
        });
        bottomSheetDialog.show();
    }


    private void funcTaoDialogHinhThuc() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_hinh_thuc_thanh_toan, null);
        Button btnZlPay, btnTienMat;
        btnZlPay = view.findViewById(R.id.btnZlPay);
        btnTienMat = view.findViewById(R.id.btnTienMat);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        btnZlPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDialogInHd();
                alertDialog.dismiss();
            }
        });
        btnTienMat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                funcCapNhatTrangThaiPgm(pgmMa);
            }
        });
        alertDialog.show();
    }

    private void funcCapNhatTrangThaiPgm(Integer pgmMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.child(String.valueOf(pgmMa)).child("pgm_TrangThai").setValue("2").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    funcTaoDialogCho();
                }
            }
        });
    }

    private void funcTaoDialogCho() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_progess, null);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    String trangThai = pgmClass.getPgm_TrangThai();
                    if (trangThai.equals("0")) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(HoaDonActivity.this, KhachHangActivity.class);
                        Toast.makeText(HoaDonActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        alertDialog.show();
    }

    private void funcTaoDialogInHd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_0_edit_text, null);
        TextView tvTitle, tvNoiDung;
        Button btnOk, btnHuy;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnOk = view.findViewById(R.id.btnOk);
        btnHuy = view.findViewById(R.id.btnHuy);
        tvTitle.setText("Lưu hóa đơn");
        tvNoiDung.setText("Bạn có muốn lưu hóa đơn không ?");
        btnOk.setText("Có");
        btnOk.setAllCaps(false);
        btnHuy.setText("Không");
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcLayMaHoaDon(pgmMa, alertDialog, "ThanhToan");
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                funcLayMaHoaDon(pgmMa, alertDialog, "In");            }
        });
        alertDialog.show();
    }

    private void funcLayMaHoaDon(Integer pgmMa, AlertDialog alertDialog, String actThanhToan) {
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("HoaDon");
        databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer maHD = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    maHD = hoaDonClass.getHd_Ma();
                }
                funcLayBanPGM(maHD, banMa, alertDialog, actThanhToan);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayBanPGM(Integer maHD, Integer banMa, AlertDialog alertDialog, String actThanhToan) {
        DatabaseReference databaseReference  = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("ban_Ma").equalTo(banMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String banTen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    banTen = banAnClass.getBan_Ten();
                }
                progressBar.setVisibility(View.GONE);
//                funcZaLoPay(maHD, banTen, alertDialog, actThanhToan);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    private void funcZaLoPay(Integer maHD, String banTen, AlertDialog alertDialog, String actThanhToan) {
//        String ngay = funcLayNgay();
//        String hdTongTien = funcLaySoTuChuoi(binding.tvHDThanhToan.getText().toString());
//        CreateOrder orderApi = new CreateOrder();
//        try {
//            JSONObject data = orderApi.createOrder(hdTongTien, String.valueOf(maHD), ngay, banTen);
//            String code = data.getString("returncode");
//            Log.d("GD", "code: " + code);
//            if (code.equals("1")) {
//                String token = data.getString("zptranstoken");
//                Log.d("Token", token);
//                ZaloPaySDK.getInstance().payOrder(HoaDonActivity.this, token, "demozpdk://app", new PayOrderListener() {
//                    @Override
//                    public void onPaymentSucceeded(String s, String s1, String s2) {
//                        Toast.makeText(HoaDonActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
//                        funcThanhToan(pgmMa, actThanhToan, "Chuyển khoản");
//                        alertDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onPaymentCanceled(String s, String s1) {
//                        Toast.makeText(HoaDonActivity.this, "Hủy thanh toán", Toast.LENGTH_SHORT).show();
//                        alertDialog.dismiss();
//                    }
//
//                    @Override
//                    public void onPaymentError(ZaloPayError zaloPayError, String s, String s1) {
//                        Toast.makeText(HoaDonActivity.this, "Lỗi !! Vui lòng thử lại", Toast.LENGTH_SHORT).show();
//                        alertDialog.dismiss();
//                    }
//                });
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        ZaloPaySDK.getInstance().onResult(intent);
//    }

    private void funcHienThiButton() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByKey().equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    quyen = nguoiDungClass.getNd_Quyen();
                }
                if(quyen.equals("Khách hàng")){
                    binding.btnHDThanhToanIn.setVisibility(View.GONE);
                    binding.btnHDThanhToan.setBackgroundColor(getColor(R.color.bg_color));
                    binding.btnHDThanhToan.setTextColor(Color.WHITE);
//                    binding.lnTienKhachDua.setVisibility(View.GONE);
//                    binding.lnTienThoi.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTaoDialogHienThiKhuyenMai() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_hien_thi_km, null);
        listKm = new ArrayList<>();
        RecyclerView rvKm = view.findViewById(R.id.rvKhuyenMai);
        Button btnOK = view.findViewById(R.id.btnOk);
        LinearLayout lnDiemTl = view.findViewById(R.id.lnDiemTl);
        TextView tvHang, tvPhanTram;
        tvHang = view.findViewById(R.id.tvHangKH);
        tvPhanTram = view.findViewById(R.id.tvPhanTramKm);
        funcHienThiHangKH(lnDiemTl, tvHang, tvPhanTram);
        rvKm.setHasFixedSize(true);
        rvKm.setLayoutManager(new LinearLayoutManager(this));
        khuyenMaiAdapter = new KhuyenMaiAdapter(this, listKm);
        rvKm.setAdapter(khuyenMaiAdapter);
        Integer tien = Integer.valueOf(funcLaySoTuChuoi(binding.tvHDCongTien.getText().toString()));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuyenMai");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listKm != null){
                    listKm.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuyenMaiClass khuyenMaiClass = dataSnapshot.getValue(KhuyenMaiClass.class);

                    try {
                        Date hdNgay = format.parse(hdNgayLap);
                        Date kmNgayBd = format.parse(khuyenMaiClass.getKm_NgayBd());
                        Date kmNgayKt = format.parse(khuyenMaiClass.getKm_NgayKt());
                        if(hdNgay.equals(kmNgayBd) || hdNgay.equals(kmNgayKt) || hdNgay.after(kmNgayBd) && hdNgay.before(kmNgayKt)){
                            if(tien >= khuyenMaiClass.getKm_Muc()){
                                listKm.add(khuyenMaiClass);
                            }

                        }
                    }catch (ParseException e){

                    }
                }
                khuyenMaiAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void funcHienThiHangKH(LinearLayout lnDiemTl, TextView tvHang, TextView tvPhanTram) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByKey().equalTo(nguoiDungMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hang = null;
                Integer diemTl = 0;
                Integer km = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    diemTl = nguoiDungClass.getNd_DiemTL();
                }
                if(diemTl>=100 && diemTl <300){          // KH B1
                    km = 1;
                    hang = "KH bậc 1 (ĐTL: " + diemTl + ")";
                }else if(diemTl>=300 && diemTl<500){    // KH B2
                    km =3;
                    hang = "KH bậc 2 (ĐTL: " + diemTl + ")";
                }else if(diemTl>=500 && diemTl<700){    // KH B3
                    km =5;
                    hang = "KH bậc 3 (ĐTL: " + diemTl + ")";
                }else if(diemTl>=700 && diemTl<1000){    // KH B4
                    km = 7;
                    hang = "KH bậc 4 (ĐTL: " + diemTl + ")";
                }else if(diemTl>1000){    // KH B5
                    km =10;
                    hang = "KH bậc 5 (ĐTL: " + diemTl + ")";
                }
                if(km==0){
                    lnDiemTl.setVisibility(View.GONE);
                }else{
                    tvHang.setText(hang);
                    tvPhanTram.setText(km + "%");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTaoDialogXacNhan(String act){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_0_edit_text, null);
        builder.setView(view).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        TextView tvTitle, tvNoiDung;
        Button btnHuy, btnOk;
        tvTitle = view.findViewById(R.id.tvTitle);
        tvNoiDung = view.findViewById(R.id.tvTextView);
        btnHuy = view.findViewById(R.id.btnHuy);
        btnOk = view.findViewById(R.id.btnOk);
        tvTitle.setText("Thanh toán");
        tvNoiDung.setText("Xác nhận thanh toán hóa đơn này ?");
        btnHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(act.equals("thanhToan")){
                    funcThanhToan(pgmMa, "ThanhToan", "Tiền mặt");
                }else if(act.equals("in")){
                    funcThanhToan(pgmMa, "In", "Tiền mặt");
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }
    private void funcThanhToan(Integer pgmMa, String act, String hinhThuc) {
        String hdHinhThuc = hinhThuc;
        String hdGioRa = funcLayGio();
        String hdTongTien = funcLaySoTuChuoi(binding.tvHDThanhToan.getText().toString());
        // Cap nhat hoa don
        funclayHoaDonTheoPgm(pgmMa, hdHinhThuc, hdGioRa, Integer.valueOf(hdTongTien), act);
        // Cong diem tich luy
        funcLayMaNd(pgmMa, "CongDiem", 0);
        // Cap nhat PGM
        DatabaseReference dataPgm = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        dataPgm.child(String.valueOf(pgmMa)).child("pgm_TrangThai").setValue("0");
        if(act.equals("ThanhToan")){
            Toast.makeText(HoaDonActivity.this, "Thanh toán thành công", Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
        }

    }

    private void funcLayMaKhuVuc(Integer banMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("ban_Ma").equalTo(banMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kvMa = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    kvMa = banAnClass.getKv_Ma();
                }
                funcLayBanTheoKhuVuc(kvMa);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayBanTheoKhuVuc(Integer kvMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banAnClass = dataSnapshot.getValue(BanAnClass.class);
                    funcKiemTraBan(kvMa, banAnClass.getBan_Ma());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcKiemTraBan(Integer kvMa, Integer banMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("ban_Ma").equalTo(banMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean coKhach = false;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    if(pgmClass.getPgm_TrangThai().equals("1")){
                        coKhach = true;
                        break;
                    }
                }
                if(!coKhach){
                    funcCapNhatKhuVuc(kvMa, "Hiển thị");
                }else {
                    funcCapNhatKhuVuc(kvMa, "Có khách");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void funcCapNhatKhuVuc(Integer kvMa, String trangThai) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    if(!khuVucClass.getKv_TrangThai().equals("Hiển thị")){
                        databaseReference.child(String.valueOf(kvMa)).child("kv_TrangThai").setValue(trangThai);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayMaNd(Integer pgmMa, String act, Integer tienKm) {
        DatabaseReference dataPGM = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        dataPGM.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String uId = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    uId = pgmClass.getNd_Ma();
                }
                if(act.equals("CongDiem")){
                    funcCongDiemTL(uId);
                }else if(act.equals("LayDiemTl")){
                    funcLayDiemTL(uId, tienKm);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcLayNguoiDungTheoPgm(Integer pgmMa){
        DatabaseReference dataPGM = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        dataPGM.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    nguoiDungMa = pgmClass.getNd_Ma();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcCongDiemTL(String uId) {
        DatabaseReference dataNguoiDung = FirebaseDatabase.getInstance().getReference("NguoiDung");
        dataNguoiDung.orderByKey().equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Double tinhDiem = 0.0;
                Double diemTL = 0.0;
                Double diemHienTai = 0.0;
                String quyen = null;
                Double giaHd = Double.valueOf(funcLaySoTuChuoi(binding.tvHDThanhToan.getText().toString()));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    diemHienTai = Double.valueOf(nguoiDungClass.getNd_DiemTL());
                    quyen = nguoiDungClass.getNd_Quyen();
                }
                tinhDiem = giaHd/10000;
                Double phanDu = tinhDiem%1;
                if(phanDu>= 0.5){
                    tinhDiem = Math.ceil(tinhDiem);
                }else {
                    tinhDiem = Math.floor(tinhDiem);
                }
                diemTL = diemHienTai + tinhDiem;
                if(quyen.equals("Khách hàng")){
                    dataNguoiDung.child(uId).child("nd_DiemTL").setValue(diemTL);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funclayHoaDonTheoPgm(Integer pgmMa, String hdHinhThuc, String hdGioRa, Integer hdTongTien, String act) {
        DatabaseReference dataHoaDon = FirebaseDatabase.getInstance().getReference("HoaDon");
        dataHoaDon.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer hdMa = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    hdMa = hoaDonClass.getHd_Ma();
                }
                if(hdMa != 0){
                    funcCapNhatHoaDon(hdMa, hdHinhThuc, hdGioRa, hdTongTien, act);
                }else {
                    Toast.makeText(HoaDonActivity.this, "Hoa don khong ton tai", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcCapNhatHoaDon(Integer hdMa, String hdHinhThuc, String hdGioRa, Integer hdTongTien, String act) {
        DatabaseReference dataHoaDon = FirebaseDatabase.getInstance().getReference("HoaDon");
        dataHoaDon.child(String.valueOf(hdMa)).child("hd_GioRa").setValue(hdGioRa);
        dataHoaDon.child(String.valueOf(hdMa)).child("hd_HinhThuc").setValue(hdHinhThuc);
        dataHoaDon.child(String.valueOf(hdMa)).child("hd_TongTien").setValue(hdTongTien);
        dataHoaDon.child(String.valueOf(hdMa)).child("hd_TrangThai").setValue("0");
        if(act.equals("In")) {
            Intent intent = new Intent(HoaDonActivity.this, XuatHoaDonActivity.class);
            intent.putExtra("pgmMa", pgmMa);
            intent.putExtra("banMa", banMa);
            intent.putExtra("soLuong", soLuong);
            intent.putExtra("tongTien", binding.tvHDCongTien.getText().toString());
            intent.putExtra("giamGia", binding.tvHDGiamGia.getText().toString());
            startActivity(intent);
        }

    }
    private void funcTextChanged() {
        binding.tvHDCongTien.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvHDTongTien.setText(binding.tvHDCongTien.getText().toString());
            }
        });
        binding.tvHDMucGiamGia.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Integer phamTram = Integer.valueOf(funcLaySoTuChuoi(binding.tvHDMucGiamGia.getText().toString()));
                Integer congTien = Integer.valueOf(funcLaySoTuChuoi(binding.tvHDCongTien.getText().toString()));
                Integer tienGiam =(congTien*phamTram)/100;
                Integer conLai = congTien - tienGiam;
                if(phamTram == 0){
                    binding.tvHDGiamGia.setText("0");
                }else {
                    binding.tvHDGiamGia.setText("- " + numberFormat.format(tienGiam));
                }
                binding.tvHDTongTien.setText(numberFormat.format(conLai));
            }
        });
        binding.tvHDTongTien.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.tvHDThanhToan.setText(binding.tvHDTongTien.getText().toString());
            }
        });

//        binding.edHDKhachDua.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
//        binding.edHDKhachDua.addTextChangedListener(new TextWatcher() {
//            private String current = "";
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (s.length() > 0 && !s.toString().equals(current)) {
//                    binding.edHDKhachDua.removeTextChangedListener(this);
//
//                    String cleanString = s.toString().replaceAll("[,.]", "");
//
//                    try {
//                        double parsed = Double.parseDouble(cleanString);
//                        String formatted = NumberFormat.getInstance().format(parsed);
//
//                        current = formatted;
//                        binding.edHDKhachDua.setText(formatted);
//                        binding.edHDKhachDua.setSelection(formatted.length());
//                    } catch (NumberFormatException e) {
//                        // Xử lý ngoại lệ nếu văn bản không thể chuyển đổi thành số
//                    }
//
//                    binding.edHDKhachDua.addTextChangedListener(this);
//                } else {
//                    current = "";
//                }
//
//                String tongTien = funcLaySoTuChuoi(binding.tvHDTongTien.getText().toString());
//                String khachDua = funcLaySoTuChuoi(binding.edHDKhachDua.getText().toString());
//                if(!khachDua.equals("")){
//                    Integer tienThoi = 0;
//                    if(Integer.valueOf(khachDua) > Integer.valueOf(tongTien)){
//                        tienThoi = Integer.valueOf(khachDua) - Integer.valueOf(tongTien);
//                    }
//                    binding.tvHDTienThoi.setText(numberFormat.format(tienThoi));
//                }else {
//                    binding.tvHDTienThoi.setText("0");
//                }
//
//            }
//        });
    }
    private void funcHienThiBotDialogThongTin(Integer pgmMa) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this, com.google.android.material.R.style.Theme_Design_Light_BottomSheetDialog);
        View view = LayoutInflater.from(this).inflate(R.layout.bot_dialog_hoa_don, null);
        bottomSheetDialog.setContentView(view);
        TextView tvMa, tvBan, tvNgay, tvNhanVien, tvKhachHang, tvThanhToan;
        LinearLayout lnNhanVien, lnThanhToan;
        tvMa = view.findViewById(R.id.tvBotHDMa);
        tvBan = view.findViewById(R.id.tvBotHDBan);
        tvNgay = view.findViewById(R.id.tvBotHDNgay);
        tvNhanVien = view.findViewById(R.id.tvBotHDNhanVien);
        tvKhachHang = view.findViewById(R.id.tvBotHDKhachHang);
        tvThanhToan = view.findViewById(R.id.tvBotHDThanhToan);
        lnNhanVien = view.findViewById(R.id.lnBotHDNhanVien);
        lnThanhToan = view.findViewById(R.id.lnBotHDThanhToan);
        DatabaseReference dataHoaDon = FirebaseDatabase.getInstance().getReference("HoaDon");
        dataHoaDon.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    tvMa.setText("HD" + hoaDonClass.getHd_Ma());
                    tvNgay.setText(hoaDonClass.getHd_Ngay());
                    if(ac.equals("ThongKe")){
                        lnThanhToan.setVisibility(View.VISIBLE);
                        tvThanhToan.setText(hoaDonClass.getHd_HinhThuc());
                    }
                }
                mNgayVao = tvNgay.getText().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        funcLayTenBan(tvBan, banMa);
        funcLayUId(pgmMa, tvKhachHang, tvNhanVien, lnNhanVien);
        bottomSheetDialog.show();
    }
    private void funcLayUId(Integer pgmMa, TextView tvKhachHang, TextView tvNhanVien, LinearLayout lnNhanVien) {
        DatabaseReference dataPGM = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        dataPGM.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    funcLayNguoiDung(pgmClass.getNd_Ma(), tvKhachHang, tvNhanVien, lnNhanVien);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcLayNguoiDung(String ndMa, TextView tvKhachHang, TextView tvNhanVien, LinearLayout lnNhanVien) {
        DatabaseReference dataNguoiDung = FirebaseDatabase.getInstance().getReference("NguoiDung");
        dataNguoiDung.orderByKey().equalTo(ndMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    if(!nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        tvKhachHang.setText("Khách lẻ");
                        tvNhanVien.setText(nguoiDungClass.getNd_Ten());
                    }else {
                        tvKhachHang.setText(nguoiDungClass.getNd_Ten());
                        lnNhanVien.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    private void funcLayTenBan(TextView tvBan, Integer banMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("BanAn");
        databaseReference.orderByChild("ban_Ma").equalTo(banMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banClass = dataSnapshot.getValue(BanAnClass.class);
                    funcLayKhuVucTheoMaBan(banClass.getKv_Ma(), banClass.getBan_Ten(), tvBan);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcLayKhuVucTheoMaBan(Integer kvMa, String banTen, TextView tvBan) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    tvBan.setText(khuVucClass.getKv_Ten() + " - " + banTen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcHienThiDanhSach(Integer pgmMa) {
        recyclerView = binding.rvHoaDon;
        listChiTiet = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        hoaDonAdapter = new HoaDonAdapter(this, listChiTiet);
        recyclerView.setAdapter(hoaDonAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listChiTiet != null){
                    listChiTiet.clear();
                }
                Integer tongTien = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietPGMClass chiTietPGMClass = dataSnapshot.getValue(ChiTietPGMClass.class);
                    tongTien = tongTien + (chiTietPGMClass.getCt_DonGia() * chiTietPGMClass.getCt_SoLuong());
                    soLuong = soLuong + chiTietPGMClass.getCt_SoLuong();
                    listChiTiet.add(chiTietPGMClass);
                }
                binding.tvHDCongTien.setText(numberFormat.format(tongTien));
                funcLayHdNgay(pgmMa , "LayNgay");
                hoaDonAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcLayHdNgay(Integer pgmMa, String act){
        DatabaseReference dataHoaDon = FirebaseDatabase.getInstance().getReference("HoaDon");
        dataHoaDon.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String hdNgay = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    hdNgay = hoaDonClass.getHd_Ngay();
                }
                if(act.equals("LayNgay")){
                    funcLayKhuyenMai(hdNgay);
                }else if(act.equals("hienThi")){
                    hdNgayLap = hdNgay;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcLayKhuyenMai(String ngay){
        Integer tien = Integer.valueOf(funcLaySoTuChuoi(binding.tvHDCongTien.getText().toString()));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuyenMai");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer tongKm = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuyenMaiClass khuyenMaiClass = dataSnapshot.getValue(KhuyenMaiClass.class);

                    try {
                        Date hdNgay = format.parse(ngay);
                        Date kmNgayBd = format.parse(khuyenMaiClass.getKm_NgayBd());
                        Date kmNgayKt = format.parse(khuyenMaiClass.getKm_NgayKt());
                        if(hdNgay.equals(kmNgayBd) || hdNgay.equals(kmNgayKt) || hdNgay.after(kmNgayBd) && hdNgay.before(kmNgayKt)){
                            if(tien >= khuyenMaiClass.getKm_Muc()){
                                tongKm = tongKm + khuyenMaiClass.getKm_PhanTram();
                            }

                        }
                    }catch (ParseException e){

                    }
                }
                funcLayMaNd(pgmMa, "LayDiemTl", tongKm);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void funcLayDiemTL(String uId, Integer tongKm) {

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.orderByKey().equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer diemTl = 0;
                Integer tien = tongKm;
                String quyen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    diemTl = nguoiDungClass.getNd_DiemTL();
                    quyen = nguoiDungClass.getNd_Quyen();
                }

                if(!quyen.equals("Khách hàng")){
                    binding.tvHDMucGiamGia.setText(tien + "%");
                }else {
                    if(diemTl>=100 && diemTl <300){          // KH B1
                        tien += 1;
                    }else if(diemTl>=300 && diemTl<500){    // KH B2
                        tien +=3;
                    }else if(diemTl>=500 && diemTl<700){    // KH B3
                        tien +=5;
                    }else if(diemTl>=700 && diemTl<1000){    // KH B4
                        tien +=7;
                    }else if(diemTl>1000){    // KH B5
                        tien +=10;
                    }
                    binding.tvHDMucGiamGia.setText(tien + "%");
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public String funcLaySoTuChuoi(String input) {
        String numberStr = input.replaceAll("[^0-9]", "");
        return numberStr;
    }
    public String funcLayNgay() {
        // Lấy ngày giờ hiện tại ở múi giờ mặc định
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Chuyển sang múi giờ Việt Nam
        ZoneId vietnamTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamDateTime = currentDateTime.atZone(vietnamTimeZone);

        // Định dạng ngày giờ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDateTime = vietnamDateTime.format(formatter);

        return formattedDateTime;
    }
    public String funcLayGio() {
        // Lấy ngày giờ hiện tại ở múi giờ mặc định
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Chuyển sang múi giờ Việt Nam
        ZoneId vietnamTimeZone = ZoneId.of("Asia/Ho_Chi_Minh");
        ZonedDateTime vietnamDateTime = currentDateTime.atZone(vietnamTimeZone);

        // Định dạng ngày giờ
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedDateTime = vietnamDateTime.format(formatter);

        return formattedDateTime;
    }
}