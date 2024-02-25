package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.quanlyquanan0609.Adapter.HoaDonKhachHangAdapter;
import com.example.quanlyquanan0609.Class.HoaDonClass;
import com.example.quanlyquanan0609.Class.PGMClass;
import com.example.quanlyquanan0609.databinding.ActivityHoaDonKhachHangBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class HoaDonKhachHangActivity extends AppCompatActivity {
    ActivityHoaDonKhachHangBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHoaDonKhachHangBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbar;
        progressBar = binding.progressBar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        funcLayMaHD();
    }

    private void funcLayMaHD() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("PhieuGoiMon");
        databaseReference.orderByChild("nd_Ma").equalTo(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<PGMClass> list = new ArrayList<>();
                Integer count = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    PGMClass pgmClass = dataSnapshot.getValue(PGMClass.class);
                    count = count + 1;
                    list.add(pgmClass);
                }
                funcHienThiDanhSach(list);
                binding.tvHDKHSlHD.setText("Tổng " + count + " hóa đơn");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiDanhSach(ArrayList<PGMClass> listPGM) {
        progressBar.setVisibility(View.VISIBLE);
        RecyclerView recyclerView = binding.rvLSTT;
        ArrayList<HoaDonClass> list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        HoaDonKhachHangAdapter adapter = new HoaDonKhachHangAdapter(this, list);
        recyclerView.setAdapter(adapter);
        for (PGMClass pgmClass : listPGM){
            Log.i("PGM", "funcHienThiDanhSach: " + pgmClass.getPgm_Ma());
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("HoaDon");
            databaseReference.orderByChild("pgm_Ma").equalTo(pgmClass.getPgm_Ma()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HoaDonClass hoaDonClass = dataSnapshot.getValue(HoaDonClass.class);
                        if(hoaDonClass.getHd_TrangThai().equals("0")){
                            list.add(hoaDonClass);
                        }
                    }
                    Collections.sort(list, new Comparator<HoaDonClass>() {
                        @Override
                        public int compare(HoaDonClass hoaDon1, HoaDonClass hoaDon2) {
                            return hoaDon2.getHd_Ma().compareTo(hoaDon1.getHd_Ma());
                        }
                    });
                    adapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        adapter.notifyDataSetChanged();

    }
}