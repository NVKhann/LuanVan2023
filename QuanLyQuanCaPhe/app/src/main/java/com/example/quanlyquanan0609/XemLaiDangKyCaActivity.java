package com.example.quanlyquanan0609;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.quanlyquanan0609.Adapter.DanhSachDangKyCaNhanVienAdapter;
import com.example.quanlyquanan0609.Class.ChiTietCaClass;
import com.example.quanlyquanan0609.Class.MonAnClass;
import com.example.quanlyquanan0609.databinding.ActivityXemLaiDangKyCaBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.HashSet;
import java.util.Set;

public class XemLaiDangKyCaActivity extends AppCompatActivity {
    ActivityXemLaiDangKyCaBinding binding;
    Toolbar toolbar;
    ProgressBar progressBar;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdfNgay = new SimpleDateFormat("dd/MM/yyyy");
    SimpleDateFormat sdfThang = new SimpleDateFormat("MM/yyyy");


    final int nam = calendar.get(Calendar.YEAR);
    final int thang = calendar.get(Calendar.MONTH);
    final int ngay = calendar.get(Calendar.DAY_OF_MONTH);
    int year = nam, month = thang, day = ngay;
    String thangTk = String.format("%02d/%d", thang+1, nam);
    String TAG = "Ngay";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityXemLaiDangKyCaBinding.inflate(getLayoutInflater());
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
        binding.tvDSDKCaThang.setText(thangTk);
        binding.tvDSDKCaThang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcTaoDatePickerDialog();
            }
        });
        funcHienThiDanhSach(thangTk);
    }


    private void funcHienThiDanhSach(String thangTk) {
        RecyclerView recyclerView = binding.rvDanhSachCaNV;
        ArrayList<ChiTietCaClass> list = new ArrayList<>();
        DanhSachDangKyCaNhanVienAdapter danhSachAdapter;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        danhSachAdapter = new DanhSachDangKyCaNhanVienAdapter(this, list);
        recyclerView.setAdapter(danhSachAdapter);
        progressBar.setVisibility(View.VISIBLE);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String uId = firebaseUser.getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("ChiTietCLV");
        databaseReference.orderByChild("nd_Ma").equalTo(uId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Set<String> uniqueDates = new HashSet<>();
                if(list != null){
                    list.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    ChiTietCaClass chiTietCaClass = dataSnapshot.getValue(ChiTietCaClass.class);
                    String ngayDK = chiTietCaClass.getCt_Ngay();
                    try {
                        Date dNgayDK = sdfNgay.parse(ngayDK);
                        Calendar calendarToCheck = Calendar.getInstance();
                        calendarToCheck.setTime(dNgayDK);

                        Date dThangTK = sdfThang.parse(thangTk);
                        Calendar calendarMonthYear = Calendar.getInstance();
                        calendarMonthYear.setTime(dThangTK);
                        if (calendarToCheck.get(Calendar.MONTH) + 1 == calendarMonthYear.get(Calendar.MONTH) + 1 && calendarToCheck.get(Calendar.YEAR) == calendarMonthYear.get(Calendar.YEAR)) {
                            if (!uniqueDates.contains(ngayDK)) {
                                uniqueDates.add(ngayDK);
                                list.add(chiTietCaClass);
                            }

                        }
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                Collections.sort(list, new Comparator<ChiTietCaClass>() {
                    @Override
                    public int compare(ChiTietCaClass chiTietCaClass, ChiTietCaClass t1) {
                        return t1.getCt_Ngay().compareTo(chiTietCaClass.getCt_Ngay()); // Đảo ngược kết quả trả về
                    }
                });
                progressBar.setVisibility(View.GONE);
                binding.tvXDKCTrong.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
                danhSachAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void funcTaoDatePickerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(XemLaiDangKyCaActivity.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_month_year_picker, null);
        builder.setView(dialogView);

        final NumberPicker monthPicker = dialogView.findViewById(R.id.monthPicker);
        final NumberPicker yearPicker = dialogView.findViewById(R.id.yearPicker);

        // Set the range for the month (1-12) and year (e.g., 1900 to 2100) in the NumberPickers
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        yearPicker.setMinValue(2010);
        yearPicker.setMaxValue(nam);
        yearPicker.setWrapSelectorWheel(false);


        monthPicker.setValue(month + 1);
        yearPicker.setValue(year);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int selectedMonth = monthPicker.getValue();
                int selectedYear = yearPicker.getValue();
                String selectedDate = String.format("%02d/%d", selectedMonth, selectedYear);
                binding.tvDSDKCaThang.setText(selectedDate);
                funcHienThiDanhSach(selectedDate);
                thangTk = selectedDate;
                day = ngay;
                month = selectedMonth - 1;
                year = selectedYear;

            }
        });

        builder.setNegativeButton("Cancel", null);

        builder.create().show();
    }
}