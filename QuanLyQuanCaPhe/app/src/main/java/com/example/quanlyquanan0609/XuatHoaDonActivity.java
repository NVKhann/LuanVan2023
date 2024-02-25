package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.HoaDonAdapter;
import com.example.quanlyquanan0609.Adapter.XuatHoaDonAdapter;
import com.example.quanlyquanan0609.Class.BanAnClass;
import com.example.quanlyquanan0609.Class.ChiTietPGMClass;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.databinding.ActivityXuatHoaDonBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

//import vn.zalopay.sdk.ZaloPaySDK;


public class XuatHoaDonActivity extends AppCompatActivity {
    ActivityXuatHoaDonBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    RecyclerView recyclerView;
    ArrayList<ChiTietPGMClass> listChiTiet;
    XuatHoaDonAdapter xuatHoaDonAdapter;
    Integer pgmMa, banMa, soLuong;
    String giamGia, tongTien;
    NumberFormat numberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityXuatHoaDonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        pgmMa = getIntent().getIntExtra("pgmMa", 0);
        banMa = getIntent().getIntExtra("banMa", 0);
        soLuong = getIntent().getIntExtra("soLuong", 0);
        tongTien = getIntent().getStringExtra("tongTien");
        giamGia = getIntent().getStringExtra("giamGia");
        numberFormat = new DecimalFormat("#,###.##");
        toolbar = binding.toolbar;
        progressBar = binding.progressBar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcKiemTraQuyen();
            }
        });
        funcHienThiThongTin(pgmMa);
        funcHienThiDanhSach(pgmMa);
        funcLayUId(pgmMa);
    }

    private void funcLayUId(Integer pgmMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    funcLayThongTinNguoiDung(pgmClass.getNd_Ma());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayThongTinNguoiDung(String ndMa) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByKey().equalTo(ndMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    if(!nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        binding.tvXhdKhachHang.setText("Khách lẻ");
                        binding.tvXhdNhanVien.setText(nguoiDungClass.getNd_Ten());
                    }else {
                        binding.tvXhdKhachHang.setText(nguoiDungClass.getNd_Ten());
                        funcLayNhanVien();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayNhanVien() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByKey().equalTo(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    if(nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        binding.lnXHDNhanVien.setVisibility(View.GONE);
                    }else {
                        binding.tvXhdNhanVien.setText(nguoiDungClass.getNd_Ten());
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiThongTin(Integer pgmMa) {
        binding.tvXhdTongTien.setText(tongTien);
        binding.tvXhdGiamGia.setText(giamGia);
        binding.tvXhdTongSl.setText(String.valueOf(soLuong));
        DatabaseReference dataHoaDon = FirebaseDatabase.getInstance().getReference("HoaDon");
        dataHoaDon.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                    binding.tvXhdMaHD.setText("HD" + hoaDonClass.getHd_Ma());
                    binding.tvXhdNgay.setText("Ngày: " + hoaDonClass.getHd_Ngay());
                    binding.tvXhdGioVao.setText(hoaDonClass.getHd_GioVao());
                    binding.tvXhdGioRa.setText(hoaDonClass.getHd_GioRa());
                    binding.tvXhdTongTT.setText(numberFormat.format(hoaDonClass.getHd_TongTien()));
                    binding.tvXhdHThuc.setText(hoaDonClass.getHd_HinhThuc().toUpperCase());

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference dataBanAn = FirebaseDatabase.getInstance().getReference("BanAn");
        dataBanAn.orderByChild("ban_Ma").equalTo(banMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    BanAnClass banClass = dataSnapshot.getValue(BanAnClass.class);
                    funcLayKhuVucTheoMaBan(banClass.getKv_Ma(), banClass.getBan_Ten());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcLayKhuVucTheoMaBan(Integer kvMa, String banTen) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ma").equalTo(kvMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    binding.tvXhdBan.setText(khuVucClass.getKv_Ten() + " - " + banTen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiDanhSach(Integer pgmMa) {
        recyclerView = binding.rvXuatHoaDon;
        listChiTiet = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        xuatHoaDonAdapter = new XuatHoaDonAdapter(this, listChiTiet);
        recyclerView.setAdapter(xuatHoaDonAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietPGM");
        databaseReference.orderByChild("pgm_Ma").equalTo(pgmMa).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(listChiTiet != null){
                    listChiTiet.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietPGMClass chiTietPGMClass = dataSnapshot.getValue(ChiTietPGMClass.class);
                    listChiTiet.add(chiTietPGMClass);
                }
                xuatHoaDonAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByChild("nd_Ma").equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ndQuyen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    ndQuyen = nguoiDungClass.getNd_Quyen();
                }
                if (ndQuyen.equals("Khách hàng")){
                    menu.findItem(R.id.menuXHDIn).setVisible(false);
                    toolbar.setTitle("Hóa đơn");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_xuat_hoa_don, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuXHDTai){
            funcLuuHoaDon();
        }else if(item.getItemId() == R.id.menuXHDIn){
            Toast.makeText(XuatHoaDonActivity.this, "Đang cập nhật...", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);

    }

    private void funcLuuHoaDon() {
        LinearLayout lnHoaDon = binding.lnHoaDon;
        Bitmap bitmap = Bitmap.createBitmap(
                lnHoaDon.getWidth(),
                lnHoaDon.getHeight(),
                Bitmap.Config.ARGB_8888
        );
        lnHoaDon.draw(new Canvas(bitmap));
        saveBitMap(bitmap);
        Toast.makeText(XuatHoaDonActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
        funcKiemTraQuyen();
    }

    private void funcKiemTraQuyen() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.orderByKey().equalTo(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String quyen = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    quyen = nguoiDungClass.getNd_Quyen();
                }
                if(quyen.equals("Khách hàng")){
                    Intent intent = new Intent(XuatHoaDonActivity.this, KhachHangActivity.class);
                    startActivity(intent);
                    finish();
                }else if(quyen.equals("Nhân viên")){
                    Intent intent = new Intent(XuatHoaDonActivity.this, MainNhanVienActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(XuatHoaDonActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void saveBitMap(Bitmap bitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String hdMa = binding.tvXhdMaHD.getText().toString();
        String fileName = hdMa + ".png";
        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}