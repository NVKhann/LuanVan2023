package com.example.quanlyquanan0609;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.quanlyquanan0609.databinding.ActivityCaLamViecBinding;

public class CaLamViecActivity extends AppCompatActivity {
    ActivityCaLamViecBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCaLamViecBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        binding.cardCVL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaLamViecActivity.this, HienThiCaLamViecActivity.class);
                startActivity(intent);
            }
        });
        binding.cardChiTietCLV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaLamViecActivity.this, ChiTietCaActivity.class);
                intent.putExtra("TrangThai", "HienThi");
                startActivity(intent);
            }
        });
        binding.cardQrDiemDanh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CaLamViecActivity.this, QrCodeDiemDanhActivity.class);
                startActivity(intent);
            }
        });
    }
}