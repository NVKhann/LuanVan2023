package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.CtcCaAdapter;
import com.example.quanlyquanan0609.Adapter.HienThiChiTietCaAdapter;
import com.example.quanlyquanan0609.Class.CaLamViecClass;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.KhuVucClass;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.databinding.ActivityChiTietCaBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChiTietCaActivity extends AppCompatActivity {
    ActivityChiTietCaBinding binding;
    ProgressBar progressBar;
    Toolbar toolbar;
    String spinKhuVuc = "Khu vực A";
    String spinTuan = "Tuần này";
    String acNguon = "";
    Date currentDate = new Date();
    // Định dạng ngày tháng
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Calendar calendar = Calendar.getInstance();
    final int nam = calendar.get(Calendar.YEAR);
    final int thang = calendar.get(Calendar.MONTH);
    final int ngay = calendar.get(Calendar.DAY_OF_MONTH);
    int I1 = nam, I2 = thang, I3 = ngay;
    int Nam = nam, Thang = thang, Ngay = ngay;
    // Chuyển đổi thành chuỗi ngày tháng
    String ngaHienTai = dateFormat.format(currentDate);
    String ngayDK = dateFormat.format(currentDate);

    HienThiChiTietCaAdapter hienThiChiTietCaAdapter;
    ArrayList<CaLamViecClass> list;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChiTietCaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        progressBar = binding.progressBar;
        toolbar = binding.toolbar;
        acNguon = getIntent().getStringExtra("TrangThai");
        setSupportActionBar(toolbar);
        if(acNguon.equals("HienThi")){
            toolbar.setTitle("Danh sách đăng ký ca");
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        funcHienThiSpinnerKV();
        binding.spinKhuVuc.setSelection(0);
        binding.tvtvNgayDK.setText(ngaHienTai);
        binding.spinKhuVuc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinKhuVuc = adapterView.getItemAtPosition(i).toString();
                funcLayMaKhuVucTheoTenHienThi(spinKhuVuc, ngayDK);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        binding.tvtvNgayDK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        ChiTietCaActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        i1 = i1 + 1;
                        String ngay = String.format("%02d/%02d/%d", i2, i1, i);
                        binding.tvtvNgayDK.setText(ngay);
                        ngayDK = ngay;
                        funcLayMaKhuVucTheoTenHienThi(spinKhuVuc, ngayDK);
                        I1 = i;
                        I2 = i1 - 1;
                        I3 = i2;

                    }
                }, I1, I2, I3);
                datePickerDialog.show();
            }
        });

    }
    private void funcLayMaKhuVucTheoTenHienThi(String spinKhuVuc, String ngay) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.orderByChild("kv_Ten").equalTo(spinKhuVuc).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer kvMa = null;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    kvMa = khuVucClass.getKv_Ma();
                }
                if(kvMa != null){
                    funcHienThiListCa();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiListCa() {
        recyclerView = binding.rvListCa;
        list = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        hienThiChiTietCaAdapter = new HienThiChiTietCaAdapter(this, list, spinKhuVuc, ngayDK, acNguon);
        recyclerView.setAdapter(hienThiChiTietCaAdapter);
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("CaLamViec");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(list != null){
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    CaLamViecClass caLamViecClass = dataSnapshot.getValue(CaLamViecClass.class);
                    list.add(caLamViecClass);
                }
                Collections.sort(list, new Comparator<CaLamViecClass>() {
                    @Override
                    public int compare(CaLamViecClass caLamViecClass, CaLamViecClass t1) {
                        return caLamViecClass.getCa_Ma().compareTo(t1.getCa_Ma());
                    }
                });
                binding.tvListTrong.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                hienThiChiTietCaAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcHienThiSpinnerKV() {
        List<String> listKhuVuc = new ArrayList<>();
        ArrayAdapter<String> adapterKv = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, listKhuVuc);
        adapterKv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("KhuVuc");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    KhuVucClass khuVucClass = dataSnapshot.getValue(KhuVucClass.class);
                    if(khuVucClass.getKv_TrangThai().equals("Hiển thị")){
                        listKhuVuc.add(khuVucClass.getKv_Ten());
                    }
                    adapterKv.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        binding.spinKhuVuc.setAdapter(adapterKv);

    }
    private int getIndex(Spinner spinner, String item) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(item)) {
                return i;
            }
        }
        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        funcLayMaKhuVucTheoTenHienThi(spinKhuVuc, ngayDK);
    }
}