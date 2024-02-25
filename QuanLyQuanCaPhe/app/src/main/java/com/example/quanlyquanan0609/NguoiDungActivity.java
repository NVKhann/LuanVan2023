package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.icu.text.Collator;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.example.quanlyquanan0609.Adapter.MonAnAdapter;
import com.example.quanlyquanan0609.Adapter.NguoiDungAdapter;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.Class.NguoiDungClass;
import com.example.quanlyquanan0609.databinding.ActivityNguoiDungBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

public class NguoiDungActivity extends AppCompatActivity {
    ActivityNguoiDungBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    TabLayout tabLayout;
    String title = "Nhân viên";
    RecyclerView recyclerView;
    ArrayList<NguoiDungClass> list;
    NguoiDungAdapter nguoiDungAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNguoiDungBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        toolbar = binding.toolbar;
        tabLayout = binding.tlNguoiDung;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabIndex = tab.getPosition(); // Lấy vị trí của TabItem được chọn
                if(tabIndex == 0){
                    funcHienNguoiDung("Nhân viên");
                    title = "Nhân viên";
                }else {
                    funcHienNguoiDung("Khách hàng");
                    title = "Khách hàng";
                }


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        funcDiemSoLuong();

    }

    private void funcDiemSoLuong() {
        TabLayout.Tab tabNhanVien = tabLayout.getTabAt(0);
        TabLayout.Tab tabKhachHang = tabLayout.getTabAt(1);
        recyclerView = binding.rvListMonAn;
        recyclerView.setHasFixedSize(true);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int countNV = 0;
                int countKH = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    if(nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        countKH = countKH + 1;
                    }else {
                        countNV = countNV + 1;
                    }
                }
                tabNhanVien.setText("Nhân viên (" + countNV + ")");
                tabKhachHang.setText("Khách hàng (" + countKH + ")");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienNguoiDung(String loai) {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        list = new ArrayList<>();
        nguoiDungAdapter = new NguoiDungAdapter(NguoiDungActivity.this, list);
        recyclerView.setAdapter(nguoiDungAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NguoiDung");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list != null){
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    NguoiDungClass nguoiDungClass = dataSnapshot.getValue(NguoiDungClass.class);
                    if(loai.equals("Khách hàng") && nguoiDungClass.getNd_Quyen().equals(loai)){
                        list.add(nguoiDungClass);
                    }else if(loai.equals("Nhân viên") && !nguoiDungClass.getNd_Quyen().equals("Khách hàng")){
                        list.add(nguoiDungClass);
                    }
                }
                nguoiDungAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_them, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menuThem){
            Intent intent = new Intent(NguoiDungActivity.this, ThemTaiKhoanActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        funcHienNguoiDung(title);
        funcDiemSoLuong();
    }
}