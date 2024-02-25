package com.example.quanlyquanan0609;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.quanlyquanan0609.databinding.ActivityThongKeBinding;

public class ThongKeActivity extends AppCompatActivity {
    ActivityThongKeBinding binding;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityThongKeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.cardTKHoaDon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThongKeActivity.this, ThongKeHoaDonActivity.class);
                startActivity(intent);
            }
        });
        binding.cardTKDoanhThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThongKeActivity.this, ThongKeDoanhThuActivity.class);
                startActivity(intent);
            }
        });
//        binding.cardTKDTMonAn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ThongKeActivity.this, ThongKeDoanhThuMonAnActivity.class);
//                startActivity(intent);
//            }
//        });
        binding.cardDSCaNV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ThongKeActivity.this, ThongKeCaTheoNhanVienActivity.class);
                startActivity(intent);
            }
        });
    }
}